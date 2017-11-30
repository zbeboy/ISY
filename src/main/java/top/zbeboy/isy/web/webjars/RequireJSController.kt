package top.zbeboy.isy.web.webjars

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.webjars.RequireJS
import javax.servlet.http.HttpServletRequest

/**
 * 配置webjars与requireJS
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
@Controller
open class RequireJSController {
    @ResponseBody
    @RequestMapping(value = ["/webjarsjs"], produces = ["application/javascript"])
    fun webjarjs(request: HttpServletRequest): String {
        return RequireJS.getSetupJavaScript(request.contextPath + "/webjars/")
    }
}