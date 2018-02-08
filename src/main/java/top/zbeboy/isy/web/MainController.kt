package top.zbeboy.isy.web

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.ModelAndView
import top.zbeboy.isy.annotation.logging.RecordSystemLogging
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.service.system.AuthoritiesService
import top.zbeboy.isy.web.util.AjaxUtils
import java.io.*
import java.nio.charset.Charset
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
    open lateinit var localeResolver: LocaleResolver

    @Resource
    open lateinit var authoritiesService: AuthoritiesService

    @Autowired
    open lateinit var isyProperties: ISYProperties

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
    @RequestMapping(value = ["/login"], method = [(RequestMethod.GET)])
    fun login(): String {
        return if (authoritiesService.isRememberMeAuthenticated()) {
            "redirect:/web/menu/backstage"
        } else {
            "login"
        }
    }

    /**
     * 注册页
     *
     * @param type 注册类型(学生，教职工)
     * @return 注册页
     */
    @RequestMapping(value = ["/register"], method = [(RequestMethod.GET)])
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
    @RequestMapping(value = ["/register/finish"], method = [(RequestMethod.GET)])
    fun registerFinish(modelMap: ModelMap): String {
        modelMap["msg"] = "验证邮件已发送至您的邮箱，请登录邮箱进行验证！"
        return "msg"
    }

    /**
     * 忘记密码
     *
     * @return 忘记密码页面
     */
    @RequestMapping(value = ["/user/login/password/forget"], method = [(RequestMethod.GET)])
    fun loginPasswordForget(): String {
        return "forget_password"
    }

    /**
     * 忘记密码完成时，但并不是成功
     *
     * @param modelMap 页面对象
     * @return 完成页面
     */
    @RequestMapping(value = ["/user/login/password/forget/finish"], method = [(RequestMethod.GET)])
    fun loginPasswordForgetFinish(modelMap: ModelMap): String {
        modelMap["msg"] = "密码重置邮件已发送至您的邮箱。"
        return "msg"
    }

    /**
     * 密码重置成功
     *
     * @param modelMap 页面对象
     * @return 重置成功页面
     */
    @RequestMapping(value = ["/user/login/password/reset/finish"], method = [(RequestMethod.GET)])
    fun passwordResetFinish(modelMap: ModelMap): String {
        modelMap["msg"] = "密码重置成功。"
        return "msg"
    }

    /**
     * 后台欢迎页
     *
     * @return 后台欢迎页
     */
    @RecordSystemLogging(module = "Main", methods = "backstage", description = "访问系统主页")
    @RequestMapping(value = ["/web/menu/backstage"], method = [(RequestMethod.GET)])
    open fun backstage(request: HttpServletRequest): String {
        return "backstage"
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
    @RequestMapping(value = ["/server/probe"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun serverHealthCheck(): AjaxUtils<*> {
        return AjaxUtils.of<Any>().success().msg("Server is running ...")
    }

    /**
     * let's encrypt certificate check.
     * @param request 请求
     * @param response 响应
     */
    @RequestMapping(value = ["/.well-known/acme-challenge/*"], method = [(RequestMethod.GET)])
    fun letUsEncryptCertificateCheck(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        val responseHeaders = HttpHeaders()
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8")
        val result = ""
        try {
            val uri = request.requestURI.replace("/", "\\")
            //文件路径自行替换一下就行,就是上图中生成验证文件的路径,因为URI中已经包含了/.well-known/acme-challenge/,所以这里不需要
            val file = File(isyProperties.getCertificate().place + uri)
            val `is` = FileInputStream(file)
            // 设置response参数，可以打开下载页面
            response.reset()
            response.contentType = "application/vnd.ms-excel;charset=utf-8"
            response.setHeader("Content-Disposition", "attachment;filename=" + String("验证文件".toByteArray(), Charset.forName("iso-8859-1")))
            val out = response.outputStream
            var bis: BufferedInputStream? = null
            var bos: BufferedOutputStream? = null
            try {
                bis = BufferedInputStream(`is`)
                bos = BufferedOutputStream(out)
                val buff = ByteArray(2048)
                var bytesRead: Int = bis.read(buff, 0, buff.size)
                // Simple read/write loop.
                while (-1 != bytesRead) {
                    bos.write(buff, 0, bytesRead)
                    bytesRead = bis.read(buff, 0, buff.size)
                }
            } catch (e: IOException) {
                throw e
            } finally {
                if (bis != null)
                    bis.close()
                if (bos != null)
                    bos.close()
            }
        } catch (e: Exception) {
            log.error("Let's encrypt certificate check error : {}", e)
        }
        return ResponseEntity(result, responseHeaders, HttpStatus.OK)
    }
}