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
import top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution;
import top.zbeboy.isy.domain.tables.records.InternshipTeacherDistributionRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Result<Record1<Integer>> findByInternshipReleaseIdDistinctOrganizeId(String internshipReleaseId) {
        return create.selectDistinct(STUDENT.ORGANIZE_ID)
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .join(STUDENT)
                .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetch();
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }

    @Override
    public Result<Record> findByInternshipReleaseIdAndStaffIdForStudent(String internshipReleaseId, int staffId) {
        return create.select()
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .join(STUDENT)
                .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(staffId)))
                .fetch();
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentIdForStaff(String internshipReleaseId, int studentId) {
        return create.select()
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .join(STAFF)
                .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }

    @Override
    public Result<InternshipTeacherDistributionRecord> findByInternshipReleaseIdAndStaffId(String internshipReleaseId, int staffId) {
        return create.selectFrom(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(staffId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(InternshipTeacherDistribution internshipTeacherDistribution) {
        create.insertInto(INTERNSHIP_TEACHER_DISTRIBUTION)
                .set(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID, internshipTeacherDistribution.getInternshipReleaseId())
                .set(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID, internshipTeacherDistribution.getStaffId())
                .set(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID, internshipTeacherDistribution.getStudentId())
                .set(INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME, internshipTeacherDistribution.getUsername())
                .execute();
    }

    @Override
    public void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        create.deleteFrom(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(studentId)))
                .execute();
    }

    @Override
    public void updateStaffId(InternshipTeacherDistribution internshipTeacherDistribution) {
        create.update(INTERNSHIP_TEACHER_DISTRIBUTION)
                .set(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID, internshipTeacherDistribution.getStaffId())
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipTeacherDistribution.getInternshipReleaseId())
                        .and(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(internshipTeacherDistribution.getStudentId())))
                .execute();
    }

    @Override
    public List<InternshipTeacherDistributionBean> findAllByPage(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils, String internshipReleaseId) {
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

        if (records.isNotEmpty()) {
            for (Record r : records) {
                InternshipTeacherDistributionBean internshipTeacherDistributionBeen = new InternshipTeacherDistributionBean();
                internshipTeacherDistributionBeen.setInternshipTitle(r.getValue(INTERNSHIP_RELEASE.INTERNSHIP_TITLE));
                internshipTeacherDistributionBeen.setSchoolName(r.getValue(SCHOOL.SCHOOL_NAME));
                internshipTeacherDistributionBeen.setCollegeName(r.getValue(COLLEGE.COLLEGE_NAME));
                internshipTeacherDistributionBeen.setDepartmentName(r.getValue(DEPARTMENT.DEPARTMENT_NAME));
                internshipTeacherDistributionBeen.setStudentRealName(r.getValue(USERS.as("T").REAL_NAME));
                internshipTeacherDistributionBeen.setStudentUsername(r.getValue(USERS.as("T").USERNAME));
                internshipTeacherDistributionBeen.setStudentNumber(r.getValue(STUDENT.STUDENT_NUMBER));
                internshipTeacherDistributionBeen.setStudentId(r.getValue(STUDENT.STUDENT_ID));
                internshipTeacherDistributionBeen.setStaffRealName(r.getValue(USERS.as("S").REAL_NAME));
                internshipTeacherDistributionBeen.setStaffUsername(r.getValue(USERS.as("S").USERNAME));
                internshipTeacherDistributionBeen.setStaffNumber(r.getValue(STAFF.STAFF_NUMBER));
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
    public int countByCondition(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils, String internshipReleaseId) {
        Record1<Integer> count;
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
     * @param dataTablesUtils datatables工具类
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

    /**
     * 院数据排序
     *
     * @param dataTablesUtils datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("internship_title".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = INTERNSHIP_RELEASE.INTERNSHIP_TITLE.asc();
                } else {
                    sortString = INTERNSHIP_RELEASE.INTERNSHIP_TITLE.desc();
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = SCHOOL.SCHOOL_NAME.asc();
                } else {
                    sortString = SCHOOL.SCHOOL_NAME.desc();
                }
            }

            if ("college_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = COLLEGE.COLLEGE_NAME.asc();
                } else {
                    sortString = COLLEGE.COLLEGE_NAME.desc();
                }
            }

            if ("department_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = DEPARTMENT.DEPARTMENT_NAME.asc();
                } else {
                    sortString = DEPARTMENT.DEPARTMENT_NAME.desc();
                }
            }

            if ("student_real_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = USERS.as("T").REAL_NAME.asc();
                } else {
                    sortString = USERS.as("T").REAL_NAME.desc();
                }
            }

            if ("student_username".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = USERS.as("T").USERNAME.asc();
                } else {
                    sortString = USERS.as("T").USERNAME.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = STUDENT.STUDENT_NUMBER.asc();
                } else {
                    sortString = STUDENT.STUDENT_NUMBER.desc();
                }
            }

            if ("staff_real_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = USERS.as("S").REAL_NAME.asc();
                } else {
                    sortString = USERS.as("S").REAL_NAME.desc();
                }
            }

            if ("staff_username".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = USERS.as("S").USERNAME.asc();
                } else {
                    sortString = USERS.as("S").USERNAME.desc();
                }
            }

            if ("staff_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = STAFF.STAFF_NUMBER.asc();
                } else {
                    sortString = STAFF.STAFF_NUMBER.desc();
                }
            }

            if ("real_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = USERS.as("U").REAL_NAME.asc();
                } else {
                    sortString = USERS.as("U").REAL_NAME.desc();
                }
            }

            if ("username".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = USERS.as("U").USERNAME.asc();
                } else {
                    sortString = USERS.as("U").USERNAME.desc();
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type);
    }
}
