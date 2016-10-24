package top.zbeboy.isy.web.data.student;

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
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.MailService;
import top.zbeboy.isy.service.StudentService;
import top.zbeboy.isy.service.UsersService;
import top.zbeboy.isy.service.UsersTypeService;
import top.zbeboy.isy.service.util.BCryptUtils;
import top.zbeboy.isy.service.util.RandomUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.register.student.StudentVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2016-08-22.
 */
@Controller
public class StudentController {

    private final Logger log = LoggerFactory.getLogger(StudentController.class);

    @Resource
    private StudentService studentService;

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
     * 判断学号是否已被注册
     *
     * @param studentNumber 学号
     * @return true未被注册 false已被注册
     */
    @RequestMapping(value = "/user/register/valid/student", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils validStudent(@RequestParam("studentNumber") String studentNumber) {
        List<Student> students = studentService.findByStudentNumber(StringUtils.trimWhitespace(studentNumber));
        if (!students.isEmpty()) {
            return new AjaxUtils().fail();
        }
        return new AjaxUtils().success();
    }

    /**
     * 学生注册
     *
     * @param studentVo     页面 form表单字段
     * @param bindingResult 是否有错
     * @param session
     * @param request
     * @return true 成功 false失败
     */
    @RequestMapping(value = "/user/register/student", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils registerStudent(@Valid StudentVo studentVo, BindingResult bindingResult, HttpSession session, HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            List<Student> students = studentService.findByStudentNumber(StringUtils.trimWhitespace(studentVo.getStudentNumber()));
            String email = StringUtils.trimWhitespace(studentVo.getEmail());
            String mobile = StringUtils.trimWhitespace(studentVo.getMobile());
            if (!students.isEmpty()) {
                return new AjaxUtils().fail().msg("学号已被注册");
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
                            if (!studentVo.getMobile().equals(tempMobile)) {
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
                                            if (!studentVo.getPhoneVerifyCode().equals(mobileCode)) {
                                                return new AjaxUtils().fail().msg("验证码错误");
                                            } else {
                                                String password = StringUtils.trimWhitespace(studentVo.getPassword());
                                                String confirmPassword = StringUtils.trimWhitespace(studentVo.getConfirmPassword());
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
                                                    saveUsers.setUsersTypeId(usersTypeService.findByUsersTypeName(Workbook.STUDENT_USERS_TYPE).getUsersTypeId());
                                                    saveUsers.setJoinDate(new java.sql.Date(System.currentTimeMillis()));

                                                    DateTime dateTime = DateTime.now();
                                                    dateTime = dateTime.plusDays(5);
                                                    String mailboxVerifyCode = RandomUtils.generateEmailCheckKey();
                                                    saveUsers.setMailboxVerifyCode(mailboxVerifyCode);
                                                    saveUsers.setMailboxVerifyValid(new Timestamp(dateTime.toDate().getTime()));
                                                    saveUsers.setLangKey(request.getLocale().toLanguageTag());
                                                    usersService.save(saveUsers);

                                                    Student saveStudent = new Student();
                                                    saveStudent.setOrganizeId(studentVo.getOrganize());
                                                    saveStudent.setStudentNumber(studentVo.getStudentNumber());
                                                    saveStudent.setUsername(email);
                                                    studentService.save(saveStudent);

                                                    //清空session
                                                    session.removeAttribute("mobileExpiry");
                                                    session.removeAttribute("mobile");
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
     * 学生数据
     *
     * @return 学生数据页面
     */
    @RequestMapping(value = "/web/menu/data/student", method = RequestMethod.GET)
    public String studentData() {
        return "web/data/student/student_data";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/student/pass/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<StudentBean> studentPassDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("student_number");
        headers.add("real_name");
        headers.add("username");
        headers.add("mobile");
        headers.add("id_card");
        headers.add("role_name");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("department_name");
        headers.add("science_name");
        headers.add("grade");
        headers.add("organize_name");
        headers.add("sex");
        headers.add("birthday");
        headers.add("nation_name");
        headers.add("politicalLandscape_name");
        headers.add("dormitory_number");
        headers.add("place_origin");
        headers.add("parent_name");
        headers.add("parent_contact_phone");
        headers.add("family_residence");
        headers.add("enabled");
        headers.add("lang_key");
        headers.add("join_date");
        headers.add("operator");
        DataTablesUtils<StudentBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = studentService.findAllByPageExistsAuthorities(dataTablesUtils);
        List<StudentBean> studentBeen = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            studentBeen = records.into(StudentBean.class);
            studentBeen.forEach(user -> {
                Result<Record1<String>> record1s = usersService.findByUsernameWithRoleNoCache(user.getUsername());
                StringBuilder stringBuilder = new StringBuilder();
                for (Record r : record1s) {
                    stringBuilder.append(r.getValue(0)).append(" ");
                }
                user.setRoleName(stringBuilder.toString());
            });
        }
        dataTablesUtils.setData(studentBeen);
        dataTablesUtils.setiTotalRecords(studentService.countAllExistsAuthorities());
        dataTablesUtils.setiTotalDisplayRecords(studentService.countByConditionExistsAuthorities(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * datatables ajax查询数据
     *
     * @param request
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/student/wait/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<StudentBean> studentWaitDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("student_number");
        headers.add("username");
        headers.add("mobile");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("department_name");
        headers.add("science_name");
        headers.add("grade");
        headers.add("organize_name");
        headers.add("lang_key");
        headers.add("join_date");
        headers.add("operator");
        DataTablesUtils<StudentBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = studentService.findAllByPageNotExistsAuthorities(dataTablesUtils);
        List<StudentBean> usersBeen = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            usersBeen = records.into(StudentBean.class);
        }
        dataTablesUtils.setData(usersBeen);
        dataTablesUtils.setiTotalRecords(studentService.countAllNotExistsAuthorities());
        dataTablesUtils.setiTotalDisplayRecords(studentService.countByConditionNotExistsAuthorities(dataTablesUtils));
        return dataTablesUtils;
    }
}
