package top.zbeboy.isy.web.data.student

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
import top.zbeboy.isy.domain.tables.pojos.Role
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.domain.tables.pojos.UsersKey
import top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo
import top.zbeboy.isy.elastic.pojo.StudentElastic
import top.zbeboy.isy.glue.data.StudentGlue
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.DesService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.platform.UsersKeyService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersUniqueInfoService
import top.zbeboy.isy.service.system.MailService
import top.zbeboy.isy.service.util.*
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.platform.common.RoleMethodControllerCommon
import top.zbeboy.isy.web.platform.common.UsersMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.register.student.StudentVo
import java.sql.Timestamp
import java.text.ParseException
import java.time.Clock
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.validation.Valid

/**
 * Created by zbeboy 2017-12-11 .
 **/
@Controller
open class StudentController {

    private val log = LoggerFactory.getLogger(StudentController::class.java)

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var mailService: MailService

    @Autowired
    open lateinit var isyProperties: ISYProperties

    @Resource
    open lateinit var studentGlue: StudentGlue

    @Resource
    open lateinit var desService: DesService

    @Resource
    open lateinit var usersKeyService: UsersKeyService

    @Resource
    open lateinit var usersUniqueInfoService: UsersUniqueInfoService

    @Resource
    open lateinit var roleMethodControllerCommon: RoleMethodControllerCommon

    @Resource
    open lateinit var usersMethodControllerCommon: UsersMethodControllerCommon

    /**
     * 判断学号是否已被注册
     *
     * @param studentNumber 学号
     * @return true未被注册 false已被注册
     */
    @RequestMapping(value = ["/user/register/valid/student"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validStudent(@RequestParam("studentNumber") studentNumber: String): AjaxUtils<*> {
        val student = studentService.findByStudentNumber(StringUtils.trimWhitespace(studentNumber))
        return if (!ObjectUtils.isEmpty(student)) {
            AjaxUtils.of<Any>().fail()
        } else AjaxUtils.of<Any>().success()
    }

    /**
     * 已登录用户学号更新检验
     *
     * @param username      用户账号
     * @param studentNumber 学号
     * @return true 可以用 false 不可以
     */
    @RequestMapping(value = ["/anyone/users/valid/student"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validAnyoneStudent(@RequestParam("username") username: String, @RequestParam("studentNumber") studentNumber: String): AjaxUtils<*> {
        val studentRecords = studentService.findByStudentNumberNeUsername(username, studentNumber)
        return if (studentRecords.isEmpty()) {
            AjaxUtils.of<Any>().success()
        } else AjaxUtils.of<Any>().fail()
    }

    /**
     * 学生注册
     *
     * @param studentVo     学生
     * @param bindingResult 检验
     * @param session       session
     * @param request       请求
     * @return true 成功 false失败
     */
    @RequestMapping(value = ["/user/register/student"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun registerStudent(@Valid studentVo: StudentVo, bindingResult: BindingResult, session: HttpSession, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val email = StringUtils.trimWhitespace(studentVo.email!!)
            val mobile = StringUtils.trimWhitespace(studentVo.mobile!!)
            if (!ObjectUtils.isEmpty(session.getAttribute("mobile"))) {
                val tempMobile = session.getAttribute("mobile") as String
                if (studentVo.mobile != tempMobile) {
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
                                if (studentVo.phoneVerifyCode != mobileCode) {
                                    ajaxUtils.fail().msg("验证码错误")
                                } else {
                                    val password = StringUtils.trimWhitespace(studentVo.password!!)
                                    val confirmPassword = StringUtils.trimWhitespace(studentVo.confirmPassword!!)
                                    if (password != confirmPassword) {
                                        ajaxUtils.fail().msg("密码不一致")
                                    } else {
                                        // 注册成功
                                        val saveUsers = Users()
                                        val saveStudent = StudentElastic()
                                        val enabled: Byte = 1
                                        saveUsers.username = email
                                        saveUsers.enabled = enabled
                                        saveStudent.enabled = enabled
                                        saveUsers.mobile = mobile
                                        saveStudent.mobile = mobile
                                        saveUsers.password = BCryptUtils.bCryptPassword(password)
                                        saveUsers.usersTypeId = cacheManageService.findByUsersTypeName(Workbook.STUDENT_USERS_TYPE).usersTypeId
                                        saveUsers.joinDate = java.sql.Date(Clock.systemDefaultZone().millis())
                                        saveStudent.joinDate = saveUsers.joinDate

                                        var dateTime = DateTime.now()
                                        dateTime = dateTime.plusDays(Workbook.MAILBOX_VERIFY_VALID)
                                        val mailboxVerifyCode = RandomUtils.generateEmailCheckKey()
                                        saveUsers.mailboxVerifyCode = mailboxVerifyCode
                                        saveUsers.mailboxVerifyValid = Timestamp(dateTime.toDate().time)
                                        saveUsers.langKey = request.locale.toLanguageTag()
                                        saveStudent.langKey = saveUsers.langKey
                                        saveUsers.avatar = Workbook.USERS_AVATAR
                                        saveStudent.avatar = saveUsers.avatar
                                        saveUsers.verifyMailbox = 0
                                        saveUsers.realName = studentVo.realName
                                        saveStudent.realName = saveUsers.realName
                                        usersService.save(saveUsers)

                                        saveStudent.schoolId = studentVo.school
                                        saveStudent.schoolName = studentVo.schoolName
                                        saveStudent.collegeId = studentVo.college
                                        saveStudent.collegeName = studentVo.collegeName
                                        saveStudent.departmentId = studentVo.department
                                        saveStudent.departmentName = studentVo.departmentName
                                        saveStudent.scienceId = studentVo.science
                                        saveStudent.scienceName = studentVo.scienceName
                                        saveStudent.grade = studentVo.grade
                                        saveStudent.organizeName = studentVo.organizeName
                                        saveStudent.organizeId = studentVo.organize
                                        saveStudent.studentNumber = studentVo.studentNumber
                                        saveStudent.username = email
                                        studentService.save(saveStudent)

                                        // 为该用户产生一个密钥KEY
                                        val usersKey = UsersKey()
                                        usersKey.username = desService.encrypt(email, isyProperties.getSecurity().desDefaultKey!!)
                                        usersKey.userKey = UUIDUtils.getUUID()
                                        usersKeyService.save(usersKey)

                                        // 清空session
                                        session.removeAttribute("mobileExpiry")
                                        session.removeAttribute("mobile")
                                        session.removeAttribute("mobileCode")

                                        //发送验证邮件
                                        if (isyProperties.getMail().isOpen) {
                                            mailService.sendValidEmailMail(saveUsers, RequestUtils.getBaseUrl(request))
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
     * 学生数据
     *
     * @return 学生数据页面
     */
    @RequestMapping(value = ["/web/menu/data/student"], method = [(RequestMethod.GET)])
    fun studentData(): String {
        return "web/data/student/student_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/student/pass/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun studentPassDatas(request: HttpServletRequest): DataTablesUtils<StudentBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("real_name")
        headers.add("student_number")
        headers.add("username")
        headers.add("mobile")
        headers.add("id_card")
        headers.add("role_name")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("department_name")
        headers.add("science_name")
        headers.add("grade")
        headers.add("organize_name")
        headers.add("sex")
        headers.add("birthday")
        headers.add("nation_name")
        headers.add("politicalLandscape_name")
        headers.add("dormitory_number")
        headers.add("place_origin")
        headers.add("parent_name")
        headers.add("parent_contact_phone")
        headers.add("family_residence")
        headers.add("enabled")
        headers.add("lang_key")
        headers.add("join_date")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<StudentBean>(request, headers)
        val resultUtils = studentGlue.findAllByPageExistsAuthorities(dataTablesUtils)
        dataTablesUtils.data = resultUtils.getData()
        dataTablesUtils.setiTotalRecords(studentGlue.countAllExistsAuthorities())
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements())
        return dataTablesUtils
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/data/student/wait/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun studentWaitDatas(request: HttpServletRequest): DataTablesUtils<StudentBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("real_name")
        headers.add("student_number")
        headers.add("username")
        headers.add("mobile")
        headers.add("school_name")
        headers.add("college_name")
        headers.add("department_name")
        headers.add("science_name")
        headers.add("grade")
        headers.add("organize_name")
        headers.add("lang_key")
        headers.add("join_date")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<StudentBean>(request, headers)
        val resultUtils = studentGlue.findAllByPageNotExistsAuthorities(dataTablesUtils)
        dataTablesUtils.data = resultUtils.getData()
        dataTablesUtils.setiTotalRecords(studentGlue.countAllNotExistsAuthorities())
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements())
        return dataTablesUtils
    }

    /**
     * 用户角色数据
     *
     * @param username 用户账号
     * @return 数据
     */
    @RequestMapping(value = ["/web/data/student/role/data"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun roleData(@RequestParam("username") username: String): AjaxUtils<Role> {
        return AjaxUtils.of<Role>().success().listData(roleMethodControllerCommon.getRoleData(username))
    }

    /**
     * 保存用户角色
     *
     * @param username 用户账号
     * @param roles    角色
     * @param request  请求
     * @return true 成功 false 角色为空
     */
    @RequestMapping(value = ["/web/data/student/role/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun roleSave(@RequestParam("username") username: String, @RequestParam("roles") roles: String, request: HttpServletRequest): AjaxUtils<*> {
        return roleMethodControllerCommon.roleSave(username, roles, request)
    }

    /**
     * 更新用户状态
     *
     * @param userIds ids
     * @param enabled 状态
     * @return true 成功 false 失败
     */
    @RequestMapping("/web/data/student/users/update/enabled")
    @ResponseBody
    fun usersUpdateEnabled(userIds: String, enabled: Byte?): AjaxUtils<*> {
        return usersMethodControllerCommon.usersUpdateEnabled(userIds, enabled)
    }

    /**
     * 删除无角色关联的用户
     *
     * @param userIds 用户账号
     * @return true 成功 false 失败
     */
    @RequestMapping("/web/data/student/users/deletes")
    @ResponseBody
    fun deleteUsers(@RequestParam("username") userIds: String): AjaxUtils<*> {
        return usersMethodControllerCommon.deleteUsers(userIds)
    }

    /**
     * 更新用户学校信息
     *
     * @param organize 班级id
     * @return true 更新成功 false 更新失败
     */
    @RequestMapping(value = ["/anyone/users/profile/student/school/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun studentSchoolUpdate(@RequestParam("organize") organize: Int): AjaxUtils<*> {
        val users = usersService.getUserFromSession()
        val student = studentService.findByUsername(users!!.username)
        student.organizeId = organize
        studentService.update(student, null)
        return AjaxUtils.of<Any>().success().msg("更新学校信息成功")
    }

    /**
     * 更新基本信息
     *
     * @param studentVo     学生信息
     * @param bindingResult 检验
     * @return true or false
     */
    @RequestMapping(value = ["/anyone/users/student/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun studentUpdate(@Valid studentVo: top.zbeboy.isy.web.vo.platform.users.StudentVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            try {
                val users = usersService.findByUsername(studentVo.username!!)
                val realName = studentVo.realName
                val avatar = studentVo.avatar
                if (StringUtils.hasLength(realName)) {
                    users!!.realName = realName
                } else {
                    users!!.realName = null
                }
                if (StringUtils.hasLength(avatar)) {
                    users.avatar = studentVo.avatar
                } else {
                    users.avatar = Workbook.USERS_AVATAR
                }
                usersService.update(users)

                val usersKey = cacheManageService.getUsersKey(studentVo.username!!)
                val student = studentService.findByUsername(studentVo.username!!)
                student.studentNumber = studentVo.studentNumber
                student.nationId = studentVo.nationId
                student.politicalLandscapeId = studentVo.politicalLandscapeId
                if (StringUtils.hasLength(studentVo.birthday)) {
                    student.birthday = desService.encrypt(DateTimeUtils.formatDate(studentVo.birthday!!).toString(), usersKey)
                } else {
                    student.birthday = null
                }

                if (StringUtils.hasLength(studentVo.sex)) {
                    student.sex = desService.encrypt(studentVo.sex!!, usersKey)
                } else {
                    student.sex = null
                }

                if (StringUtils.hasLength(studentVo.familyResidence)) {
                    student.familyResidence = desService.encrypt(studentVo.familyResidence!!, usersKey)
                } else {
                    student.familyResidence = null
                }

                if (StringUtils.hasLength(studentVo.dormitoryNumber)) {
                    student.dormitoryNumber = desService.encrypt(studentVo.dormitoryNumber!!, usersKey)
                } else {
                    student.dormitoryNumber = null
                }

                if (StringUtils.hasLength(studentVo.parentName)) {
                    student.parentName = desService.encrypt(studentVo.parentName!!, usersKey)
                } else {
                    student.parentName = null
                }

                if (StringUtils.hasLength(studentVo.parentContactPhone)) {
                    student.parentContactPhone = desService.encrypt(studentVo.parentContactPhone!!, usersKey)
                } else {
                    student.parentContactPhone = null
                }

                if (StringUtils.hasLength(studentVo.placeOrigin)) {
                    student.placeOrigin = desService.encrypt(studentVo.placeOrigin!!, usersKey)
                } else {
                    student.placeOrigin = null
                }

                val key = isyProperties.getSecurity().desDefaultKey
                val usersUniqueInfo = UsersUniqueInfo()
                usersUniqueInfo.username = desService.encrypt(studentVo.username!!, key!!)
                if (StringUtils.hasLength(studentVo.idCard)) {
                    usersUniqueInfo.idCard = desService.encrypt(studentVo.idCard!!, key)
                } else {
                    usersUniqueInfo.idCard = null
                }
                usersUniqueInfoService.saveOrUpdate(usersUniqueInfo)

                studentService.update(student, usersUniqueInfo)
                return AjaxUtils.of<Any>().success()
            } catch (e: ParseException) {
                log.error("Birthday to sql date is exception : {}", e.message)
                return AjaxUtils.of<Any>().fail().msg("时间转换异常")
            }

        }
        return AjaxUtils.of<Any>().fail().msg("参数检验错误")
    }
}