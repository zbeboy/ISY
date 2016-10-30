package top.zbeboy.isy.web.platform.users;

import com.octo.captcha.service.CaptchaServiceException;
import org.joda.time.DateTime;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import top.zbeboy.isy.config.ISYProperties;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;
import top.zbeboy.isy.domain.tables.records.CollegeRoleRecord;
import top.zbeboy.isy.domain.tables.records.RoleRecord;
import top.zbeboy.isy.domain.tables.records.UsersTypeRecord;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.service.util.BCryptUtils;
import top.zbeboy.isy.service.util.RandomUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.bean.file.FileBean;
import top.zbeboy.isy.web.bean.platform.users.UsersBean;
import top.zbeboy.isy.web.jcaptcha.CaptchaServiceSingleton;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;
import top.zbeboy.isy.web.vo.register.student.StudentVo;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-08-22.
 */
@Controller
public class UsersController {

    private final Logger log = LoggerFactory.getLogger(UsersController.class);

    /*
    检验邮箱使用
     */
    private final int VALID_EMAIL = 1;

    /*
    检验手机号使用
     */
    private final int VALID_MOBILE = 2;

    /*
    验证码错误码
     */
    private final int CAPTCHA_ERROR = 0;

    /*
    无效的验证码
     */
    private final int CAPTCHA_INVALID = 1;

    @Resource
    private UsersService usersService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private AuthoritiesService authoritiesService;

    @Resource
    private StudentService studentService;

    @Resource
    private StaffService staffService;

    @Resource
    private CollegeRoleService collegeRoleService;

    @Resource
    private RoleService roleService;

    @Resource
    private MailService mailService;

    @Resource
    private MobileService mobileService;

    @Resource
    private UploadService uploadService;

    @Autowired
    private ISYProperties isyProperties;

    @Autowired
    private RequestUtils requestUtils;

    /**
     * 检验注册表单
     *
     * @param username  账号(邮箱)
     * @param mobile    手机号
     * @param validType 检验类型：1.邮箱；2.手机号
     * @return true 检验通过 false 不通过
     */
    @RequestMapping(value = "/user/register/valid/users", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils validUsers(String username, String mobile, int validType) {
        if (validType == VALID_EMAIL) {
            Users tempUsers = usersService.findByUsername(StringUtils.trimWhitespace(username));
            if (!ObjectUtils.isEmpty(tempUsers)) {
                return new AjaxUtils().fail().msg("该邮箱已被注册");
            } else {
                return new AjaxUtils().success();
            }
        }

        if (validType == VALID_MOBILE) {
            List<Users> tempUsers = usersService.findByMobile(StringUtils.trimWhitespace(mobile));
            if (!ObjectUtils.isEmpty(tempUsers)) {
                return new AjaxUtils().fail().msg("该手机号已被注册");
            } else {
                return new AjaxUtils().success();
            }
        }

        return new AjaxUtils().fail().msg("检验类型异常");
    }

    /**
     * 检验手机验证码
     *
     * @param mobile          手机号
     * @param phoneVerifyCode 验证码
     * @param session
     * @return true 通过 false 不通过
     */
    @RequestMapping(value = "/user/register/valid/mobile", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils validMobile(@RequestParam("mobile") String mobile, @RequestParam("phoneVerifyCode") String phoneVerifyCode, HttpSession session) {
        if (!ObjectUtils.isEmpty(session.getAttribute("mobile"))) {
            String tempMobile = (String) session.getAttribute("mobile");
            if (!mobile.equals(tempMobile)) {
                return new AjaxUtils().fail().msg("发现手机号不一致，请重新获取验证码");
            } else {
                if (!ObjectUtils.isEmpty(session.getAttribute("mobileExpiry"))) {
                    Date mobileExpiry = (Date) session.getAttribute("mobileExpiry");
                    Date now = new Date();
                    if (!now.before(mobileExpiry)) {
                        return new AjaxUtils().fail().msg("验证码已过有效期(30分钟)");
                    } else {
                        if (!ObjectUtils.isEmpty(session.getAttribute("mobileCode"))) {
                            String mobileCode = (String) session.getAttribute("mobileCode");
                            if (!phoneVerifyCode.equals(mobileCode)) {
                                return new AjaxUtils().fail().msg("验证码错误");
                            } else {
                                return new AjaxUtils().success().msg("");
                            }
                        }
                    }
                }
            }
        }
        return new AjaxUtils().fail().msg("请输入手机号，并点击获取验证码按钮");
    }

    /**
     * 邮箱邮件验证
     *
     * @param key
     * @param username
     * @param modelMap
     * @return 消息页面
     */
    @RequestMapping("/user/register/mailbox/valid")
    public String validEmail(@RequestParam("key") String key, @RequestParam("username") String username, ModelMap modelMap) {
        Users users = usersService.findByUsername(username);
        if (!ObjectUtils.isEmpty(users)) {
            Timestamp mailboxVerifyValid = users.getMailboxVerifyValid();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.before(mailboxVerifyValid)) {
                if (key.equals(users.getMailboxVerifyCode())) {
                    Byte verifyMailbox = 1;
                    users.setVerifyMailbox(verifyMailbox);
                    usersService.update(users);
                    modelMap.put("msg", "恭喜您注册成功，请等待管理员审核后便可登录，审核结果会很快发至您的邮箱，注意查收。");
                } else {
                    modelMap.put("msg", "您的邮箱验证码有误，验证邮箱失败！");
                }
            } else {
                modelMap.put("msg", "您的邮箱验证已过有效期！");
            }
        } else {
            modelMap.put("msg", "该邮箱未注册！");
        }
        return "msg";
    }

    /**
     * 获取手机验证码
     *
     * @param mobile  手机号
     * @param session
     * @return 发送验证码到手机
     */
    @RequestMapping(value = "/user/register/mobile/code", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils mobileCode(@RequestParam("mobile") String mobile, HttpSession session) {
        String regex = "1[0-9]{10}";
        if (mobile.matches(regex)) {
            DateTime dateTime = DateTime.now();
            dateTime = dateTime.plusMinutes(30);
            String mobileKey = RandomUtils.generateMobileKey();
            System.out.println(mobileKey);
            session.setAttribute("mobile", mobile);
            session.setAttribute("mobileExpiry", dateTime.toDate());
            session.setAttribute("mobileCode", mobileKey);
            mobileService.sendValidMobileShortMessage(mobile, mobileKey);
            if (isyProperties.getMobile().isOpen()) {
                return new AjaxUtils().success().msg("短信已发送，请您稍等");
            } else {
                return new AjaxUtils().fail().msg("短信发送已被管理员关闭");
            }
        } else {
            return new AjaxUtils().fail().msg("手机号格式不正确");
        }
    }

    /**
     * 获取验证码
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/user/login/jcaptcha")
    public void jCaptcha(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        byte[] captchaChallengeAsJpeg = null;
        // the output stream to render the captcha image as jpeg into
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            // get the session id that will identify the generated captcha.
            // the same id must be used to validate the response, the session id is a good candidate!
            String captchaId = request.getSession().getId();
            // call the ImageCaptchaService getChallenge method
            BufferedImage challenge = CaptchaServiceSingleton.getInstance().getImageChallengeForID(captchaId, request.getLocale());
            // a jpeg encoder
            ImageIO.write(challenge, "jpeg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            response.sendError(response.SC_NOT_FOUND);
            log.error(" jcaptcha exception : {} ", e.getMessage());
            return;
        } catch (CaptchaServiceException e) {
            response.sendError(response.SC_INTERNAL_SERVER_ERROR);
            log.error(" jcaptcha exception : {} ", e.getMessage());
            return;
        }

        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();

        // flush it in the response
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();

        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    /**
     * js 检验验证码
     *
     * @param captcha
     * @param request
     * @return
     */
    @RequestMapping("/user/login/valid/jcaptcha")
    @ResponseBody
    public AjaxUtils validateCaptchaForId(@RequestParam("j_captcha_response") String captcha, HttpServletRequest request) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        Boolean isResponseCorrect = Boolean.FALSE;
        // remember that we need an id to validate!
        String captchaId = request.getSession().getId();
        log.debug(" valid captchaId : {}", captchaId);
        log.debug(" j_captcha_response : {} ", captcha);
        // call the service method
        try {
            isResponseCorrect = CaptchaServiceSingleton.getInstance().validateResponseForID(captchaId, captcha);
            if (isResponseCorrect) {
                ajaxUtils.success().msg("验证码正确");
            } else {
                ajaxUtils.fail().msg("验证码错误").obj(CAPTCHA_ERROR);
            }
        } catch (CaptchaServiceException e) {
            // should not happen,may be thrown if the id is not valid
            ajaxUtils.fail().msg("参数无效,请重新输入验证码").obj(CAPTCHA_INVALID);
            log.error(" valid exception : {} ", e.getMessage());
        }
        return ajaxUtils;
    }

    /**
     * 忘记密码邮件
     *
     * @param email 账号
     * @return
     */
    @RequestMapping("/user/login/password/forget/email")
    @ResponseBody
    public AjaxUtils loginPasswordForgetEmail(@RequestParam("username") String email, HttpServletRequest request) {
        String username = StringUtils.trimWhitespace(email);
        String msg = "获取账号信息失败";
        if (StringUtils.hasLength(username)) {
            Users users = usersService.findByUsername(username);
            if (!ObjectUtils.isEmpty(users)) {
                DateTime dateTime = DateTime.now();
                dateTime = dateTime.plusDays(2);
                String passwordResetKey = RandomUtils.generateResetKey();
                users.setPasswordResetKey(passwordResetKey);
                users.setPasswordResetKeyValid(new Timestamp(dateTime.toDate().getTime()));
                usersService.update(users);
                if (isyProperties.getMail().isOpen()) {
                    mailService.sendPasswordResetMail(users, new RequestUtils().getBaseUrl(request));
                    return new AjaxUtils().success().msg("密码重置邮件已发送至您的邮箱");
                } else {
                    msg = "邮件推送已被管理员关闭";
                }
            }
        }
        return new AjaxUtils().fail().msg(msg);
    }

    /**
     * 密码重置页面
     *
     * @param key      验证码
     * @param email    邮箱账号
     * @param modelMap
     * @return 密码重置页面
     */
    @RequestMapping("/user/login/password/forget/reset")
    public String loginPasswordForgetReset(@RequestParam("key") String key, @RequestParam("username") String email, ModelMap modelMap) {
        String resetKey = StringUtils.trimWhitespace(key);
        String username = StringUtils.trimWhitespace(email);
        String msg = "";
        if (StringUtils.hasLength(resetKey) && StringUtils.hasLength(username)) {
            Users users = usersService.findByUsername(username);
            if (!ObjectUtils.isEmpty(users)) {
                Timestamp passwordResetKeyValid = users.getPasswordResetKeyValid();
                Timestamp now = new Timestamp(System.currentTimeMillis());
                if (now.before(passwordResetKeyValid)) {
                    if (resetKey.equals(users.getPasswordResetKey())) {
                        modelMap.addAttribute("username", email);
                        return "reset_password";
                    } else {
                        msg = "验证码错误";
                    }
                } else {
                    msg = "验证码有效期已过";
                }
            } else {
                msg = "该账号不存在";
            }
        } else {
            msg = "参数异常";
        }
        modelMap.addAttribute("msg", msg);
        return "msg";
    }

    /**
     * 重置密码
     *
     * @param studentVo     页面对象
     * @param bindingResult
     * @return true重置成功 false重置失败
     */
    @RequestMapping("/user/login/password/reset")
    @ResponseBody
    public AjaxUtils loginPasswordReset(StudentVo studentVo, BindingResult bindingResult) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        if (!bindingResult.hasErrors()) {
            String username = StringUtils.trimWhitespace(studentVo.getEmail());
            String password = StringUtils.trimWhitespace(studentVo.getPassword());
            String confirmPassword = StringUtils.trimWhitespace(studentVo.getConfirmPassword());
            if (password.equals(confirmPassword)) {
                Users users = usersService.findByUsername(username);
                if (!ObjectUtils.isEmpty(users)) {
                    users.setPassword(BCryptUtils.bCryptPassword(confirmPassword));
                    usersService.update(users);
                    ajaxUtils.success().msg("密码更新成功");
                } else {
                    ajaxUtils.fail().msg("该账号不存在");
                }
            } else {
                ajaxUtils.fail().msg("密码不一致");
            }
        } else {
            ajaxUtils.fail().msg("参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 获取用户类型数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/web/platform/users/type/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<UsersType> usersTypeData() {
        List<UsersType> usersTypes = new ArrayList<>();
        usersTypes.add(new UsersType(0, "注册类型"));
        Result<UsersTypeRecord> usersTypeRecords = usersTypeService.findAll();
        if (usersTypeRecords.isNotEmpty()) {
            usersTypes.addAll(usersTypeRecords.into(UsersType.class));
        }
        return new AjaxUtils<UsersType>().success().listData(usersTypes);
    }

    /**
     * 用户角色数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/special/channel/users/role/data", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils<Role> roleData(@RequestParam("username") String username) {
        List<Role> roles = new ArrayList<>();
        if (authoritiesService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            Role role = roleService.findByRoleName(Workbook.ADMIN_ROLE_NAME);
            roles.add(role);
        }
        // 根据此用户账号查询院下所有角色
        Users users = usersService.findByUsername(username);
        Optional<Record> record = usersService.findUserSchoolInfo(users);
        if (record.isPresent()) {
            College college = record.get().into(College.class);
            List<CollegeRoleRecord> collegeRoleRecords = collegeRoleService.findByCollegeId(college.getCollegeId());
            if (!ObjectUtils.isEmpty(collegeRoleRecords) && !collegeRoleRecords.isEmpty()) {
                List<Role> tempRole = new ArrayList<>();
                List<Integer> roleIds = new ArrayList<>();
                collegeRoleRecords.forEach(role -> roleIds.add(role.getRoleId()));
                Result<RoleRecord> roleRecords = roleService.findInRoleId(roleIds);
                tempRole = roleRecords.into(Role.class);
                roles.addAll(tempRole);
            }
        }
        return new AjaxUtils<Role>().success().listData(roles);
    }

    /**
     * 保存用户角色
     *
     * @param username 用户账号
     * @param roles    角色
     * @return true 成功 false 角色为空
     */
    @RequestMapping(value = "/special/channel/users/role/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils roleSave(@RequestParam("username") String username, @RequestParam("roles") String roles) {
        if (StringUtils.hasLength(roles)) {
            List<String> roleList = SmallPropsUtils.StringIdsToStringList(roles);
            authoritiesService.deleteByUsername(username);
            roleList.forEach(role -> {
                Authorities authorities = new Authorities(username, role);
                authoritiesService.save(authorities);
            });
            return new AjaxUtils().success().msg("更改用户角色成功");
        }
        return new AjaxUtils().fail().msg("用户角色参数异常");
    }

    /**
     * 平台用户
     *
     * @return 平台用户页面
     */
    @RequestMapping(value = "/web/menu/platform/users", method = RequestMethod.GET)
    public String platformUsers() {
        return "web/platform/users/users_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request
     * @return datatables数据
     */
    @RequestMapping(value = "/web/platform/users/pass/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<UsersBean> platformPassDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("username");
        headers.add("mobile");
        headers.add("real_name");
        headers.add("role_name");
        headers.add("users_type_name");
        headers.add("enabled");
        headers.add("lang_key");
        headers.add("join_date");
        headers.add("operator");
        DataTablesUtils<UsersBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = usersService.findAllByPageExistsAuthorities(dataTablesUtils);
        List<UsersBean> usersBeen = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            usersBeen = records.into(UsersBean.class);
            usersBeen.forEach(user -> {
                Result<Record1<String>> record1s = usersService.findByUsernameWithRoleNoCache(user.getUsername());
                StringBuilder stringBuilder = new StringBuilder();
                for (Record r : record1s) {
                    stringBuilder.append(r.getValue(0)).append(" ");
                }
                user.setRoleName(stringBuilder.toString());
            });
        }
        dataTablesUtils.setData(usersBeen);
        dataTablesUtils.setiTotalRecords(usersService.countAllExistsAuthorities());
        dataTablesUtils.setiTotalDisplayRecords(usersService.countByConditionExistsAuthorities(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * datatables ajax查询数据
     *
     * @param request
     * @return datatables数据
     */
    @RequestMapping(value = "/web/platform/users/wait/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<UsersBean> platformWaitDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("username");
        headers.add("mobile");
        headers.add("users_type_name");
        headers.add("lang_key");
        headers.add("join_date");
        headers.add("operator");
        DataTablesUtils<UsersBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = usersService.findAllByPageNotExistsAuthorities(dataTablesUtils);
        List<UsersBean> usersBeen = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            usersBeen = records.into(UsersBean.class);
        }
        dataTablesUtils.setData(usersBeen);
        dataTablesUtils.setiTotalRecords(usersService.countAllNotExistsAuthorities());
        dataTablesUtils.setiTotalDisplayRecords(usersService.countByConditionNotExistsAuthorities(dataTablesUtils));
        return dataTablesUtils;
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
    public AjaxUtils usersUpdateEnabled(String userIds, Byte enabled) {
        if (StringUtils.hasLength(userIds)) {
            usersService.updateEnabled(SmallPropsUtils.StringIdsToStringList(userIds), enabled);
            return new AjaxUtils().success().msg("注销用户成功");
        }
        return new AjaxUtils().fail().msg("注销用户失败");
    }

    /**
     * 删除无角色关联的用户
     *
     * @param userIds 用户账号
     * @return true 成功 false 失败
     */
    @RequestMapping("/special/channel/users/deletes")
    @ResponseBody
    public AjaxUtils deleteUsers(@RequestParam("username") String userIds) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        if (StringUtils.hasLength(userIds)) {
            List<String> ids = SmallPropsUtils.StringIdsToStringList(userIds);
            ids.forEach(id -> {
                List<AuthoritiesRecord> authoritiesRecords = authoritiesService.findByUsername(id);
                if (!ObjectUtils.isEmpty(authoritiesRecords) && !authoritiesRecords.isEmpty()) {
                    ajaxUtils.fail().msg("用户存在角色关联，无法删除");
                } else {
                    Users users = usersService.findByUsername(id);
                    UsersType usersType = usersTypeService.findByUsersTypeId(users.getUsersTypeId());
                    switch (usersType.getUsersTypeName()) {
                        case Workbook.STUDENT_USERS_TYPE:  // 学生
                            studentService.deleteByUsername(id);
                            usersService.deleteById(id);
                            ajaxUtils.success().msg("删除用户成功");
                            break;
                        case Workbook.STAFF_USERS_TYPE:  // 教职工
                            staffService.deleteByUsername(id);
                            usersService.deleteById(id);
                            ajaxUtils.success().msg("删除用户成功");
                            break;
                        default:
                            ajaxUtils.fail().msg("未获取到用户类型");
                            break;
                    }

                }
            });
        } else {
            ajaxUtils.fail().msg("用户账号为空");
        }
        return ajaxUtils;
    }

    /**
     * 用户资料页面
     *
     * @return 资料页面
     */
    @RequestMapping("/anyone/users/profile")
    public String usersProfile(ModelMap modelMap) {
        Users users = usersService.getUserFromSession();
        UsersType usersType = usersTypeService.findByUsersTypeId(users.getUsersTypeId());
        String page;
        switch (usersType.getUsersTypeName()) {
            case Workbook.STUDENT_USERS_TYPE:  // 学生
                page = "web/platform/users/users_profile_student::#page-wrapper";
                Optional<Record> student = studentService.findByUsernameRelation(users.getUsername());
                if (student.isPresent()) {
                    StudentBean studentBean = student.get().into(StudentBean.class);
                    studentBean.setAvatar("/anyone/users/download/avatar?username=" + users.getUsername());
                    modelMap.addAttribute("user", studentBean);
                }
                break;
            case Workbook.STAFF_USERS_TYPE:  // 教职工
                page = "web/platform/users/users_profile_staff::#page-wrapper";
                Optional<Record> staff = staffService.findByUsernameRelation(users.getUsername());
                if (staff.isPresent()) {
                    StaffBean staffBean = staff.get().into(StaffBean.class);
                    modelMap.addAttribute("user", staffBean);
                }
                break;
            case Workbook.SYSTEM_USERS_TYPE:  // 系统
                page = "web/platform/users/users_profile_system::#page-wrapper";
                modelMap.addAttribute("user", users);
                break;
            default:
                page = "login";
                break;
        }
        return page;
    }

    /**
     * 更新用户学校信息
     *
     * @param department 系id
     * @param organize   班级id
     * @return true 更新成功 false 更新失败
     */
    @RequestMapping(value = "/anyone/users/profile/school/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils usersProfileSchoolUpdate(@RequestParam("department") int department, @RequestParam("organize") int organize) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        Users users = usersService.getUserFromSession();
        UsersType usersType = usersTypeService.findByUsersTypeId(users.getUsersTypeId());
        switch (usersType.getUsersTypeName()) {
            case Workbook.STUDENT_USERS_TYPE:  // 学生
                Optional<Record> student = studentService.findByUsernameRelation(users.getUsername());
                if (student.isPresent()) {
                    Student updateStudent = student.get().into(Student.class);
                    updateStudent.setOrganizeId(organize);
                    studentService.update(updateStudent);
                    ajaxUtils.success().msg("更新学校信息成功");
                } else {
                    ajaxUtils.fail().msg("未查询到当前用户");
                }
                break;
            case Workbook.STAFF_USERS_TYPE:  // 教职工
                Optional<Record> staff = staffService.findByUsernameRelation(users.getUsername());
                if (staff.isPresent()) {
                    Staff updateStaff = staff.get().into(Staff.class);
                    updateStaff.setDepartmentId(department);
                    staffService.update(updateStaff);
                    ajaxUtils.success().msg("更新学校信息成功");
                } else {
                    ajaxUtils.fail().msg("未查询到当前用户");
                }
                break;
            default:
                ajaxUtils.fail().msg("未查询到当前用户的用户类型");
                break;
        }
        return ajaxUtils;
    }

    /**
     * 用户资料编辑页面
     *
     * @return 资料编辑页面
     */
    @RequestMapping("/anyone/users/profile/edit")
    public String usersProfileEdit(ModelMap modelMap) {
        Users users = usersService.getUserFromSession();
        UsersType usersType = usersTypeService.findByUsersTypeId(users.getUsersTypeId());
        String page;
        switch (usersType.getUsersTypeName()) {
            case Workbook.STUDENT_USERS_TYPE:  // 学生
                page = "web/platform/users/users_profile_student_edit::#page-wrapper";
                Optional<Record> student = studentService.findByUsernameRelation(users.getUsername());
                if (student.isPresent()) {
                    StudentBean studentBean = student.get().into(StudentBean.class);
                    studentBean.setAvatar("/anyone/users/download/avatar?username=" + users.getUsername());
                    modelMap.addAttribute("user", studentBean);
                }
                break;
            case Workbook.STAFF_USERS_TYPE:  // 教职工
                page = "web/platform/users/users_profile_staff_edit::#page-wrapper";
                Optional<Record> staff = staffService.findByUsernameRelation(users.getUsername());
                if (staff.isPresent()) {
                    StaffBean staffBean = staff.get().into(StaffBean.class);
                    modelMap.addAttribute("user", staffBean);
                }
                break;
            case Workbook.SYSTEM_USERS_TYPE:  // 系统
                page = "web/platform/users/users_profile_system_edit::#page-wrapper";
                modelMap.addAttribute("user", users);
                break;
            default:
                page = "login";
                break;
        }
        return page;
    }

    /**
     * 用户上传头像
     *
     * @param multipartHttpServletRequest
     * @param request
     * @return 文件信息
     */
    @RequestMapping(value = "/anyone/users/upload/avatar")
    @ResponseBody
    public AjaxUtils<FileBean> usersUploadAvatar(MultipartHttpServletRequest multipartHttpServletRequest, HttpServletRequest request) {
        AjaxUtils<FileBean> data = new AjaxUtils<FileBean>();
        try {
            Users users = usersService.getUserFromSession();
            List<FileBean> fileBeen = uploadService.upload(multipartHttpServletRequest,
                    avatarPath(users), request.getRemoteAddr());
            data.success().listData(fileBeen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 用户头像展示
     * @param username 用户账号
     * @param response 响应
     * @param request 请求
     */
    @RequestMapping("/anyone/users/download/avatar")
    public void downloadAvatar(@RequestParam("username") String username, HttpServletResponse response, HttpServletRequest request){
        Users users = usersService.findByUsername(StringUtils.trimWhitespace(username));
        uploadService.showImage(users.getAvatar(),response,request);
    }

    /**
     * 用户头像上传后预览
     * @param fileName 文件名
     * @param username 用户账号
     * @param response 响应
     * @param request 请求
     */
    @RequestMapping("/anyone/users/avatar/preview")
    public void downloadAvatarPreview(@RequestParam("fileName") String fileName,@RequestParam("username") String username, HttpServletResponse response, HttpServletRequest request){
        Users users = usersService.findByUsername(StringUtils.trimWhitespace(username));
        String absolutePath = avatarPath(users) + fileName;
        uploadService.showImage(absolutePath,response,request);
    }

    /**
     * 头像路径
     * @param users 用户
     * @return 路径
     */
    private String avatarPath(Users users){
        return Workbook.USERS_PORTFOLIOS + users.getUsername() + File.separator + "avatar" + File.separator;
    }
}
