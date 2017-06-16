package top.zbeboy.isy.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Files;
import top.zbeboy.isy.service.common.FilesService;
import top.zbeboy.isy.service.common.UploadService;
import top.zbeboy.isy.service.util.FilesUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.web.util.AjaxUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by lenovo on 2016-08-17.
 */
@Slf4j
@Controller
public class MainController {

    @Resource
    private LocaleResolver localeResolver;

    @Resource
    private UploadService uploadService;

    @Resource
    private FilesService filesService;

    /**
     * main page
     *
     * @return main page
     */
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
     * @param modelMap 页面对象
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
     * @param modelMap 页面对象
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
     * @param modelMap 页面对象
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
    public String backstage() {
        return "backstage";
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
    public AjaxUtils deleteFile(@RequestParam("filePath") String filePath, HttpServletRequest request) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        try {
            if (FilesUtils.deleteFile(RequestUtils.getRealPath(request) + filePath)) {
                ajaxUtils.success().msg("删除文件成功");
            } else {
                ajaxUtils.fail().msg("删除文件失败");
            }
        } catch (IOException e) {
            log.error(" delete file is exception.", e);
            ajaxUtils.fail().msg("删除文件异常");
        }
        return ajaxUtils;
    }

    /**
     * 文件下载
     *
     * @param fileId   文件id
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping("/anyone/users/download/file")
    public void downloadFile(@RequestParam("fileId") String fileId, HttpServletRequest request, HttpServletResponse response) {
        Files files = filesService.findById(fileId);
        if (!ObjectUtils.isEmpty(files)) {
            uploadService.download(files.getOriginalFileName(), "/" + files.getRelativePath(), response, request);
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
