package top.zbeboy.isy.web.internship.distribution;

import org.jooq.Record;
import org.jooq.Record1;
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
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.domain.tables.records.InternshipReleaseScienceRecord;
import top.zbeboy.isy.domain.tables.records.OrganizeRecord;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.util.PaginationUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by zbeboy on 2016/11/21.
 */
@Controller
public class InternshipTeacherDistributionController {

    private final Logger log = LoggerFactory.getLogger(InternshipTeacherDistributionController.class);

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipTeacherDistributionService internshipTeacherDistributionService;

    @Resource
    private InternshipReleaseScienceService internshipReleaseScienceService;

    @Resource
    private OrganizeService organizeService;

    @Resource
    private StaffService staffService;

    @Resource
    private StudentService studentService;

    @Resource
    private UsersService usersService;

    /**
     * 实习教师分配
     *
     * @return 实习教师分配页面
     */
    @RequestMapping(value = "/web/menu/internship/teacher_distribution", method = RequestMethod.GET)
    public String teacherDistribution() {
        return "/web/internship/distribution/internship_teacher_distribution::#page-wrapper";
    }

    /**
     * 获取实习教师分配数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<InternshipReleaseBean> distributionDatas(PaginationUtils paginationUtils) {
        Byte isDel = 0;
        InternshipRelease internshipRelease = new InternshipRelease();
        internshipRelease.setInternshipReleaseIsDel(isDel);
        Result<Record> records = internshipReleaseService.findAllByPage(paginationUtils, internshipRelease);
        List<InternshipReleaseBean> internshipReleaseBeens = internshipReleaseService.dealData(paginationUtils, records, internshipRelease);
        return new AjaxUtils<InternshipReleaseBean>().success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils);
    }

    /**
     * 分配情况页面
     *
     * @param internshipReleaseId 实习发布id
     * @return 页面
     */
    @RequestMapping("/web/internship/teacher_distribution/distribution/condition")
    public String distributionCondition(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        // TODO:未判断时间条件
        return "/web/internship/distribution/internship_distribution_condition::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/distribution/condition/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<InternshipTeacherDistributionBean> distributionConditionDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("internship_title");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("department_name");
        headers.add("student_real_name");
        headers.add("student_username");
        headers.add("student_number");
        headers.add("teacher_real_name");
        headers.add("teacher_username");
        headers.add("teacher_number");
        headers.add("real_name");
        headers.add("username");
        headers.add("operator");
        String internshipReleaseId = request.getParameter("internshipReleaseId");
        DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        List<InternshipTeacherDistributionBean> internshipTeacherDistributionBeens = internshipTeacherDistributionService.findAllByPage(dataTablesUtils, internshipReleaseId);
        dataTablesUtils.setData(internshipTeacherDistributionBeens);
        dataTablesUtils.setiTotalRecords(internshipTeacherDistributionService.countAll(internshipReleaseId));
        dataTablesUtils.setiTotalDisplayRecords(internshipTeacherDistributionService.countByCondition(dataTablesUtils, internshipReleaseId));
        return dataTablesUtils;
    }

    /**
     * 添加
     *
     * @param internshipReleaseId 实习id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/distribution/condition/add", method = RequestMethod.GET)
    public String addDistribution(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        return "/web/internship/distribution/internship_add_distribution::#page-wrapper";
    }

    /**
     * 编辑
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/distribution/condition/edit", method = RequestMethod.GET)
    public String editDistribution(@RequestParam("id") String internshipReleaseId, @RequestParam("studentId") int studentId, ModelMap modelMap) {
        InternshipTeacherDistribution internshipTeacherDistribution;
        Student student = null;
        Optional<Record> record = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
        if (record.isPresent()) {
            internshipTeacherDistribution = record.get().into(InternshipTeacherDistribution.class);
            modelMap.addAttribute("internshipReleaseId", internshipTeacherDistribution.getInternshipReleaseId());
            modelMap.addAttribute("staffId", internshipTeacherDistribution.getStaffId());
            Optional<Record> studentRecord = studentService.findByIdRelation(studentId);
            if (studentRecord.isPresent()) {
                student = studentRecord.get().into(Student.class);
            }
        }
        modelMap.addAttribute("student", student);
        return "/web/internship/distribution/internship_edit_distribution::#page-wrapper";
    }

    /**
     * 批量分配
     *
     * @param internshipReleaseId 实习id
     * @param modelMap            页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/batch/distribution", method = RequestMethod.GET)
    public String batchDistribution(@RequestParam("id") String internshipReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("internshipReleaseId", internshipReleaseId);
        return "/web/internship/distribution/internship_batch_distribution::#page-wrapper";
    }

    /**
     * 保存时检验学生账号信息
     *
     * @param internshipReleaseId 实习发布Id
     * @param info                信息
     * @param type                检验类型
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/save/valid/student", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils validStudent(@RequestParam("id") String internshipReleaseId, @RequestParam("student") String info, int type) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        int departmentId = internshipRelease.getDepartmentId();
        Optional<Record> record = Optional.empty();
        if (type == 0) {
            record = studentService.findByUsernameAndDepartmentId(info, departmentId);
        } else if (type == 1) {
            record = studentService.findByStudentNumberAndDepartmentId(info, departmentId);
        }
        if (record.isPresent()) {
            Student student = record.get().into(Student.class);
            Optional<Record> distribution = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, student.getStudentId());
            if (distribution.isPresent()) {
                ajaxUtils.fail().msg("该学生账号已分配指导教师");
            } else {
                ajaxUtils.success().msg("可分配");
            }
        } else {
            ajaxUtils.fail().msg("参数有误");
        }
        return ajaxUtils;
    }

    /**
     * 批量分配 所需班级数据
     *
     * @param internshipReleaseId 实习id
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/batch/distribution/organizes", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<Organize> batchDistributionOrganizes(@RequestParam("id") String internshipReleaseId) {
        List<Organize> organizes = new ArrayList<>();
        List<Integer> hasOrganizes = new ArrayList<>();
        Result<InternshipReleaseScienceRecord> records = internshipReleaseScienceService.findByInternshipReleaseId(internshipReleaseId);
        if (records.isNotEmpty()) {
            List<Integer> scienceIds = new ArrayList<>();
            records.forEach(id -> scienceIds.add(id.getScienceId()));
            Result<OrganizeRecord> organizeRecords = organizeService.findInScienceIds(scienceIds);
            if (organizeRecords.isNotEmpty()) {
                organizes = organizeRecords.into(Organize.class);
            }
            Result<Record1<Integer>> record1s = internshipTeacherDistributionService.findByInternshipReleaseIdDistinctOrganizeId(internshipReleaseId);
            if (record1s.isNotEmpty()) {
                record1s.forEach(r -> hasOrganizes.add(r.value1()));
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("listResult", organizes);
        result.put("hasOrganizes", hasOrganizes);
        return new AjaxUtils<Organize>().success().msg("获取班级数据成功").mapData(result);
    }

    /**
     * 批量分配 所需教师数据
     *
     * @param internshipReleaseId 实习id
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/batch/distribution/teachers", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<Staff> batchDistributionTeachers(@RequestParam("id") String internshipReleaseId) {
        List<Staff> staffs = new ArrayList<>();
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        if (!ObjectUtils.isEmpty(internshipRelease)) {
            int departmentId = internshipRelease.getDepartmentId();
            staffs = staffService.findByDepartmentId(departmentId);
        }
        return new AjaxUtils<Staff>().success().msg("获取教师数据成功").listData(staffs);
    }

    /**
     * 保存教师分配
     *
     * @param internshipReleaseId 实习id
     * @param organizeId          班级ids
     * @param staffId             教职工ids
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/batch/distribution/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils batchDistributionSave(@RequestParam("id") String internshipReleaseId, String organizeId, String staffId) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        if (StringUtils.hasLength(organizeId) && StringUtils.hasLength(staffId)
                && SmallPropsUtils.StringIdsIsNumber(organizeId) && SmallPropsUtils.StringIdsIsNumber(staffId)) {
            List<Integer> organizeIds = SmallPropsUtils.StringIdsToList(organizeId);
            List<Integer> staffIds = SmallPropsUtils.StringIdsToList(staffId);
            int i = 0;
            Users users = usersService.getUserFromSession();
            for (Integer id : organizeIds) {
                List<Student> students = studentService.findByOrganizeId(id);
                for (Student s : students) {
                    if (i >= staffIds.size()) {
                        i = 0;
                    }
                    int tempStaffId = staffIds.get(i);
                    InternshipTeacherDistribution internshipTeacherDistribution =
                            new InternshipTeacherDistribution(tempStaffId, s.getStudentId(), internshipReleaseId, users.getUsername());
                    internshipTeacherDistributionService.save(internshipTeacherDistribution);
                    i++;
                }
            }
            ajaxUtils.success().msg("保存成功");
        } else {
            ajaxUtils.fail().msg("保存失败，参数异常");
        }
        return ajaxUtils;
    }

    /**
     * 单个添加保存
     *
     * @param info                学生信息
     * @param staffId             教职工id
     * @param internshipReleaseId 实习发布id
     * @param type                检验类型
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils save(@RequestParam("student") String info, @RequestParam("staffId") int staffId,
                          @RequestParam("id") String internshipReleaseId, int type) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        int departmentId = internshipRelease.getDepartmentId();
        Optional<Record> record = Optional.empty();
        Users users = usersService.getUserFromSession();
        if (type == 0) {
            record = studentService.findByUsernameAndDepartmentId(info, departmentId);
        } else if (type == 1) {
            record = studentService.findByStudentNumberAndDepartmentId(info, departmentId);
        }
        if (record.isPresent()) {
            Student student = record.get().into(Student.class);
            InternshipTeacherDistribution internshipTeacherDistribution =
                    new InternshipTeacherDistribution(staffId, student.getStudentId(), internshipReleaseId, users.getUsername());
            internshipTeacherDistributionService.save(internshipTeacherDistribution);
            ajaxUtils.success().msg("保存成功");
        } else {
            ajaxUtils.fail().msg("保存失败，参数有误");
        }
        return ajaxUtils;
    }

    /**
     * 更新
     *
     * @param studentId           学生id
     * @param staffId             教职工id
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/update", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils update(@RequestParam("studentId") int studentId, @RequestParam("staffId") int staffId,
                            @RequestParam("id") String internshipReleaseId) {
        AjaxUtils ajaxUtils = new AjaxUtils();
        Optional<Record> record = internshipTeacherDistributionService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
        if (record.isPresent()) {
            InternshipTeacherDistribution internshipTeacherDistribution = record.get().into(InternshipTeacherDistribution.class);
            internshipTeacherDistribution.setStaffId(staffId);
            internshipTeacherDistributionService.updateStaffId(internshipTeacherDistribution);
            ajaxUtils.success().msg("保存成功");
        } else {
            ajaxUtils.fail().msg("保存失败，参数有误");
        }
        return ajaxUtils;
    }

    /**
     * 批量更改删除
     *
     * @param studentIds          学生ids
     * @param internshipReleaseId 实习id
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/teacher_distribution/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils collegeUpdateDel(String studentIds, @RequestParam("id") String internshipReleaseId) {
        if (StringUtils.hasLength(studentIds) && SmallPropsUtils.StringIdsIsNumber(studentIds)) {
            List<Integer> ids = SmallPropsUtils.StringIdsToList(studentIds);
            ids.forEach(id ->
                    internshipTeacherDistributionService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, id)
            );
            return new AjaxUtils().success().msg("删除成功");
        }
        return new AjaxUtils().fail().msg("删除失败");
    }

}
