package top.zbeboy.isy.service;

import com.alibaba.fastjson.JSONObject;
import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zbeboy on 2016/11/21.
 */
@Service("internshipTeacherDistributionService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipTeacherDistributionServiceImpl extends DataTablesPlugin<InternshipTeacherDistributionBean> implements InternshipTeacherDistributionService {

    private final Logger log = LoggerFactory.getLogger(InternshipTeacherDistributionServiceImpl.class);

    private final DSLContext create;

    @Autowired
    public InternshipTeacherDistributionServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<InternshipTeacherDistributionBean> findAllByPage(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils,String internshipReleaseId) {
        List<InternshipTeacherDistributionBean> internshipTeacherDistributionBeens = new ArrayList<>();
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STAFF.join(USERS.as("S")).on(STAFF.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(STUDENT.join(USERS.as("T")).on(STUDENT.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(INTERNSHIP_RELEASE)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                    .join(DEPARTMENT)
                    .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .join(SCHOOL)
                    .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                    .join(USERS.as("U"))
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME.eq(USERS.as("U").USERNAME))
                    .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId));
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STAFF.join(USERS.as("S")).on(STAFF.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(STUDENT.join(USERS.as("T")).on(STUDENT.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(INTERNSHIP_RELEASE)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                    .join(DEPARTMENT)
                    .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .join(SCHOOL)
                    .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                    .join(USERS.as("U"))
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME.eq(USERS.as("U").USERNAME))
                    .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)).and(a);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }

        if(records.isNotEmpty()){
            for(Record r:records){
                InternshipTeacherDistributionBean internshipTeacherDistributionBeen = new InternshipTeacherDistributionBean();
                internshipTeacherDistributionBeen.setInternshipTitle(r.getValue(INTERNSHIP_RELEASE.INTERNSHIP_TITLE));
                internshipTeacherDistributionBeen.setSchoolName(r.getValue(SCHOOL.SCHOOL_NAME));
                internshipTeacherDistributionBeen.setCollegeName(r.getValue(COLLEGE.COLLEGE_NAME));
                internshipTeacherDistributionBeen.setDepartmentName(r.getValue(DEPARTMENT.DEPARTMENT_NAME));
                internshipTeacherDistributionBeen.setStudentRealName(r.getValue(USERS.as("T").REAL_NAME));
                internshipTeacherDistributionBeen.setStudentUsername(r.getValue(USERS.as("T").USERNAME));
                internshipTeacherDistributionBeen.setStudentNumber(r.getValue(STUDENT.STUDENT_NUMBER));
                internshipTeacherDistributionBeen.setTeacherRealName(r.getValue(USERS.as("S").REAL_NAME));
                internshipTeacherDistributionBeen.setTeacherUsername(r.getValue(USERS.as("S").USERNAME));
                internshipTeacherDistributionBeen.setRealName(r.getValue(USERS.as("U").REAL_NAME));
                internshipTeacherDistributionBeen.setUsername(r.getValue(USERS.as("U").USERNAME));
                internshipTeacherDistributionBeens.add(internshipTeacherDistributionBeen);
            }
        }

        return internshipTeacherDistributionBeens;
    }

    @Override
    public int countAll(String internshipReleaseId) {
        Record1<Integer> count = create.selectCount()
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetchOne();
        return count.value1();
    }

    @Override
    public int countByCondition(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils,String internshipReleaseId) {
        Record1<Integer> count ;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId));
            count = selectConditionStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STAFF.join(USERS.as("S")).on(STAFF.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(STUDENT.join(USERS.as("T")).on(STUDENT.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(INTERNSHIP_RELEASE)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                    .join(DEPARTMENT)
                    .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .join(SCHOOL)
                    .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                    .join(USERS.as("U"))
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME.eq(USERS.as("U").USERNAME))
                    .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)).and(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    /**
     * 数据全局搜索条件
     *
     * @param dataTablesUtils
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String studentUsername = StringUtils.trimWhitespace(search.getString("studentUsername"));
            String staffUsername = StringUtils.trimWhitespace(search.getString("staffUsername"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String staffNumber = StringUtils.trimWhitespace(search.getString("staffNumber"));
            String realName = StringUtils.trimWhitespace(search.getString("realName"));
            String username = StringUtils.trimWhitespace(search.getString("username"));
            if (StringUtils.hasLength(studentUsername)) {
                a = USERS.as("T").USERNAME.like(SQLQueryUtils.likeAllParam(studentUsername));
            }

            if (StringUtils.hasLength(staffUsername)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = USERS.as("S").USERNAME.like(SQLQueryUtils.likeAllParam(staffUsername));
                } else {
                    a = a.and(USERS.as("S").USERNAME.like(SQLQueryUtils.likeAllParam(staffUsername)));
                }
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                }
            }

            if (StringUtils.hasLength(staffNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber));
                } else {
                    a = a.and(STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber)));
                }
            }

            if (StringUtils.hasLength(realName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = USERS.as("U").REAL_NAME.like(SQLQueryUtils.likeAllParam(realName));
                } else {
                    a = a.and(USERS.as("U").REAL_NAME.like(SQLQueryUtils.likeAllParam(realName)));
                }
            }

            if (StringUtils.hasLength(username)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = USERS.as("U").USERNAME.like(SQLQueryUtils.likeAllParam(username));
                } else {
                    a = a.and(USERS.as("U").USERNAME.like(SQLQueryUtils.likeAllParam(username)));
                }
            }
        }
        return a;
    }
}
