package top.zbeboy.isy.web.data.staff

import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.elastic.pojo.StaffElastic
import top.zbeboy.isy.glue.data.StaffGlue
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.MailService
import top.zbeboy.isy.service.util.BCryptUtils
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.RandomUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.register.staff.StaffVo
import java.sql.Timestamp
import java.text.ParseException
import java.time.Clock
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-09 .
 **/
@Controller
open class StaffController {

    private val log = LoggerFactory.getLogger(StaffController::class.java)

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var mailService: MailService

    @Autowired
    open lateinit var isyProperties: ISYProperties

    @Autowired
    open lateinit var requestUtils: RequestUtils

    @Resource
    open lateinit var staffGlue: StaffGlue


    /**
     * 判断工号是否已被注册
     *
     * @param staffNumber 工号
     * @return true未被注册 false已被注册
     */
    @RequestMapping(value = ["/user/register/valid/staff"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validStaff(@RequestParam("staffNumber") staffNumber: String): AjaxUtils<*> {
        val staff = staffService.findByStaffNumber(staffNumber)
        return if (!ObjectUtils.isEmpty(staff)) {
            AjaxUtils.of<Any>().fail()
        } else AjaxUtils.of<Any>().success()
    }

    /**
     * 已登录用户工号更新检验
     *
     * @param username    用户账号
     * @param staffNumber 工号
     * @return true 可以用 false 不可以
     */
    @RequestMapping(value = ["/anyone/users/valid/staff"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validAnyoneStudent(@RequestParam("username") username: String, @RequestParam("staffNumber") staffNumber: String): AjaxUtils<*> {
        val staffRecords = staffService.findByStaffNumberNeUsername(username, staffNumber)
        return if (staffRecords.isEmpty()) {
            AjaxUtils.of<Any>().success()
        } else AjaxUtils.of<Any>().fail()
    }

    /**
     * 教职工注册
     *
     * @param staffVo       教职工
     * @param bindingResult 检验
     * @param session       session
     * @param request       请求
     * @return true 注册成功 false注册失败
     */
    @RequestMapping(value = ["/user/register/staff"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun registerStaff(@Valid staffVo: StaffVo, bindingResult: BindingResult, session: HttpSession, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val email = StringUtils.trimWhitespace(staffVo.email)
            val mobile = StringUtils.trimWhitespace(staffVo.mobile)
            if (!ObjectUtils.isEmpty(session.getAttribute("mobile"))) {
                val tempMobile = session.getAttribute("mobile") as String
                if (staffVo.mobile != tempMobile) {
                    ajaxUtils.fail().msg("发现手机号不一致，请重新获取验证码")
                } else {
                    if (!ObjectUtils.isEmpty(session.getAttribute("mobileExpiry"))) {
                        val mobileExpiry = session.getAttribute("mobileExpiry") as Date
                        val now = Date()
                        if (!now.before(mobileExpiry)) {
                            ajaxUtils.fail().msg("验证码已过有效期(30分钟)")
                        } else {
                            if (!ObjectUtils.isEmpty(session.getAttribute("mobileCode"))) {
                                val mobileCode = session.getAttribute("mobileCode") as String
                                if (staffVo.phoneVerifyCode != mobileCode) {
                                    ajaxUtils.fail().msg("验证码错误")
                                } else {
                                    val password = StringUtils.trimWhitespace(staffVo.password)
                                    val confirmPassword = StringUtils.trimWhitespace(staffVo.confirmPassword)
                                    if (password != confirmPassword) {
                                        ajaxUtils.fail().msg("密码不一致")
                                    } else {
                                        // 注册成功
                                        val saveUsers = Users()
                                        val saveStaff = StaffElastic()
                                        val enabled:Byte = 1
                                        saveUsers.username = email
                                        saveUsers.enabled = enabled
                                        saveStaff.enabled = enabled
                                        saveUsers.mobile = mobile
                                        saveStaff.mobile = mobile
                                        saveUsers.password = BCryptUtils.bCryptPassword(password)
                                        saveUsers.usersTypeId = cacheManageService.findByUsersTypeName(Workbook.STAFF_USERS_TYPE).usersTypeId
                                        saveUsers.joinDate = java.sql.Date(Clock.systemDefaultZone().millis())
                                        saveStaff.joinDate = saveUsers.joinDate

                                        var dateTime = DateTime.now()
                                        dateTime = dateTime.plusDays(Workbook.MAILBOX_VERIFY_VALID)
                                        val mailboxVerifyCode = RandomUtils.generateEmailCheckKey()
                                        saveUsers.mailboxVerifyCode = mailboxVerifyCode
                                        saveUsers.mailboxVerifyValid = Timestamp(dateTime.toDate().time)
                                        saveUsers.langKey = request.locale.toLanguageTag()
                                        saveStaff.langKey = saveUsers.langKey
                                        saveUsers.avatar = Workbook.USERS_AVATAR
                                        saveStaff.avatar = saveUsers.avatar
                                        saveUsers.verifyMailbox = 0
                                        saveUsers.realName = staffVo.realName
                                        saveStaff.realName = saveUsers.realName
                                        usersService.save(saveUsers)

                                        saveStaff.schoolId = staffVo.school
                                        saveStaff.schoolName = staffVo.schoolName
                                        saveStaff.collegeId = staffVo.college
                                        saveStaff.collegeName = staffVo.collegeName
                                        saveStaff.departmentId = staffVo.department
                                        saveStaff.departmentName = staffVo.departmentName
                                        saveStaff.departmentId = staffVo.department
                                        saveStaff.staffNumber = staffVo.staffNumber
                                        saveStaff.username = email
                                        staffService.save(saveStaff)

                                        //清空session
                                        session.removeAttribute("mobile")
                                        session.removeAttribute("mobileExpiry")
                                        session.removeAttribute("mobileCode")

                                        //发送验证邮件
                                        if (isyProperties.getMail().isOpen) {
                                            mailService.sendValidEmailMail(saveUsers, requestUtils.getBaseUrl(request))
                                            ajaxUtils.success().msg("恭喜注册成功，请验证邮箱")
                                        } else {
                                            ajaxUtils.fail().msg("邮件推送已被管理员关闭")
                                        }
                                    }
                                }
                            } else {
                                ajaxUtils.fail().msg("无法获取当前用户电话验证码，请重新获取手机验证码")
                            }
                        }
                    } else {
                        ajaxUtils.fail().msg("无法获取当前用户验证码有效期，请重新获取手机验证码")
                    }
                }
            } else {
                ajaxUtils.fail().msg("无法获取当前用户电话，请重新获取手机验证码")
            }
        } else {
            ajaxUtils.fail().msg("参数异常，请检查输入内容是否正确")
        }
        return ajaxUtils
    }

    /**
     * 教职工数据
     *
     * @return 教职工数据页面
     */
    @RequestMapping(value = ["/web/menu/data/staff"], method = [(RequestMethod.GET)])
    fun staffData(): String {
        return "web/data/staff/staff_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/staff/pass/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun staffPassDatas(request: HttpServletRequest): DataTablesUtils<StaffBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("real_name")
        headers.add("staff_number")
        headers.add("username")
        headers.add("mobile")
        headers.add("id_card")
        headers.add("role_name")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("department_name")
        headers.add("academic_title_name")
        headers.add("post")
        headers.add("sex")
        headers.add("birthday")
        headers.add("nation_name")
        headers.add("politicalLandscape_name")
        headers.add("family_residence")
        headers.add("enabled")
        headers.add("lang_key")
        headers.add("join_date")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<StaffBean>(request, headers)
        val resultUtils = staffGlue.findAllByPageExistsAuthorities(dataTablesUtils)
        dataTablesUtils.data = resultUtils.getData()
        dataTablesUtils.setiTotalRecords(staffGlue.countAllExistsAuthorities())
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements())
        return dataTablesUtils
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/staff/wait/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun staffWaitDatas(request: HttpServletRequest): DataTablesUtils<StaffBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("real_name")
        headers.add("staff_number")
        headers.add("username")
        headers.add("mobile")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("department_name")
        headers.add("lang_key")
        headers.add("join_date")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<StaffBean>(request, headers)
        val resultUtils = staffGlue.findAllByPageNotExistsAuthorities(dataTablesUtils)
        dataTablesUtils.data = resultUtils.getData()
        dataTablesUtils.setiTotalRecords(staffGlue.countAllNotExistsAuthorities())
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements())
        return dataTablesUtils
    }

    /**
     * 更新用户学校信息
     *
     * @param department 系id
     * @return true 更新成功 false 更新失败
     */
    @RequestMapping(value = ["/anyone/users/profile/staff/school/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun staffSchoolUpdate(@RequestParam("department") department: Int): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val users = usersService.getUserFromSession()
        return if(!ObjectUtils.isEmpty(users)){
            val staff = staffService.findByUsername(users!!.username)
            staff.departmentId = department
            staffService.update(staff)
            ajaxUtils.success().msg("更新学校信息成功")
        } else ajaxUtils.fail().msg("未查询到您的信息，请重新登录")
    }

    /**
     * 更新基本信息
     *
     * @param staffVo       教职工信息
     * @param bindingResult 检验
     * @return true or false
     */
    @RequestMapping(value = ["/anyone/users/staff/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun studentUpdate(@Valid staffVo: top.zbeboy.isy.web.vo.platform.users.StaffVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            try {
                val users = usersService.findByUsername(staffVo.username!!)
                val realName = staffVo.realName
                val avatar = staffVo.avatar
                if (StringUtils.hasLength(realName)) {
                    users!!.realName = realName
                } else {
                    users!!.realName = null
                }
                if (StringUtils.hasLength(avatar)) {
                    users.avatar = staffVo.avatar
                } else {
                    users.avatar = Workbook.USERS_AVATAR
                }
                usersService.update(users)

                val staff = staffService.findByUsername(staffVo.username!!)
                staff.staffNumber = staffVo.staffNumber
                staff.sex = staffVo.sex
                staff.nationId = staffVo.nationId
                staff.politicalLandscapeId = staffVo.politicalLandscapeId
                staff.academicTitleId = staffVo.academicTitleId
                if (StringUtils.hasLength(staffVo.birthday)) {
                    staff.birthday = DateTimeUtils.formatDate(staffVo.birthday!!)
                } else {
                    staff.birthday = null
                }
                if (StringUtils.hasLength(staffVo.idCard)) {
                    staff.idCard = staffVo.idCard
                } else {
                    staff.idCard = null
                }
                staff.familyResidence = staffVo.familyResidence
                staff.post = staffVo.post
                staffService.update(staff)
                return AjaxUtils.of<Any>().success()
            } catch (e: ParseException) {
                log.error("Birthday to sql date is exception : {}", e.message)
                return AjaxUtils.of<Any>().fail().msg("时间转换异常")
            }

        }
        return AjaxUtils.of<Any>().fail().msg("参数检验错误")
    }
}