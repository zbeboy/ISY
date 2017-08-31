/*
 * Copyright (ISY Team) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package top.zbeboy.isy.service.rest.internship;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.Science;
import top.zbeboy.isy.service.internship.InternshipReleaseScienceService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

@Slf4j
@Service("internshipReleaseRestService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipReleaseRestServiceImpl implements InternshipReleaseRestService {

    private final DSLContext create;

    @Resource
    private InternshipReleaseScienceService internshipReleaseScienceService;

    @Autowired
    public InternshipReleaseRestServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<Record> findAllByPage(PaginationUtils paginationUtils) {
        int pageNum = paginationUtils.getPageNum();
        int pageSize = paginationUtils.getPageSize();
        return create.select()
                .from(INTERNSHIP_RELEASE)
                .join(USERS)
                .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                .join(DEPARTMENT)
                .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                .orderBy(INTERNSHIP_RELEASE.RELEASE_TIME.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();
    }

    @Override
    public List<InternshipReleaseBean> dealData(Result<Record> records) {
        List<InternshipReleaseBean> internshipReleaseBeens = new ArrayList<>();
        if (records.isNotEmpty()) {
            internshipReleaseBeens = records.into(InternshipReleaseBean.class);
            String format = "yyyy-MM-dd HH:mm:ss";
            internshipReleaseBeens.forEach(i -> {
                dealDateTime(i,format);
                Result<Record> records1 = internshipReleaseScienceService.findByInternshipReleaseIdRelation(i.getInternshipReleaseId());
                i.setSciences(records1.into(Science.class));
            });
        }
        return internshipReleaseBeens;
    }

    @Override
    public Result<Record> findAllByPageForStudent(PaginationUtils paginationUtils, int scienceId, String allowGrade) {
        int pageNum = paginationUtils.getPageNum();
        int pageSize = paginationUtils.getPageSize();
        return create.select()
                .from(INTERNSHIP_RELEASE)
                .join(INTERNSHIP_RELEASE_SCIENCE)
                .on(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID)
                        .and(INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID.eq(scienceId)))
                .join(USERS)
                .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                .join(DEPARTMENT)
                .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                .where(INTERNSHIP_RELEASE.ALLOW_GRADE.eq(allowGrade))
                .orderBy(INTERNSHIP_RELEASE.RELEASE_TIME.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();
    }

    @Override
    public Result<Record> findAllByPageForStaff(PaginationUtils paginationUtils, int departmentId) {
        int pageNum = paginationUtils.getPageNum();
        int pageSize = paginationUtils.getPageSize();
        return create.select()
                .from(INTERNSHIP_RELEASE)
                .join(USERS)
                .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                .join(DEPARTMENT)
                .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                .where(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(departmentId))
                .orderBy(INTERNSHIP_RELEASE.RELEASE_TIME.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();
    }

    @Override
    public void dealDateTime(InternshipReleaseBean i, String format) {
        i.setTeacherDistributionStartTimeStr(DateTimeUtils.timestampToString(i.getTeacherDistributionStartTime(), format));
        i.setTeacherDistributionEndTimeStr(DateTimeUtils.timestampToString(i.getTeacherDistributionEndTime(), format));
        i.setStartTimeStr(DateTimeUtils.timestampToString(i.getStartTime(), format));
        i.setEndTimeStr(DateTimeUtils.timestampToString(i.getEndTime(), format));
        i.setReleaseTimeStr(DateTimeUtils.timestampToString(i.getReleaseTime(), format));
    }
}
