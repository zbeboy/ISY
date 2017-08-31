/*
 * Copyright (ISY Team) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package top.zbeboy.isy.rest.internship.release;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.zbeboy.isy.domain.tables.pojos.Department;
import top.zbeboy.isy.service.data.StaffService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.rest.internship.InternshipReleaseRestService;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class InternshipReleaseRestController {

    @Resource
    private InternshipReleaseRestService internshipReleaseRestService;

    @Resource
    private StudentService studentService;

    @Resource
    private StaffService staffService;

    /**
     * 获取最近几条件实习数据
     *
     * @param paginationUtils 分页工具
     * @return 实习数据
     */
    @GetMapping("/rest/internship/release/new/data")
    public AjaxUtils internshipNewData(PaginationUtils paginationUtils) {
        AjaxUtils<InternshipReleaseBean> ajaxUtils = AjaxUtils.of();
        Result<Record> records = internshipReleaseRestService.findAllByPage(paginationUtils);
        List<InternshipReleaseBean> internshipReleaseBeens = internshipReleaseRestService.dealData(records);
        return ajaxUtils.success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils);
    }

    /**
     * 为学生获取最新实习数据
     *
     * @param user            用户凭证
     * @param paginationUtils 分页工具
     * @return 实习数据
     */
    @GetMapping("/rest/internship/release/student/data")
    public AjaxUtils internshipStudentData(Principal user, PaginationUtils paginationUtils) {
        AjaxUtils<InternshipReleaseBean> ajaxUtils = AjaxUtils.of();
        if (!ObjectUtils.isEmpty(user) && StringUtils.isNotBlank(user.getName())) {
            Optional<Record> student = studentService.findByUsernameRelation(user.getName());
            if (student.isPresent()) {
                StudentBean studentBean = student.get().into(StudentBean.class);
                Result<Record> records = internshipReleaseRestService.findAllByPageForStudent(paginationUtils, studentBean.getScienceId(), studentBean.getGrade());
                List<InternshipReleaseBean> internshipReleaseBeens = new ArrayList<>();
                if (records.isNotEmpty()) {
                    internshipReleaseBeens = records.into(InternshipReleaseBean.class);
                }
                ajaxUtils.success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils).obj(studentBean.getScienceName());
            } else {
                ajaxUtils.fail().msg("查询学生信息失败");
            }
        } else {
            ajaxUtils.fail().msg("未获取到用户相关信息");
        }
        return ajaxUtils;
    }

    /**
     * 为教职工获取最新实习数据
     *
     * @param user            用户凭证
     * @param paginationUtils 分页工具
     * @return 实习数据
     */
    @GetMapping("/rest/internship/release/staff/data")
    public AjaxUtils internshipStaffData(Principal user, PaginationUtils paginationUtils) {
        AjaxUtils<InternshipReleaseBean> ajaxUtils = AjaxUtils.of();
        if (!ObjectUtils.isEmpty(user) && StringUtils.isNotBlank(user.getName())) {
            Optional<Record> staff = staffService.findByUsernameRelation(user.getName());
            if (staff.isPresent()) {
                Department department = staff.get().into(Department.class);
                Result<Record> records = internshipReleaseRestService.findAllByPageForStaff(paginationUtils, department.getDepartmentId());
                List<InternshipReleaseBean> internshipReleaseBeens = internshipReleaseRestService.dealData(records);
                ajaxUtils.success().msg("获取数据成功").listData(internshipReleaseBeens).paginationUtils(paginationUtils);
            } else {
                ajaxUtils.fail().msg("查询教职工信息失败");
            }
        } else {
            ajaxUtils.fail().msg("未获取到用户相关信息");
        }
        return ajaxUtils;
    }
}
