package top.zbeboy.isy.web.data.student;

import org.joda.time.DateTime;
import org.jooq.Record;
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
import top.zbeboy.isy.domain.tables.records.StudentRecord;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.service.util.BCryptUtils;
import top.zbeboy.isy.service.util.DateTimeUtils;
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
import java.text.ParseException;
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
    private RoleService roleService;

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
        Student student = studentService.findByStudentNumber(StringUtils.trimWhitespace(studentNumber));
        if (!ObjectUtils.isEmpty(student)) {
            return new AjaxUtils().fail();
        }
        return new AjaxUtils().success();
    }

    /**
     * 已登录用户学号更新检验
     *
     * @param username      用户账号
     * @param studentNumber 学号
     * @return true 可以用 false 不可以
     */
    @RequestMapping(value = "/anyone/users/valid/student", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils validAnyoneStudent(@RequestParam("username") String username, @RequestParam("studentNumber") String studentNumber) {
        Result<StudentRecord> studentRecords = studentService.findByStudentNumberNeUsername(username, studentNumber);
        if (studentRecords.isEmpty()) {
            return new AjaxUtils().success();
        }
        return new AjaxUtils().fail();
    }

    /**
     * 学生注册
     *
     * @param studentVo     学生
     * @param bindingResult 检验
     * @param session       session
     * @param request       请求
     * @return true 成功 false失败
     */
    @RequestMapping(value = "/user/register/student", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils registerStudent(@Valid StudentVo studentVo, BindingResult bindingResult, HttpSession session, HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            String email = StringUtils.trimWhitespace(studentVo.getEmail());
            String mobile = StringUtils.trimWhitespace(studentVo.getMobile());
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
                                        saveUsers.setAvatar(Workbook.USERS_AVATAR);
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
        return "web/data/student/student_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
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
                user.setRoleName(roleService.findByUsernameToStringNoCache(user.getUsername()));
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
     * @param request 请求
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

    /**
     * 更新用户学校信息
     *
     * @param organize 班级id
     * @return true 更新成功 false 更新失败
     */
    @RequestMapping(value = "/anyone/users/profile/student/school/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils studentSchoolUpdate(@RequestParam("organize") int organize) {
        Users users = usersService.getUserFromSession();
        Student student = studentService.findByUsername(users.getUsername());
        student.setOrganizeId(organize);
        studentService.update(student);
        return new AjaxUtils().success().msg("更新学校信息成功");
    }

    /**
     * 更新基本信息
     *
     * @param studentVo     学生信息
     * @param bindingResult 检验
     * @return true or false
     */
    @RequestMapping(value = "/anyone/users/student/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils studentUpdate(@Valid top.zbeboy.isy.web.vo.platform.users.StudentVo studentVo, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            try {
                Users users = usersService.findByUsername(studentVo.getUsername());
                String realName = studentVo.getRealName();
                String avatar = studentVo.getAvatar();
                if (StringUtils.hasLength(realName)) {
                    users.setRealName(realName);
                } else {
                    users.setRealName(null);
                }
                if (StringUtils.hasLength(avatar)) {
                    users.setAvatar(studentVo.getAvatar());
                } else {
                    users.setAvatar(Workbook.USERS_AVATAR);
                }
                usersService.update(users);

                Student student = studentService.findByUsername(studentVo.getUsername());
                student.setStudentNumber(studentVo.getStudentNumber());
                student.setSex(studentVo.getSex());
                student.setNationId(studentVo.getNationId());
                student.setPoliticalLandscapeId(studentVo.getPoliticalLandscapeId());
                if (StringUtils.hasLength(studentVo.getBirthday())) {
                    student.setBirthday(DateTimeUtils.formatDate(studentVo.getBirthday()));
                } else {
                    student.setBirthday(null);
                }
                student.setDormitoryNumber(studentVo.getDormitoryNumber());
                if(StringUtils.hasLength(studentVo.getIdCard())){
                    student.setIdCard(studentVo.getIdCard());
                } else {
                    student.setIdCard(null);
                }
                student.setFamilyResidence(studentVo.getFamilyResidence());
                student.setParentName(studentVo.getParentName());
                student.setParentContactPhone(studentVo.getParentContactPhone());
                student.setPlaceOrigin(studentVo.getPlaceOrigin());
                studentService.update(student);
                return new AjaxUtils().success();
            } catch (ParseException e) {
                log.error("Birthday to sql date is exception : {}", e.getMessage());
                return new AjaxUtils().fail().msg("时间转换异常");
            }
        }
        return new AjaxUtils().fail().msg("参数检验错误");
    }
}
