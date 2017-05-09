package top.zbeboy.isy.web.graduate.design.teacher;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclareData;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.OrganizeRecord;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.OrganizeService;
import top.zbeboy.isy.service.data.ScienceService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignDeclareDataService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.data.science.ScienceBean;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/5/8.
 */
@Controller
public class GraduationDesignTeacherController {

    private final Logger log = LoggerFactory.getLogger(GraduationDesignTeacherController.class);

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    @Resource
    private GraduationDesignDeclareDataService graduationDesignDeclareDataService;

    @Resource
    private OrganizeService organizeService;

    @Resource
    private ScienceService scienceService;

    @Resource
    private StudentService studentService;

    @Resource
    private UsersService usersService;

    /**
     * 毕业指导教师
     *
     * @return 毕业指导教师页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/tutor", method = RequestMethod.GET)
    public String teacherData() {
        return "web/graduate/design/teacher/design_teacher::#page-wrapper";
    }

    /**
     * 毕业指导教师列表页
     *
     * @return 毕业指导教师列表页
     */
    @RequestMapping(value = "/web/graduate/design/tutor/look", method = RequestMethod.GET)
    public String teacherList(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
        return "web/graduate/design/teacher/design_teacher_look::#page-wrapper";
    }

    /**
     * 毕业指导教师添加页
     *
     * @return 毕业指导教师添加页
     */
    @RequestMapping(value = "/web/graduate/design/tutor/add", method = RequestMethod.GET)
    public String teacherAdd(@RequestParam("rId") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            page = "web/graduate/design/teacher/design_teacher_add::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 毕业指导教师列表数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/tutor/look/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignTeacherBean> listData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("real_name");
        headers.add("staff_number");
        headers.add("staff_username");
        headers.add("assigner_name");
        headers.add("operator");
        DataTablesUtils<GraduationDesignTeacherBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        GraduationDesignTeacherBean otherCondition = new GraduationDesignTeacherBean();
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
            List<GraduationDesignTeacherBean> graduationDesignTeacherBeens = graduationDesignTeacherService.findAllByPage(dataTablesUtils, otherCondition);
            dataTablesUtils.setData(graduationDesignTeacherBeens);
            dataTablesUtils.setiTotalRecords(graduationDesignTeacherService.countAll(otherCondition));
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignTeacherService.countByCondition(dataTablesUtils, otherCondition));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }

    /**
     * 毕业设计指导教师 所需教师数据
     *
     * @param graduationDesignReleaseId 实习id
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/tutor/teachers", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<StaffBean> teachers(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils<StaffBean> ajaxUtils = new AjaxUtils<>();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            List<StaffBean> staffs = new ArrayList<>();
            Byte enabled = 1;
            Result<Record> staffRecords =
                    graduationDesignTeacherService.findByDepartmentIdAndEnabledRelationExistsAuthoritiesNotExistsDesignTeacher(graduationDesignRelease.getDepartmentId(),
                            enabled, graduationDesignReleaseId);
            if (staffRecords.isNotEmpty()) {
                staffs = staffRecords.into(StaffBean.class);
            }
            ajaxUtils.success().msg("获取教师数据成功").listData(staffs);
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据获取，请返回首页");
        }
        return ajaxUtils;
    }

    /**
     * 添加指导教师
     *
     * @param staffId                   教职工id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/tutor/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils add(String staffId, String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(staffId) && SmallPropsUtils.StringIdsIsNumber(staffId)) {
                GraduationDesignRelease graduationDesignRelease = errorBean.getData();
                // 查询出专业名，系名
                Optional<Record> science = scienceService.findByIdRelation(graduationDesignRelease.getScienceId());
                if (science.isPresent()) {
                    ScienceBean scienceBean = science.get().into(ScienceBean.class);
                    Byte isDel = 0;
                    // 查询出专业下所有班级
                    Result<OrganizeRecord> organizeRecords =
                            organizeService.findByScienceIdAndGradeAndIsDel(graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade(), isDel);
                    // 各班级学生人数
                    StringBuilder organizeNames = new StringBuilder();
                    StringBuilder organizePeoples = new StringBuilder();
                    int totalPeoples = 0;
                    Byte enabled = 1;
                    String sp = "###";
                    for (OrganizeRecord organize : organizeRecords) {
                        organizeNames.append(organize.getOrganizeName()).append(sp);
                        int peoples = studentService.countByOrganizeIdAndEnabledExistsAuthorities(organize.getOrganizeId(), enabled);
                        organizePeoples.append(peoples).append(sp);
                        totalPeoples += peoples;
                    }

                    graduationDesignDeclareDataService.deleteByGraduationDesignReleaseId(graduationDesignReleaseId);
                    GraduationDesignDeclareData graduationDesignDeclareData = new GraduationDesignDeclareData();
                    graduationDesignDeclareData.setGraduationDesignDeclareDataId(UUIDUtils.getUUID());
                    graduationDesignDeclareData.setDepartmentName(scienceBean.getDepartmentName());
                    graduationDesignDeclareData.setScienceName(scienceBean.getScienceName());
                    graduationDesignDeclareData.setOrganizeNames(organizeNames.substring(0, organizeNames.lastIndexOf(sp)));
                    graduationDesignDeclareData.setOrganizePeoples(organizePeoples.substring(0, organizePeoples.lastIndexOf(sp)));
                    graduationDesignDeclareData.setGraduationDesignReleaseId(graduationDesignReleaseId);
                    graduationDesignDeclareDataService.save(graduationDesignDeclareData);

                    // 保存指导教师数据
                    Users users = usersService.getUserFromSession();
                    graduationDesignTeacherService.deleteByGraduationDesignReleaseId(graduationDesignReleaseId);
                    List<Integer> staffIds = SmallPropsUtils.StringIdsToList(staffId);

                    //  计算出平均带人数
                    int average = totalPeoples / staffIds.size();
                    // 余数
                    int remainder = totalPeoples % staffIds.size();

                    for (Integer id : staffIds) {
                        GraduationDesignTeacher graduationDesignTeacher = new GraduationDesignTeacher();
                        graduationDesignTeacher.setGraduationDesignTeacherId(UUIDUtils.getUUID());
                        graduationDesignTeacher.setGraduationDesignReleaseId(graduationDesignReleaseId);
                        graduationDesignTeacher.setStaffId(id);
                        graduationDesignTeacher.setUsername(users.getUsername());
                        if (remainder > 0) {
                            graduationDesignTeacher.setStudentCount(average + 1);
                            remainder--;
                        } else {
                            graduationDesignTeacher.setStudentCount(average);
                        }
                        graduationDesignTeacherService.save(graduationDesignTeacher);
                    }
                    ajaxUtils.success().msg("添加指导教师成功");
                } else {
                    ajaxUtils.fail().msg("未查询到相关信息");
                }
            } else {
                ajaxUtils.fail().msg("参数异常");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入指导教师入口条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> accessCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = new ErrorBean<>();
        GraduationDesignRelease graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId);
        if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
            errorBean.setData(graduationDesignRelease);
            if (graduationDesignRelease.getGraduationDesignIsDel() == 1) {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("该毕业设计已被注销");
            } else {
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    errorBean.setHasError(false);
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("不在毕业设计时间范围，无法进入");
                }
                // 学生填报教师时间
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getFillTeacherStartTime(), graduationDesignRelease.getFillTeacherEndTime())) {
                    errorBean.setHasError(false);
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("学生申报指导教师已开始，操作锁定");
                }
            }
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询到相关毕业设计信息");
        }
        return errorBean;
    }
}
