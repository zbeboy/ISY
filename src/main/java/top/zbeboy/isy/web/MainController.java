package top.zbeboy.isy.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.SystemLog;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.SystemLogService;
import top.zbeboy.isy.service.UsersService;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.service.util.UUIDUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Locale;

/**
 * Created by lenovo on 2016-08-17.
 */
@Controller
public class MainController {

    private final Logger log = LoggerFactory.getLogger(MainController.class);

    @Resource
    private LocaleResolver localeResolver;

    @Resource
    private SystemLogService systemLogService;

    @Resource
    private UsersService usersService;

    @RequestMapping("/")
    public String root() {
        return "index";
    }

    /**
     * Home page.
     *
     * @return home page
     */
    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * api by spring fox.
     *
     * @return api
     */
    @RequestMapping("/api")
    public String api() {
        return "redirect:swagger-ui.html";
    }

    /**
     * 登录页
     *
     * @return 登录页.
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    /**
     * 注册页
     *
     * @param type 注册类型(学生，教职工)
     * @return 注册页
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(@RequestParam("type") String type) {
        // 注册学生
        if (type.equalsIgnoreCase(Workbook.STUDENT_REGIST)) {
            return "student_register";
        }

        // 注册教师
        if (type.equalsIgnoreCase(Workbook.STAFF_REGIST)) {
            return "staff_register";
        }
        return "login";
    }

    /**
     * 注册完成时，但并不是成功
     *
     * @return 完成页面
     */
    @RequestMapping(value = "/register/finish", method = RequestMethod.GET)
    public String registerFinish(ModelMap modelMap) {
        modelMap.put("msg", "验证邮件已发送至您邮箱，请登录邮箱进行验证。");
        return "msg";
    }

    /**
     * 忘记密码
     *
     * @return 忘记密码页面
     */
    @RequestMapping(value = "/user/login/password/forget", method = RequestMethod.GET)
    public String loginPasswordForget() {
        return "forget_password";
    }

    /**
     * 忘记密码完成时，但并不是成功
     *
     * @return 完成页面
     */
    @RequestMapping(value = "/user/login/password/forget/finish", method = RequestMethod.GET)
    public String loginPasswordForgetFinish(ModelMap modelMap) {
        modelMap.put("msg", "密码重置邮件已发送至您的邮箱。");
        return "msg";
    }

    /**
     * 密码重置成功
     *
     * @param modelMap
     * @return 重置成功页面
     */
    @RequestMapping(value = "/user/password/reset/finish", method = RequestMethod.GET)
    public String passwordResetFinish(ModelMap modelMap) {
        modelMap.put("msg", "密码重置成功。");
        return "msg";
    }

    /**
     * 后台欢迎页
     *
     * @return 后台欢迎页
     */
    @RequestMapping(value = "/web/menu/backstage", method = RequestMethod.GET)
    public String backstage(ModelMap modelMap, HttpServletRequest request) {
        Users users = usersService.getUserFromSession();
        if (!ObjectUtils.isEmpty(users)) {
            String ip = RequestUtils.getIpAddress(request);
            SystemLog systemLog = new SystemLog(UUIDUtils.getUUID(), "登录系统", new Timestamp(System.currentTimeMillis()), users.getUsername(), ip);
            systemLogService.save(systemLog);
        } else {
            modelMap.addAttribute("msg", "获取用户信息失败");
            return "msg";
        }
        return "backstage";
    }

    /**
     * 语言切换，暂时不用
     *
     * @param request
     * @param response
     * @param language 语言
     * @return 重置页面
     */
    @RequestMapping("/language")
    public ModelAndView language(HttpServletRequest request, HttpServletResponse response, String language) {
        language = language.toLowerCase();
        if (language.equals("")) {
            return new ModelAndView("redirect:/");
        } else {
            switch (language) {
                case "zh_cn":
                    localeResolver.setLocale(request, response, Locale.CHINA);
                    break;
                case "en":
                    localeResolver.setLocale(request, response, Locale.ENGLISH);
                    break;
                default:
                    localeResolver.setLocale(request, response, Locale.CHINA);
                    break;
            }
        }

        return new ModelAndView("redirect:/");
    }
}
