package top.zbeboy.isy.web

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.ModelAndView
import top.zbeboy.isy.annotation.logging.RecordSystemLogging
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.service.common.FilesService
import top.zbeboy.isy.service.common.UploadService
import top.zbeboy.isy.service.system.AuthoritiesService
import top.zbeboy.isy.service.util.FilesUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.web.util.AjaxUtils
import java.io.IOException
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2017-11-03 .
 **/
@Controller
open class MainController {

    private val log = LoggerFactory.getLogger(MainController::class.java)

    @Resource
    lateinit open var localeResolver: LocaleResolver

    @Resource
    lateinit open var uploadService: UploadService

    @Resource
    lateinit open var filesService: FilesService

    @Resource
    lateinit open var authoritiesService: AuthoritiesService

    /**
     * main page
     *
     * @return main page
     */
    @RequestMapping("/")
    fun root(): String {
        return "index"
    }

    /**
     * Home page.
     *
     * @return home page
     */
    @RequestMapping("/index")
    fun index(): String {
        return "index"
    }


    /**
     * 登录页
     *
     * @return 登录页.
     */
    @RequestMapping(value = "/login", method = arrayOf(RequestMethod.GET))
    fun login(): String {
        val page: String
        if (authoritiesService.isRememberMeAuthenticated) {
            page = "redirect:/web/menu/backstage"
        } else {
            page = "login"
        }
        return page
    }

    /**
     * 注册页
     *
     * @param type 注册类型(学生，教职工)
     * @return 注册页
     */
    @RequestMapping(value = "/register", method = arrayOf(RequestMethod.GET))
    fun register(@RequestParam("type") type: String): String {
        // 注册学生
        if (type.equals(Workbook.STUDENT_REGIST, ignoreCase = true)) {
            return "student_register"
        }

        // 注册教师
        return if (type.equals(Workbook.STAFF_REGIST, ignoreCase = true)) {
            "staff_register"
        } else "login"
    }

    /**
     * 注册完成时，但并不是成功
     *
     * @param modelMap 页面对象
     * @return 完成页面
     */
    @RequestMapping(value = "/register/finish", method = arrayOf(RequestMethod.GET))
    fun registerFinish(modelMap: ModelMap): String {
        modelMap.put("msg", "验证邮件已发送至您的邮箱，请登录邮箱进行验证！")
        return "msg"
    }

    /**
     * 忘记密码
     *
     * @return 忘记密码页面
     */
    @RequestMapping(value = "/user/login/password/forget", method = arrayOf(RequestMethod.GET))
    fun loginPasswordForget(): String {
        return "forget_password"
    }

    /**
     * 忘记密码完成时，但并不是成功
     *
     * @param modelMap 页面对象
     * @return 完成页面
     */
    @RequestMapping(value = "/user/login/password/forget/finish", method = arrayOf(RequestMethod.GET))
    fun loginPasswordForgetFinish(modelMap: ModelMap): String {
        modelMap.put("msg", "密码重置邮件已发送至您的邮箱。")
        return "msg"
    }

    /**
     * 密码重置成功
     *
     * @param modelMap 页面对象
     * @return 重置成功页面
     */
    @RequestMapping(value = "/user/password/reset/finish", method = arrayOf(RequestMethod.GET))
    fun passwordResetFinish(modelMap: ModelMap): String {
        modelMap.put("msg", "密码重置成功。")
        return "msg"
    }

    /**
     * 后台欢迎页
     *
     * @return 后台欢迎页
     */
    @RecordSystemLogging(module = "Main", methods = "backstage", description = "访问系统主页")
    @RequestMapping(value = "/web/menu/backstage", method = arrayOf(RequestMethod.GET))
    open fun backstage(request: HttpServletRequest): String {
        return "backstage"
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @param request  请求
     * @return true or false
     */
    @RequestMapping("/anyone/users/delete/file")
    @ResponseBody
    fun deleteFile(@RequestParam("filePath") filePath: String, request: HttpServletRequest): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        try {
            if (FilesUtils.deleteFile(RequestUtils.getRealPath(request) + filePath)) {
                ajaxUtils.success().msg("删除文件成功")
            } else {
                ajaxUtils.fail().msg("删除文件失败")
            }
        } catch (e: IOException) {
            log.error(" delete file is exception.", e)
            ajaxUtils.fail().msg("删除文件异常")
        }

        return ajaxUtils
    }

    /**
     * 文件下载
     *
     * @param fileId   文件id
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping("/anyone/users/download/file")
    fun downloadFile(@RequestParam("fileId") fileId: String, request: HttpServletRequest, response: HttpServletResponse) {
        val files = filesService.findById(fileId)
        if (!ObjectUtils.isEmpty(files)) {
            uploadService.download(files.originalFileName, "/" + files.relativePath, response, request)
        }
    }

    /**
     * 语言切换，暂时不用
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param language 语言
     * @return 重置页面
     */
    @RequestMapping("/language")
    fun language(request: HttpServletRequest, response: HttpServletResponse, language: String): ModelAndView {
        val languageLowerCase = language.toLowerCase()
        if (languageLowerCase == "") {
            return ModelAndView("redirect:/")
        } else {
            when (languageLowerCase) {
                "zh_cn" -> localeResolver.setLocale(request, response, Locale.CHINA)
                "en" -> localeResolver.setLocale(request, response, Locale.ENGLISH)
                else -> localeResolver.setLocale(request, response, Locale.CHINA)
            }
        }

        return ModelAndView("redirect:/")
    }

    /**
     * 用于集群时，对服务器心跳检测
     *
     * @return 服务器是否正常运行
     */
    @RequestMapping(value = "/server/probe", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun serverHealthCheck(): AjaxUtils<*> {
        return AjaxUtils.of<Any>().success().msg("Server is running ...")
    }
}