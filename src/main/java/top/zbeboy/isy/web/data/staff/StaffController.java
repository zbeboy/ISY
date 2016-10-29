package top.zbeboy.isy.web.data.staff;

import org.joda.time.DateTime;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.ISYProperties;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Staff;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.MailService;
import top.zbeboy.isy.service.StaffService;
import top.zbeboy.isy.service.UsersService;
import top.zbeboy.isy.service.UsersTypeService;
import top.zbeboy.isy.service.util.BCryptUtils;
import top.zbeboy.isy.service.util.RandomUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.register.staff.StaffVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2016-08-28.
 */
@Controller
public class StaffController {

    private final Logger log = LoggerFactory.getLogger(StaffController.class);

    @Resource
    private StaffService staffService;

    @Resource
    private UsersService usersService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private MailService mailService;

    @Autowired
    private ISYProperties isyProperties;

    @Autowired
    private RequestUtils requestUtils;

    /**
     * 判断工号是否已被注册
     *
     * @param staffNumber 工号
     * @return true未被注册 false已被注册
     */
    @RequestMapping(value = "/user/register/valid/staff", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils validStaff(@RequestParam("staffNumber") String staffNumber) {
        List<Staff> staffs = staffService.findByStaffNumber(staffNumber);
        if (!staffs.isEmpty()) {
            return new AjaxUtils().fail();
        }
        return new AjaxUtils().success();
    }

    /**
     * 教职工注册
     *
     * @param staffVo
     * @param bindingResult
     * @param session
     * @param request
     * @return true 注册成功 false注册失败
     */
    @RequestMapping(value = "/user/register/staff", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils registerStaff(@Valid StaffVo staffVo, BindingResult bindingResult, HttpSession session, HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            List<Staff> staffs = staffService.findByStaffNumber(StringUtils.trimWhitespace(staffVo.getStaffNumber()));
            String email = StringUtils.trimWhitespace(staffVo.getEmail());
            String mobile = StringUtils.trimWhitespace(staffVo.getMobile());
            if (!staffs.isEmpty()) {
                return new AjaxUtils().fail().msg("工号已被注册");
            } else {
                Users users = usersService.findByUsername(email);
                if (!ObjectUtils.isEmpty(users)) {
                    return new AjaxUtils().fail().msg("邮箱已被注册");
                } else {
                    List<Users> mobiles = usersService.findByMobile(mobile);
                    if (!mobiles.isEmpty()) {
                        return new AjaxUtils().fail().msg("手机号已被注册");
                    } else {
                        if (!ObjectUtils.isEmpty(session.getAttribute("mobile"))) {
                            String tempMobile = (String) session.getAttribute("mobile");
                            if (!staffVo.getMobile().equals(tempMobile)) {
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
                                            if (!staffVo.getPhoneVerifyCode().equals(mobileCode)) {
                                                return new AjaxUtils().fail().msg("验证码错误");
                                            } else {
                                                String password = StringUtils.trimWhitespace(staffVo.getPassword());
                                                String confirmPassword = StringUtils.trimWhitespace(staffVo.getConfirmPassword());
                                                if (!password.equals(confirmPassword)) {
                                                    return new AjaxUtils().fail().msg("密码不一致");
                                                } else {
                                                    // 注册成功
                                                    Users saveUsers = new Users();
                                                    Byte enabled = 1;
                                                    saveUsers.setUsername(email);
                                                    saveUsers.setEnabled(enabled);
                                                    saveUsers.setMobile(mobile);
                                                    saveUsers.setPassword(BCryptUtils.bCryptPassword(password));
                                                    saveUsers.setUsersTypeId(usersTypeService.findByUsersTypeName(Workbook.STAFF_USERS_TYPE).getUsersTypeId());
                                                    saveUsers.setJoinDate(new java.sql.Date(System.currentTimeMillis()));

                                                    DateTime dateTime = DateTime.now();
                                                    dateTime = dateTime.plusDays(5);
                                                    String mailboxVerifyCode = RandomUtils.generateEmailCheckKey();
                                                    saveUsers.setMailboxVerifyCode(mailboxVerifyCode);
                                                    saveUsers.setMailboxVerifyValid(new Timestamp(dateTime.toDate().getTime()));
                                                    saveUsers.setLangKey(request.getLocale().toLanguageTag());
                                                    usersService.save(saveUsers);

                                                    Staff saveStaff = new Staff();
                                                    saveStaff.setDepartmentId(staffVo.getDepartment());
                                                    saveStaff.setStaffNumber(staffVo.getStaffNumber());
                                                    saveStaff.setUsername(email);
                                                    staffService.save(saveStaff);

                                                    //清空session
                                                    session.removeAttribute("mobile");
                                                    session.removeAttribute("mobileExpiry");
                                                    session.removeAttribute("mobileCode");

                                                    //发送验证邮件
                                                    if (isyProperties.getMail().isOpen()) {
                                                        mailService.sendValidEmailMail(saveUsers, requestUtils.getBaseUrl(request));
                                                        return new AjaxUtils().success().msg("恭喜注册成功，请验证邮箱");
                                                    } else {
                                                        return new AjaxUtils().fail().msg("邮件推送已被管理员关闭");
                                                    }
                                                }
                                            }
                                        } else {
                                            return new AjaxUtils().fail().msg("无法获取当前用户电话验证码，请重新获取手机验证码");
                                        }
                                    }
                                } else {
                                    return new AjaxUtils().fail().msg("无法获取当前用户验证码有效期，请重新获取手机验证码");
                                }
                            }
                        } else {
                            return new AjaxUtils().fail().msg("无法获取当前用户电话，请重新获取手机验证码");
                        }
                    }
                }
            }
        } else {
            return new AjaxUtils().fail().msg("参数异常，请检查输入内容是否正确");
        }
    }

    /**
     * 教职工数据
     *
     * @return 教职工数据页面
     */
    @RequestMapping(value = "/web/menu/data/staff", method = RequestMethod.GET)
    public String staffData() {
        return "web/data/staff/staff_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/staff/pass/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<StaffBean> staffPassDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("staff_number");
        headers.add("real_name");
        headers.add("username");
        headers.add("mobile");
        headers.add("id_card");
        headers.add("role_name");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("department_name");
        headers.add("post");
        headers.add("sex");
        headers.add("birthday");
        headers.add("nation_name");
        headers.add("politicalLandscape_name");
        headers.add("family_residence");
        headers.add("enabled");
        headers.add("lang_key");
        headers.add("join_date");
        headers.add("operator");
        DataTablesUtils<StaffBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = staffService.findAllByPageExistsAuthorities(dataTablesUtils);
        List<StaffBean> staffBeen = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            staffBeen = records.into(StaffBean.class);
            staffBeen.forEach(user -> {
                Result<Record1<String>> record1s = usersService.findByUsernameWithRoleNoCache(user.getUsername());
                StringBuilder stringBuilder = new StringBuilder();
                for (Record r : record1s) {
                    stringBuilder.append(r.getValue(0)).append(" ");
                }
                user.setRoleName(stringBuilder.toString());
            });
        }
        dataTablesUtils.setData(staffBeen);
        dataTablesUtils.setiTotalRecords(staffService.countAllExistsAuthorities());
        dataTablesUtils.setiTotalDisplayRecords(staffService.countByConditionExistsAuthorities(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * datatables ajax查询数据
     *
     * @param request
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/staff/wait/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<StaffBean> staffWaitDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("staff_number");
        headers.add("username");
        headers.add("mobile");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("department_name");
        headers.add("lang_key");
        headers.add("join_date");
        headers.add("operator");
        DataTablesUtils<StaffBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = staffService.findAllByPageNotExistsAuthorities(dataTablesUtils);
        List<StaffBean> usersBeen = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            usersBeen = records.into(StaffBean.class);
        }
        dataTablesUtils.setData(usersBeen);
        dataTablesUtils.setiTotalRecords(staffService.countAllNotExistsAuthorities());
        dataTablesUtils.setiTotalDisplayRecords(staffService.countByConditionNotExistsAuthorities(dataTablesUtils));
        return dataTablesUtils;
    }
}
