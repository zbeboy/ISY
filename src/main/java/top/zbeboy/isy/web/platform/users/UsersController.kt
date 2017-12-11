package top.zbeboy.isy.web.platform.users

import com.alibaba.fastjson.JSON
import com.octo.captcha.service.CaptchaServiceException
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartHttpServletRequest
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.Role
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.domain.tables.pojos.UsersType
import top.zbeboy.isy.glue.platform.UsersGlue
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.service.system.AuthoritiesService
import top.zbeboy.isy.service.system.MailService
import top.zbeboy.isy.service.system.MobileService
import top.zbeboy.isy.service.util.BCryptUtils
import top.zbeboy.isy.service.util.FilesUtils
import top.zbeboy.isy.service.util.RandomUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.bean.file.FileBean
import top.zbeboy.isy.web.bean.platform.users.UsersBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.jcaptcha.CaptchaServiceSingleton
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.ImageUtils
import top.zbeboy.isy.web.vo.platform.users.AvatarVo
import top.zbeboy.isy.web.vo.platform.users.UsersVo
import top.zbeboy.isy.web.vo.register.reset.ResetVo
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.sql.Timestamp
import java.time.Clock
import java.util.*
import javax.annotation.Resource
import javax.imageio.ImageIO
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

/**
 * Created by zbeboy 2017-11-19 .
 **/
@Controller
open class UsersController {

    private val log = LoggerFactory.getLogger(UsersController::class.java)

    companion object {
        /*
        检验邮箱使用
         */
        @JvmField
        val VALID_EMAIL = 1

        /*
        检验手机号使用
         */
        @JvmField
        val VALID_MOBILE = 2

        /*
        验证码错误码
         */
        @JvmField
        val CAPTCHA_ERROR = 0

        /*
        无效的验证码
         */
        @JvmField
        val CAPTCHA_INVALID = 1
    }

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var authoritiesService: AuthoritiesService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var mailService: MailService

    @Resource
    open lateinit var mobileService: MobileService

    @Resource
    open lateinit var uploadService: UploadService

    @Autowired
    open lateinit var isyProperties: ISYProperties

    @Autowired
    open lateinit var requestUtils: RequestUtils

    @Resource
    open lateinit var usersGlue: UsersGlue

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    /**
     * 检验注册表单
     *
     * @param username  账号(邮箱)
     * @param mobile    手机号
     * @param validType 检验类型：1.邮箱；2.手机号
     * @return true 检验通过 false 不通过
     */
    @RequestMapping(value = ["/user/register/valid/users"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validUsers(username: String?, mobile: String?, validType: Int): AjaxUtils<*> {
        if (validType == VALID_EMAIL) {
            val tempUsers = usersService.findByUsername(StringUtils.trimWhitespace(username))
            return if (!ObjectUtils.isEmpty(tempUsers)) {
                AjaxUtils.of<Any>().fail().msg("该邮箱已被注册")
            } else {
                AjaxUtils.of<Any>().success()
            }
        }

        if (validType == VALID_MOBILE) {
            val tempUsers = usersService.findByMobile(StringUtils.trimWhitespace(mobile))
            return if (!ObjectUtils.isEmpty(tempUsers)) {
                AjaxUtils.of<Any>().fail().msg("该手机号已被注册")
            } else {
                AjaxUtils.of<Any>().success()
            }
        }

        return AjaxUtils.of<Any>().fail().msg("检验类型异常")
    }

    /**
     * 检验注册表单
     *
     * @param username  账号(邮箱)
     * @param mobile    手机号
     * @param validType 检验类型：2.手机号
     * @return true 检验通过 false 不通过
     */
    @RequestMapping(value = ["/anyone/valid/users"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validLoginUsers(@RequestParam("username") username: String, mobile: String, validType: Int): AjaxUtils<*> {
        if (validType == VALID_MOBILE) {
            val tempUsers = usersService.findByMobileNeUsername(StringUtils.trimWhitespace(mobile), StringUtils.trimWhitespace(username))
            return if (!ObjectUtils.isEmpty(tempUsers)) {
                AjaxUtils.of<Any>().fail().msg("该手机号已被注册")
            } else {
                AjaxUtils.of<Any>().success()
            }
        }

        return AjaxUtils.of<Any>().fail().msg("检验类型异常")
    }

    /**
     * 检验当前用户类型是否为学生
     *
     * @return true or false
     */
    @RequestMapping(value = ["/anyone/valid/cur/is/student"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun validIsStudent(): AjaxUtils<*> {
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val student = studentService.findByUsername(users!!.username)
            return AjaxUtils.of<Any>().success().msg("学生用户").obj(student.studentId!!)
        }
        return AjaxUtils.of<Any>().fail().msg("非学生用户")
    }

    /**
     * 检验当前用户类型是否为教职工
     *
     * @return true or false
     */
    @RequestMapping(value = ["/anyone/valid/cur/is/staff"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun validIsStaff(): AjaxUtils<*> {
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STAFF_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val staff = staffService.findByUsername(users!!.username)
            return AjaxUtils.of<Any>().success().msg("教职工用户").obj(staff.staffId!!)
        }
        return AjaxUtils.of<Any>().fail().msg("非教职工用户")
    }

    /**
     * 检验手机验证码
     *
     * @param mobile          手机号
     * @param phoneVerifyCode 验证码
     * @param session         session
     * @return true 通过 false 不通过
     */
    @RequestMapping(value = ["/user/register/valid/mobile"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validMobile(@RequestParam("mobile") mobile: String, @RequestParam("phoneVerifyCode") phoneVerifyCode: String, session: HttpSession): AjaxUtils<*> {
        if (!ObjectUtils.isEmpty(session.getAttribute("mobile"))) {
            val tempMobile = session.getAttribute("mobile") as String
            if (mobile != tempMobile) {
                return AjaxUtils.of<Any>().fail().msg("发现手机号不一致，请重新获取验证码")
            } else {
                if (!ObjectUtils.isEmpty(session.getAttribute("mobileExpiry"))) {
                    val mobileExpiry = session.getAttribute("mobileExpiry") as Date
                    val now = Date()
                    if (!now.before(mobileExpiry)) {
                        return AjaxUtils.of<Any>().fail().msg("验证码已过有效期(30分钟)")
                    } else {
                        if (!ObjectUtils.isEmpty(session.getAttribute("mobileCode"))) {
                            val mobileCode = session.getAttribute("mobileCode") as String
                            return if (phoneVerifyCode != mobileCode) {
                                AjaxUtils.of<Any>().fail().msg("验证码错误")
                            } else {
                                AjaxUtils.of<Any>().success().msg("")
                            }
                        }
                    }
                }
            }
        }
        return AjaxUtils.of<Any>().fail().msg("请输入手机号，并点击获取验证码按钮")
    }

    /**
     * 邮箱邮件验证
     *
     * @param key      验证码
     * @param username 账号
     * @param modelMap 页面对象
     * @return 消息页面
     */
    @RequestMapping("/user/register/mailbox/valid")
    fun validEmail(@RequestParam("key") key: String, @RequestParam("username") username: String, modelMap: ModelMap): String {
        val users = usersService.findByUsername(username)
        if (!ObjectUtils.isEmpty(users)) {
            val mailboxVerifyValid = users!!.mailboxVerifyValid
            val now = Timestamp(Clock.systemDefaultZone().millis())
            if (now.before(mailboxVerifyValid)) {
                if (key == users.mailboxVerifyCode) {
                    users.verifyMailbox = 1
                    usersService.update(users)
                    modelMap.put("msg", "恭喜您注册成功，请等待管理员审核后便可登录，审核结果会很快发至您的邮箱，注意查收。")
                } else {
                    modelMap.put("msg", "您的邮箱验证码有误，验证邮箱失败！")
                }
            } else {
                modelMap.put("msg", "您的邮箱验证已过有效期！")
            }
        } else {
            modelMap.put("msg", "该邮箱未注册！")
        }
        return "msg"
    }

    /**
     * 重新发送验证邮件
     *
     * @param username 用户账号
     * @param request  请求
     * @param modelMap 页面对象
     * @return 消息
     */
    @RequestMapping("/user/register/mailbox/anew")
    fun anewSendVerifyMail(@RequestParam("username") username: String, request: HttpServletRequest, modelMap: ModelMap): String {
        val users = usersService.findByUsername(username)
        if (!ObjectUtils.isEmpty(users)) {
            if (ObjectUtils.isEmpty(users!!.verifyMailbox) || users.verifyMailbox <= 0) {
                var dateTime = DateTime.now()
                dateTime = dateTime.plusDays(Workbook.MAILBOX_VERIFY_VALID)
                val mailboxVerifyCode = RandomUtils.generateEmailCheckKey()
                users.mailboxVerifyCode = mailboxVerifyCode
                users.mailboxVerifyValid = Timestamp(dateTime.toDate().time)
                usersService.update(users)
                //发送验证邮件
                if (isyProperties.getMail().isOpen) {
                    mailService.sendValidEmailMail(users, requestUtils.getBaseUrl(request))
                    modelMap.put("msg", "验证邮件已发送至您邮箱，请登录邮箱进行验证！")
                } else {
                    modelMap.put("msg", "邮件推送已被管理员关闭，暂时无法验证")
                }
            } else {
                modelMap.put("msg", "该邮箱已经验证，请勿重复验证")
            }
        } else {
            modelMap.put("msg", "未查询到用户信息")
        }
        return "msg"
    }

    /**
     * 获取手机验证码
     *
     * @param mobile  手机号
     * @param session session
     * @return 发送验证码到手机
     */
    @RequestMapping(value = ["/user/register/mobile/code"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun mobileCode(@RequestParam("mobile") mobile: String, session: HttpSession): AjaxUtils<*> {
        val regex = "1[0-9]{10}"
        if (mobile.matches(regex.toRegex())) {
            var dateTime = DateTime.now()
            dateTime = dateTime.plusMinutes(Workbook.MOBILE_VERIFY_VALID)
            val mobileKey = RandomUtils.generateMobileKey()
            session.setAttribute("mobile", mobile)
            session.setAttribute("mobileExpiry", dateTime.toDate())
            session.setAttribute("mobileCode", mobileKey)
            mobileService.sendValidMobileShortMessage(mobile, mobileKey)
            return if (isyProperties.getMobile().isOpen) {
                AjaxUtils.of<Any>().success().msg("短信已发送，请您耐心等待(验证码30分钟内有效，无需重复发送)")
            } else {
                AjaxUtils.of<Any>().fail().msg("短信发送已被管理员关闭")
            }
        } else {
            return AjaxUtils.of<Any>().fail().msg("手机号格式不正确")
        }
    }

    /**
     * 获取验证码
     *
     * @param request  请求
     * @param response 响应
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/user/login/jcaptcha")
    @Throws(ServletException::class, IOException::class)
    fun jCaptcha(request: HttpServletRequest, response: HttpServletResponse) {
        val captchaChallengeAsJpeg: ByteArray
        // the output stream to render the captcha image as jpeg into
        val jpegOutputStream = ByteArrayOutputStream()
        try {
            // get the session id that will identify the generated captcha.
            // the same id must be used to validate the response, the session id is a good candidate!
            val captchaId = request.session.id
            // call the ImageCaptchaService getChallenge method
            val challenge = CaptchaServiceSingleton.getInstance().getImageChallengeForID(captchaId, request.locale)
            // a jpeg encoder
            ImageIO.write(challenge, "jpeg", jpegOutputStream)
        } catch (e: IllegalArgumentException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
            log.error(" jcaptcha exception : {} ", e)
            return
        } catch (e: CaptchaServiceException) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            log.error(" jcaptcha exception : {} ", e)
            return
        }

        captchaChallengeAsJpeg = jpegOutputStream.toByteArray()

        // flush it in the response
        response.setHeader("Cache-Control", "no-store")
        response.setHeader("Pragma", "no-cache")
        response.setDateHeader("Expires", 0)
        response.contentType = "image/jpeg"
        val responseOutputStream = response.outputStream

        responseOutputStream.write(captchaChallengeAsJpeg)
        responseOutputStream.flush()
        responseOutputStream.close()
    }

    /**
     * js 检验验证码
     *
     * @param captcha 验证码
     * @param request 请求
     * @return true or false
     */
    @RequestMapping("/user/login/valid/jcaptcha")
    @ResponseBody
    fun validateCaptchaForId(@RequestParam("j_captcha_response") captcha: String, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val isResponseCorrect: Boolean
        // remember that we need an id to validate!
        val captchaId = request.session.id
        log.debug(" valid captchaId : {}", captchaId)
        log.debug(" j_captcha_response : {} ", captcha)
        // call the service method
        try {
            isResponseCorrect = CaptchaServiceSingleton.getInstance().validateResponseForID(captchaId, captcha)
            if (isResponseCorrect) {
                ajaxUtils.success().msg("验证码正确")
            } else {
                ajaxUtils.fail().msg("验证码错误").obj(CAPTCHA_ERROR)
            }
        } catch (e: CaptchaServiceException) {
            // should not happen,may be thrown if the id is not valid
            ajaxUtils.fail().msg("参数无效,请重新输入验证码").obj(CAPTCHA_INVALID)
            log.error(" valid exception : {} ", e)
        }

        return ajaxUtils
    }

    /**
     * 忘记密码邮箱验证
     *
     * @param email 邮箱
     * @return true or false
     */
    @RequestMapping(value = ["/user/login/valid/email"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validEmail(@RequestParam("email") email: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val users = usersService.findByUsername(email)
        if (!ObjectUtils.isEmpty(users)) {
            if (!ObjectUtils.isEmpty(users!!.verifyMailbox) && users.verifyMailbox > 0) {
                ajaxUtils.success().msg("邮箱正常")
            } else {
                ajaxUtils.fail().msg("该邮箱未激活")
            }
        } else {
            ajaxUtils.fail().msg("该邮箱不存在")
        }
        return ajaxUtils
    }

    /**
     * 忘记密码邮件
     *
     * @param email   账号
     * @param request 请求
     * @return 是否发送成功
     */
    @RequestMapping("/user/login/password/forget/email")
    @ResponseBody
    fun loginPasswordForgetEmail(@RequestParam("username") email: String, request: HttpServletRequest): AjaxUtils<*> {
        val username = StringUtils.trimWhitespace(email)
        val msg: String
        if (StringUtils.hasLength(username)) {
            val users = usersService.findByUsername(username)
            if (!ObjectUtils.isEmpty(users)) {
                var dateTime = DateTime.now()
                dateTime = dateTime.plusDays(Workbook.MAILBOX_FORGET_PASSWORD_VALID)
                val passwordResetKey = RandomUtils.generateResetKey()
                users!!.passwordResetKey = passwordResetKey
                users.passwordResetKeyValid = Timestamp(dateTime.toDate().time)
                usersService.update(users)
                if (isyProperties.getMail().isOpen) {
                    mailService.sendPasswordResetMail(users, requestUtils.getBaseUrl(request))
                    return AjaxUtils.of<Any>().success().msg("密码重置邮件已发送至您的邮箱")
                } else {
                    msg = "邮件推送已被管理员关闭"
                }
            } else {
                msg = "获取账号信息失败"
            }
        } else {
            msg = "获取参数有误"
        }
        return AjaxUtils.of<Any>().fail().msg(msg)
    }

    /**
     * 密码重置页面
     *
     * @param key      验证码
     * @param email    邮箱账号
     * @param modelMap 页面对象
     * @return 密码重置页面
     */
    @RequestMapping("/user/login/password/forget/reset")
    fun loginPasswordForgetReset(@RequestParam("key") key: String, @RequestParam("username") email: String, modelMap: ModelMap): String {
        val resetKey = StringUtils.trimWhitespace(key)
        val username = StringUtils.trimWhitespace(email)
        val msg: String
        if (StringUtils.hasLength(resetKey) && StringUtils.hasLength(username)) {
            val users = usersService.findByUsername(username)
            if (!ObjectUtils.isEmpty(users)) {
                val passwordResetKeyValid = users!!.passwordResetKeyValid
                val now = Timestamp(Clock.systemDefaultZone().millis())
                msg = if (now.before(passwordResetKeyValid)) {
                    if (resetKey == users.passwordResetKey) {
                        modelMap.addAttribute("username", email)
                        return "reset_password"
                    } else {
                        "验证码错误"
                    }
                } else {
                    "验证码有效期已过"
                }
            } else {
                msg = "该账号不存在"
            }
        } else {
            msg = "参数异常"
        }
        modelMap.addAttribute("msg", msg)
        return "msg"
    }

    /**
     * 重置密码
     *
     * @param resetVo     页面对象
     * @param bindingResult 检验
     * @return true重置成功 false重置失败
     */
    @RequestMapping("/user/login/password/reset")
    @ResponseBody
    fun loginPasswordReset(@Valid resetVo: ResetVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val username = StringUtils.trimWhitespace(resetVo.email)
            val password = StringUtils.trimWhitespace(resetVo.password)
            val confirmPassword = StringUtils.trimWhitespace(resetVo.confirmPassword)
            if (password == confirmPassword) {
                val users = usersService.findByUsername(username)
                if (!ObjectUtils.isEmpty(users)) {
                    users!!.password = BCryptUtils.bCryptPassword(confirmPassword)
                    usersService.update(users)
                    ajaxUtils.success().msg("密码更新成功")
                } else {
                    ajaxUtils.fail().msg("该账号不存在")
                }
            } else {
                ajaxUtils.fail().msg("密码不一致")
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 获取用户类型数据
     *
     * @return 数据
     */
    @RequestMapping(value = ["/web/platform/users/type/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun usersTypeData(): AjaxUtils<UsersType> {
        val ajaxUtils = AjaxUtils.of<UsersType>()
        val usersTypes = ArrayList<UsersType>()
        usersTypes.add(UsersType(0, "注册类型"))
        val usersTypeRecords = usersTypeService.findAll()
        if (usersTypeRecords.isNotEmpty) {
            usersTypes.addAll(usersTypeRecords.into(UsersType::class.java))
        }
        return ajaxUtils.success().listData(usersTypes)
    }

    /**
     * 用户角色数据
     *
     * @param username 用户账号
     * @return 数据
     */
    @RequestMapping(value = ["/special/channel/users/role/data"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun roleData(@RequestParam("username") username: String): AjaxUtils<Role> {
        return AjaxUtils.of<Role>().success().listData(methodControllerCommon.getRoleData(username))
    }

    /**
     * 保存用户角色
     *
     * @param username 用户账号
     * @param roles    角色
     * @param request  请求
     * @return true 成功 false 角色为空
     */
    @RequestMapping(value = ["/special/channel/users/role/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun roleSave(@RequestParam("username") username: String, @RequestParam("roles") roles: String, request: HttpServletRequest): AjaxUtils<*> {
        return methodControllerCommon.roleSave(username, roles, request)
    }

    /**
     * 平台用户
     *
     * @return 平台用户页面
     */
    @RequestMapping(value = ["/web/menu/platform/users"], method = [(RequestMethod.GET)])
    fun platformUsers(): String {
        return "web/platform/users/users_data::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/platform/users/pass/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun platformPassDatas(request: HttpServletRequest): DataTablesUtils<UsersBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("real_name")
        headers.add("username")
        headers.add("mobile")
        headers.add("role_name")
        headers.add("users_type_name")
        headers.add("enabled")
        headers.add("lang_key")
        headers.add("join_date")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<UsersBean>(request, headers)
        val resultUtils = usersGlue.findAllByPageExistsAuthorities(dataTablesUtils)
        dataTablesUtils.data = resultUtils.getData()
        dataTablesUtils.setiTotalRecords(usersGlue.countAllExistsAuthorities())
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements())
        return dataTablesUtils
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/web/platform/users/wait/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun platformWaitDatas(request: HttpServletRequest): DataTablesUtils<UsersBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("real_name")
        headers.add("username")
        headers.add("mobile")
        headers.add("users_type_name")
        headers.add("lang_key")
        headers.add("join_date")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<UsersBean>(request, headers)
        val resultUtils = usersGlue.findAllByPageNotExistsAuthorities(dataTablesUtils)
        dataTablesUtils.data = resultUtils.getData()
        dataTablesUtils.setiTotalRecords(usersGlue.countAllNotExistsAuthorities())
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements())
        return dataTablesUtils
    }

    /**
     * 更新用户状态
     *
     * @param userIds ids
     * @param enabled 状态
     * @return true 成功 false 失败
     */
    @RequestMapping("/special/channel/users/update/enabled")
    @ResponseBody
    fun usersUpdateEnabled(userIds: String, enabled: Byte?): AjaxUtils<*> {
        return methodControllerCommon.usersUpdateEnabled(userIds, enabled)
    }

    /**
     * 删除无角色关联的用户
     *
     * @param userIds 用户账号
     * @return true 成功 false 失败
     */
    @RequestMapping("/special/channel/users/deletes")
    @ResponseBody
    fun deleteUsers(@RequestParam("username") userIds: String): AjaxUtils<*> {
        return methodControllerCommon.deleteUsers(userIds)
    }

    /**
     * 用户资料页面
     *
     * @param modelMap 页面对象
     * @param request  请求
     * @return 资料页面
     */
    @RequestMapping("/anyone/users/profile")
    fun usersProfile(modelMap: ModelMap, request: HttpServletRequest): String {
        val users = usersService.getUserFromSession()
        val usersType = cacheManageService.findByUsersTypeId(users!!.usersTypeId)
        val page: String
        when (usersType.usersTypeName) {
            Workbook.STUDENT_USERS_TYPE  // 学生
            -> {
                page = "web/platform/users/users_profile_student::#page-wrapper"
                profileStudent(users, modelMap, request)
            }
            Workbook.STAFF_USERS_TYPE  // 教职工
            -> {
                page = "web/platform/users/users_profile_staff::#page-wrapper"
                profileStaff(users, modelMap, request)
            }
            Workbook.SYSTEM_USERS_TYPE  // 系统
            -> {
                page = "web/platform/users/users_profile_system::#page-wrapper"
                profileSystem(users, modelMap, request)
            }
            else -> page = "login"
        }
        return page
    }

    /**
     * 用户资料编辑页面
     *
     * @param modelMap 页面对象
     * @param request  请求
     * @return 资料编辑页面
     */
    @RequestMapping("/anyone/users/profile/edit")
    fun usersProfileEdit(modelMap: ModelMap, request: HttpServletRequest): String {
        val users = usersService.getUserFromSession()
        val usersType = cacheManageService.findByUsersTypeId(users!!.usersTypeId)
        val page: String
        when (usersType.usersTypeName) {
            Workbook.STUDENT_USERS_TYPE  // 学生
            -> {
                page = "web/platform/users/users_profile_student_edit::#page-wrapper"
                profileStudent(users, modelMap, request)
            }
            Workbook.STAFF_USERS_TYPE  // 教职工
            -> {
                page = "web/platform/users/users_profile_staff_edit::#page-wrapper"
                profileStaff(users, modelMap, request)
            }
            Workbook.SYSTEM_USERS_TYPE  // 系统
            -> {
                page = "web/platform/users/users_profile_system_edit::#page-wrapper"
                profileSystem(users, modelMap, request)
            }
            else -> page = "login"
        }
        return page
    }

    /**
     * 处理学生数据
     *
     * @param users    用户
     * @param modelMap 页面对象
     * @param request  请求
     */
    private fun profileStudent(users: Users, modelMap: ModelMap, request: HttpServletRequest) {
        val student = studentService.findByUsernameRelation(users.username)
        if (student.isPresent) {
            val studentBean = student.get().into(StudentBean::class.java)
            modelMap.addAttribute("avatarForSaveOrUpdate", studentBean.avatar)
            val showAvatar = getAvatar(studentBean.avatar!!, request)
            studentBean.avatar = showAvatar
            val dormitoryNumber = studentBean.dormitoryNumber
            if (StringUtils.hasLength(dormitoryNumber) && dormitoryNumber.contains("-")) {
                studentBean.ridgepole = dormitoryNumber.substring(0, dormitoryNumber.lastIndexOf('-'))
                studentBean.dorm = dormitoryNumber.substring(dormitoryNumber.lastIndexOf('-') + 1)
            }
            modelMap.addAttribute("user", studentBean)
        }
    }

    /**
     * 处理教职工数据
     *
     * @param users    用户
     * @param modelMap 页面对象
     * @param request  请求
     */
    private fun profileStaff(users: Users, modelMap: ModelMap, request: HttpServletRequest) {
        val staff = staffService.findByUsernameRelation(users.username)
        if (staff.isPresent) {
            val staffBean = staff.get().into(StaffBean::class.java)
            modelMap.addAttribute("avatarForSaveOrUpdate", staffBean.avatar)
            val showAvatar = getAvatar(staffBean.avatar!!, request)
            staffBean.avatar = showAvatar
            modelMap.addAttribute("user", staffBean)
        }
    }

    /**
     * 处理系统数据
     *
     * @param users    用户
     * @param modelMap 页面对象
     * @param request  请求
     */
    private fun profileSystem(users: Users, modelMap: ModelMap, request: HttpServletRequest) {
        val newUsers = usersService.findByUsername(users.username)
        modelMap.addAttribute("avatarForSaveOrUpdate", newUsers!!.avatar)
        val showAvatar = getAvatar(newUsers.avatar, request)
        newUsers.avatar = showAvatar
        modelMap.addAttribute("user", newUsers)
    }

    /**
     * 得到处理过的用户头像
     *
     * @param avatar  头像
     * @param request 请求
     * @return 处理过的头像
     */
    private fun getAvatar(avatar: String, request: HttpServletRequest): String {
        return if (avatar == Workbook.USERS_AVATAR) {
            requestUtils.getBaseUrl(request) + "/" + avatar
        } else {
            requestUtils.getBaseUrl(request) + "/anyone/users/review/avatar?path=" + avatar
        }
    }

    /**
     * 用户配置页面
     *
     * @param modelMap 页面对象
     * @return 配置页面
     */
    @RequestMapping(value = ["/anyone/users/setting"], method = [(RequestMethod.GET)])
    fun userSetting(modelMap: ModelMap): String {
        val users = usersService.getUserFromSession()
        modelMap.addAttribute("user", users)
        return "web/platform/users/users_setting::#page-wrapper"
    }

    /**
     * 已登录用户身份证号更新检验
     *
     * @param username 用户账号
     * @param idCard   身份证号
     * @return true 可以用 false 不可以
     */
    @RequestMapping(value = ["/anyone/users/valid/id_card"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun validIdCard(@RequestParam("username") username: String, @RequestParam("idCard") idCard: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val users = usersService.getUserFromSession()
        val usersType = cacheManageService.findByUsersTypeId(users!!.usersTypeId)
        when (usersType.usersTypeName) {
            Workbook.STUDENT_USERS_TYPE  // 学生
            -> {
                val studentRecords = studentService.findByIdCardNeUsername(username, idCard)
                val staffs = staffService.findByIdCard(idCard)
                if (ObjectUtils.isEmpty(staffs) && staffs.isEmpty() && studentRecords.isEmpty()) {
                    ajaxUtils.success()
                } else {
                    ajaxUtils.fail()
                }
            }
            Workbook.STAFF_USERS_TYPE  // 教职工
            -> {
                val staffRecords = staffService.findByIdCardNeUsername(username, idCard)
                val students = studentService.findByIdCard(idCard)
                if (ObjectUtils.isEmpty(students) && students.isEmpty() && staffRecords.isEmpty()) {
                    ajaxUtils.success()
                } else {
                    ajaxUtils.fail()
                }
            }
            else -> ajaxUtils.fail()
        }
        return ajaxUtils
    }

    /**
     * 用户上传头像
     *
     * @param multipartHttpServletRequest 文件请求
     * @param request                     请求
     * @return 文件信息
     */
    @RequestMapping("/anyone/users/upload/avatar")
    @ResponseBody
    fun usersUploadAvatar(multipartHttpServletRequest: MultipartHttpServletRequest, request: HttpServletRequest): AjaxUtils<FileBean> {
        val data = AjaxUtils.of<FileBean>()
        try {
            val users = usersService.getUserFromSession()
            val fileBeen = uploadService.upload(multipartHttpServletRequest,
                    RequestUtils.getRealPath(request) + Workbook.avatarPath(users!!), request.remoteAddr)
            val avatarVo = JSON.parseObject(request.getParameter("avatar_data"), AvatarVo::class.java)
            val x = avatarVo.x!!
            val y = avatarVo.y!!
            val height = avatarVo.height!!
            val width = avatarVo.width!!
            val rotate = avatarVo.rotate!!

            for (curFileInfo in fileBeen) {
                val curFile = File(curFileInfo.lastPath)
                val rotateFile = File(curFile.path.substring(0, curFile.path.lastIndexOf('.')) + "_rotate." + curFileInfo.ext)

                // 旋转头像
                ImageUtils.makeRotate(curFile, rotateFile, rotate)

                // 裁剪头像
                if (rotateFile.exists()) {
                    ImageUtils.crop(rotateFile,
                            curFile, x, y, width, height)
                    FilesUtils.deleteFile(rotateFile.absolutePath)
                } else {
                    data.fail().msg("头像创建失败")
                }

                if (curFile.exists()) {
                    data.success().listData(fileBeen).obj(Workbook.avatarPath(users))

                    val tempNewName = curFileInfo.newName!!.substring(0, curFileInfo.newName!!.lastIndexOf('.')) + "." + curFileInfo.ext
                    val tempLastPath = curFileInfo.lastPath!!.substring(0, curFileInfo.lastPath!!.lastIndexOf('.')) + "." + curFileInfo.ext
                    curFileInfo.newName = tempNewName
                    curFileInfo.lastPath = tempLastPath
                } else {
                    data.fail().msg("头像创建失败")
                }
            }

        } catch (e: Exception) {
            log.error("Upload avatar error, error is {}", e)
            data.fail().msg("头像创建失败")
        }

        return data
    }

    /**
     * 预览当前用户头像
     *
     * @param request 请求
     */
    @RequestMapping(value = ["/anyone/users/review/avatar"], method = [(RequestMethod.GET)])
    fun reviewAvatar(@RequestParam("path") path: String, request: HttpServletRequest, response: HttpServletResponse) {
        uploadService.reviewPic("/" + path, request, response)
    }

    /**
     * 用户更新手机号
     *
     * @param username        用户账号
     * @param newMobile       手机号
     * @param phoneVerifyCode 验证码
     * @param session         手机信息
     * @return true or false
     */
    @RequestMapping(value = ["/anyone/user/mobile/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun mobileUpdate(@RequestParam("username") username: String, @RequestParam("newMobile") newMobile: String,
                     @RequestParam("phoneVerifyCode") phoneVerifyCode: String, session: HttpSession): AjaxUtils<*> {
        if (!ObjectUtils.isEmpty(session.getAttribute("mobile"))) {
            val tempMobile = session.getAttribute("mobile") as String
            if (newMobile != tempMobile) {
                return AjaxUtils.of<Any>().fail().msg("发现手机号不一致，请重新获取验证码")
            } else {
                if (!ObjectUtils.isEmpty(session.getAttribute("mobileExpiry"))) {
                    val mobileExpiry = session.getAttribute("mobileExpiry") as Date
                    val now = Date()
                    if (!now.before(mobileExpiry)) {
                        return AjaxUtils.of<Any>().fail().msg("验证码已过有效期(30分钟)")
                    } else {
                        if (!ObjectUtils.isEmpty(session.getAttribute("mobileCode"))) {
                            val mobileCode = session.getAttribute("mobileCode") as String
                            if (phoneVerifyCode != mobileCode) {
                                return AjaxUtils.of<Any>().fail().msg("验证码错误")
                            } else {
                                val users = usersService.findByUsername(username)
                                return if (!ObjectUtils.isEmpty(users)) {
                                    users!!.mobile = newMobile
                                    usersService.update(users)
                                    //清空session
                                    session.removeAttribute("mobileExpiry")
                                    session.removeAttribute("mobile")
                                    session.removeAttribute("mobileCode")
                                    AjaxUtils.of<Any>().success().msg("更新手机号成功")
                                } else {
                                    AjaxUtils.of<Any>().fail().msg("未查询到用户信息")
                                }
                            }
                        } else {
                            return AjaxUtils.of<Any>().fail().msg("无法获取当前用户电话验证码，请重新获取手机验证码")
                        }
                    }
                } else {
                    return AjaxUtils.of<Any>().fail().msg("无法获取当前用户验证码有效期，请重新获取手机验证码")
                }
            }
        } else {
            return AjaxUtils.of<Any>().fail().msg("无法获取当前用户电话，请重新获取手机验证码")
        }
    }

    /**
     * 更新用户密码
     *
     * @param username    用户账号
     * @param newPassword 新密码
     * @param okPassword  确认密码
     * @return true or false
     */
    @RequestMapping(value = ["/anyone/user/password/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun passwordUpdate(@RequestParam("username") username: String, @RequestParam("newPassword") newPassword: String,
                       @RequestParam("okPassword") okPassword: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val regex = "^[a-zA-Z0-9]\\w{5,17}$"
        if (newPassword.matches(regex.toRegex())) {
            if (okPassword == newPassword) {
                val users = usersService.findByUsername(username)
                if (!ObjectUtils.isEmpty(users)) {
                    users!!.password = BCryptUtils.bCryptPassword(newPassword)
                    usersService.update(users)
                    ajaxUtils.success().msg("更新密码成功")
                } else {
                    ajaxUtils.fail().msg("未查询到用户信息")
                }
            } else {
                ajaxUtils.fail().msg("密码不一致")
            }
        } else {
            ajaxUtils.fail().msg("密码为6位数字或大小写字母")
        }
        return ajaxUtils
    }

    /**
     * 系统更新信息
     *
     * @param usersVo       系统
     * @param bindingResult 检验
     * @return true or false
     */
    @RequestMapping(value = ["/anyone/users/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun usersUpdate(@Valid usersVo: UsersVo, bindingResult: BindingResult): AjaxUtils<*> {
        if (!bindingResult.hasErrors()) {
            val updateUsers = usersService.findByUsername(usersVo.username!!)
            return if (!ObjectUtils.isEmpty(updateUsers)) {
                updateUsers!!.realName = usersVo.realName
                updateUsers.avatar = usersVo.avatar
                usersService.update(updateUsers)
                AjaxUtils.of<Any>().success().msg("更新成功")
            } else {
                AjaxUtils.of<Any>().fail().msg("未查询到用户")
            }
        }
        return AjaxUtils.of<Any>().fail().msg("参数异常")
    }
}