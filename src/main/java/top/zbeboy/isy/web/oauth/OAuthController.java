package top.zbeboy.isy.web.oauth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OAuthController {

    @RequestMapping("/oauth/error")
    public String oauthError(ModelMap modelMap) throws Exception {
        // We can add more stuff to the model here for JSP rendering. If the client was a machine then
        // the JSON will already have been rendered.
        modelMap.addAttribute("title", "OAuth error...");
        modelMap.addAttribute("message", "OAuth 授权过程中出现问题，若无法解决，请联系管理员。");
        return "oauth/oauth_error";
    }

}
