package top.zbeboy.isy.service.graduate.design;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignArchives;
import top.zbeboy.isy.domain.tables.records.GraduationDesignArchivesRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.graduate.design.archives.GraduationDesignArchivesBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2017-08-06.
 */
@Slf4j
@Service("graduationDesignArchivesService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignArchivesServiceImpl extends DataTablesPlugin<GraduationDesignArchivesBean> implements GraduationDesignArchivesService {

    private final DSLContext create;

    @Autowired
    public GraduationDesignArchivesServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public GraduationDesignArchivesRecord findByGraduationDesignPresubjectId(String graduationDesignPresubjectId) {
        return create.selectFrom(GRADUATION_DESIGN_ARCHIVES)
                .where(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID.eq(graduationDesignPresubjectId))
                .fetchOne();
    }

    @Override
    public GraduationDesignArchivesRecord findByArchiveNumber(String archiveNumber) {
        return create.selectFrom(GRADUATION_DESIGN_ARCHIVES)
                .where(GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER.eq(archiveNumber))
                .fetchOne();
    }

    @Override
    public List<GraduationDesignArchivesBean> findAllByPage(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils, GraduationDesignArchivesBean graduateArchivesBean) {
        List<GraduationDesignArchivesBean> graduateArchivesBeans = new ArrayList<>();
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduateArchivesBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record> selectJoinStep = create.select()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_TUTOR.STUDENT_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_ARCHIVES)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_DECLARE_DATA)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DESIGN_RELEASE_ID))
                    .join(GRADUATION_DESIGN_RELEASE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID))
                    .join(SCIENCE)
                    .on(GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID));
            sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            records = selectJoinStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_TUTOR.STUDENT_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_ARCHIVES)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_DECLARE_DATA)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DESIGN_RELEASE_ID))
                    .join(GRADUATION_DESIGN_RELEASE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID))
                    .join(SCIENCE)
                    .on(GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(a);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        buildData(records, graduateArchivesBeans);
        return graduateArchivesBeans;
    }

    @Override
    public int countAll(GraduationDesignArchivesBean graduateArchivesBean) {
        Record1<Integer> count;
        Condition a = otherCondition(null, graduateArchivesBean);
        if (ObjectUtils.isEmpty(a)) {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .fetchOne();
        } else {
            count = create.selectCount()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_TUTOR.STUDENT_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_ARCHIVES)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_DECLARE_DATA)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DESIGN_RELEASE_ID))
                    .join(GRADUATION_DESIGN_RELEASE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID))
                    .join(SCIENCE)
                    .on(GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(a)
                    .fetchOne();
        }
        return count.value1();
    }

    @Override
    public int countByCondition(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils, GraduationDesignArchivesBean graduateArchivesBean) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduateArchivesBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(DEFENSE_ORDER);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_TUTOR.STUDENT_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_ARCHIVES)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_DECLARE_DATA)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DESIGN_RELEASE_ID))
                    .join(GRADUATION_DESIGN_RELEASE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID))
                    .join(SCIENCE)
                    .on(GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    @Override
    public List<GraduationDesignArchivesBean> exportData(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils, GraduationDesignArchivesBean graduationDesignArchivesBean) {
        List<GraduationDesignArchivesBean> graduationDesignArchivesBeans = new ArrayList<>();
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignArchivesBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record> selectJoinStep = create.select()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_TUTOR.STUDENT_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_ARCHIVES)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_DECLARE_DATA)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DESIGN_RELEASE_ID))
                    .join(GRADUATION_DESIGN_RELEASE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID))
                    .join(SCIENCE)
                    .on(GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID));
            records = selectJoinStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_TUTOR.STUDENT_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_ARCHIVES)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_DECLARE_DATA)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DESIGN_RELEASE_ID))
                    .join(GRADUATION_DESIGN_RELEASE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID))
                    .join(SCIENCE)
                    .on(GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(a);
            records = selectConditionStep.fetch();
        }
        buildData(records, graduationDesignArchivesBeans);
        return graduationDesignArchivesBeans;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void saveAndIgnore(GraduationDesignArchives graduationDesignArchives) {
        create.insertInto(GRADUATION_DESIGN_ARCHIVES,
                GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID,
                GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT,
                GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER,
                GRADUATION_DESIGN_ARCHIVES.NOTE)
                .values(graduationDesignArchives.getGraduationDesignPresubjectId(),
                        graduationDesignArchives.getIsExcellent(),
                        graduationDesignArchives.getArchiveNumber(),
                        graduationDesignArchives.getNote())
                .onDuplicateKeyIgnore()
                .execute();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignArchives graduationDesignArchives) {
        create.insertInto(GRADUATION_DESIGN_ARCHIVES)
                .set(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID, graduationDesignArchives.getGraduationDesignPresubjectId())
                .set(GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER, graduationDesignArchives.getArchiveNumber())
                .set(GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT, graduationDesignArchives.getIsExcellent())
                .set(GRADUATION_DESIGN_ARCHIVES.NOTE, graduationDesignArchives.getNote())
                .execute();
    }

    @Override
    public void update(GraduationDesignArchives graduationDesignArchives) {
        create.update(GRADUATION_DESIGN_ARCHIVES)
                .set(GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT, graduationDesignArchives.getIsExcellent())
                .set(GRADUATION_DESIGN_ARCHIVES.NOTE, graduationDesignArchives.getNote())
                .set(GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER, graduationDesignArchives.getArchiveNumber())
                .where(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID.eq(graduationDesignArchives.getGraduationDesignPresubjectId()))
                .execute();
    }


    /**
     * 其它条件
     *
     * @param graduateArchivesBean 条件
     * @return 条件
     */
    public Condition otherCondition(Condition a, GraduationDesignArchivesBean graduateArchivesBean) {
        if (!ObjectUtils.isEmpty(graduateArchivesBean)) {
            if (StringUtils.hasLength(graduateArchivesBean.getGraduationDesignReleaseId())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID.eq(graduateArchivesBean.getGraduationDesignReleaseId()));
                } else {
                    a = GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID.eq(graduateArchivesBean.getGraduationDesignReleaseId());
                }
            }
        }

        return a;
    }

    private void buildData(Result<Record> records, List<GraduationDesignArchivesBean> graduateArchivesBeans) {
        for (Record r : records) {
            GraduationDesignArchivesBean graduateArchivesBean = new GraduationDesignArchivesBean();
            graduateArchivesBean.setGraduationDesignReleaseId(r.getValue(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID));
            graduateArchivesBean.setCollegeName(r.getValue(COLLEGE.COLLEGE_NAME));
            graduateArchivesBean.setCollegeCode(r.getValue(COLLEGE.COLLEGE_CODE));
            graduateArchivesBean.setScienceName(r.getValue(SCIENCE.SCIENCE_NAME));
            graduateArchivesBean.setScienceCode(r.getValue(SCIENCE.SCIENCE_CODE));
            graduateArchivesBean.setGraduationDate(r.getValue(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DATE));
            graduateArchivesBean.setStaffName(r.getValue(USERS.REAL_NAME));
            graduateArchivesBean.setStaffNumber(r.getValue(STAFF.STAFF_NUMBER));
            graduateArchivesBean.setAcademicTitleName(r.getValue(ACADEMIC_TITLE.ACADEMIC_TITLE_NAME));
            graduateArchivesBean.setAssistantTeacher(r.getValue(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER));
            graduateArchivesBean.setAssistantTeacherAcademic(r.getValue(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC));
            graduateArchivesBean.setAssistantTeacherNumber(r.getValue(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_NUMBER));
            graduateArchivesBean.setPresubjectTitle(r.getValue(GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE));
            graduateArchivesBean.setSubjectTypeName(r.getValue(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME));
            graduateArchivesBean.setOriginTypeName(r.getValue(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME));
            graduateArchivesBean.setStudentName(r.getValue(DEFENSE_ORDER.STUDENT_NAME));
            graduateArchivesBean.setStudentNumber(r.getValue(DEFENSE_ORDER.STUDENT_NUMBER));
            graduateArchivesBean.setScoreTypeName(r.getValue(SCORE_TYPE.SCORE_TYPE_NAME));
            graduateArchivesBean.setGraduationDesignPresubjectId(r.getValue(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID));
            graduateArchivesBean.setIsExcellent(r.getValue(GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT));
            graduateArchivesBean.setArchiveNumber(r.getValue(GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER));
            graduateArchivesBean.setNote(r.getValue(GRADUATION_DESIGN_ARCHIVES.NOTE));
            graduateArchivesBeans.add(graduateArchivesBean);
        }
    }

    /**
     * 数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String staffName = StringUtils.trimWhitespace(search.getString("staffName"));
            String staffNumber = StringUtils.trimWhitespace(search.getString("staffNumber"));

            if (StringUtils.hasLength(studentName)) {
                a = DEFENSE_ORDER.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName));
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = DEFENSE_ORDER.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(DEFENSE_ORDER.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                }
            }

            if (StringUtils.hasLength(staffName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(staffName));
                } else {
                    a = a.and(USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(staffName)));
                }
            }

            if (StringUtils.hasLength(staffNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber));
                } else {
                    a = a.and(STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber)));
                }
            }

        }
        return a;
    }

    /**
     * 数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("college_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("college_code".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_CODE.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = COLLEGE.COLLEGE_CODE.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("science_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_NAME.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = SCIENCE.SCIENCE_NAME.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("science_code".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_CODE.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = SCIENCE.SCIENCE_CODE.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("graduation_date".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DATE.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DATE.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("staff_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = USERS.REAL_NAME.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("staff_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STAFF.STAFF_NUMBER.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = STAFF.STAFF_NUMBER.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("academic_title_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("assistant_teacher".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("assistant_teacher_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_NUMBER.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_NUMBER.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("assistant_teacher_academic".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("presubject_title".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("subject_type_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("origin_type_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NUMBER.asc();
                } else {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NUMBER.desc();
                }
            }

            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NAME.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NAME.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("score_type_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCORE_TYPE.SCORE_TYPE_NAME.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = SCORE_TYPE.SCORE_TYPE_NAME.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("is_excellent".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

            if ("archive_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER.desc();
                }
            }

            if ("note".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.NOTE.asc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.NOTE.desc();
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc();
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
