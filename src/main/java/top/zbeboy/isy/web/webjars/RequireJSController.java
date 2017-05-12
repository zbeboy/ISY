package top.zbeboy.isy.web.webjars;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.webjars.RequireJS;

import javax.servlet.http.HttpServletRequest;

/**
 * 配置webjars与requireJS
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Controller
public class RequireJSController {

    @ResponseBody
    @RequestMapping(value = "/webjarsjs", produces = "application/javascript")
    public String webjarjs(HttpServletRequest request) {
        return RequireJS.getSetupJavaScript(request.getContextPath() + "/webjars/");
    }
}
