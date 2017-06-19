package top.zbeboy.isy.service.graduate.design;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclare;
import top.zbeboy.isy.domain.tables.records.GraduationDesignDeclareRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zbeboy on 2017/6/8.
 */
@Slf4j
@Service("graduationDesignDeclareService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignDeclareServiceImpl extends DataTablesPlugin<GraduationDesignDeclareBean> implements GraduationDesignDeclareService {

    private final DSLContext create;

    @Autowired
    public GraduationDesignDeclareServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public GraduationDesignDeclareRecord findByGraduationDesignPresubjectId(String graduationDesignPresubjectId) {
        return create.selectFrom(GRADUATION_DESIGN_DECLARE)
                .where(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID.eq(graduationDesignPresubjectId))
                .fetchOne();
    }

    @Override
    public List<GraduationDesignDeclareBean> findAllByPage(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean) {
        List<GraduationDesignDeclareBean> graduationDesignDeclareBeens = new ArrayList<>();
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDeclareBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT.join(USERS.as("S")).on(STUDENT.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.getGraduationDesignReleaseId()));
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT.join(USERS.as("S")).on(STUDENT.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.getGraduationDesignReleaseId()).and(a));
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        buildData(records, graduationDesignDeclareBeens);
        return graduationDesignDeclareBeens;
    }

    @Override
    public int countAll(GraduationDesignDeclareBean graduationDesignDeclareBean) {
        Record1<Integer> count;
        if (graduationDesignDeclareBean.getStaffId() > 0) {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.getGraduationDesignReleaseId())
                            .and(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDeclareBean.getStaffId())))
                    .fetchOne();
        } else {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.getGraduationDesignReleaseId()))
                    .fetchOne();
        }
        return count.value1();
    }

    @Override
    public int countByCondition(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDeclareBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.getGraduationDesignReleaseId())
                            .and(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDeclareBean.getStaffId())));
            count = selectConditionStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT.join(USERS.as("S")).on(STUDENT.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.getGraduationDesignReleaseId()).and(a));
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    @Override
    public List<GraduationDesignDeclareBean> exportData(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean) {
        List<GraduationDesignDeclareBean> graduationDesignDeclareBeens = new ArrayList<>();
        Byte b = 1;
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDeclareBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT.join(USERS.as("S")).on(STUDENT.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.getGraduationDesignReleaseId())
                    .and(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY.eq(b)));
            records = selectConditionStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT.join(USERS.as("S")).on(STUDENT.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.getGraduationDesignReleaseId())
                            .and(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY.eq(b)).and(a));
            records = selectConditionStep.fetch();
        }
        buildData(records, graduationDesignDeclareBeens);
        return graduationDesignDeclareBeens;
    }

    private List<GraduationDesignDeclareBean> buildData(Result<Record> records, List<GraduationDesignDeclareBean> graduationDesignDeclareBeens) {
        for (Record r : records) {
            GraduationDesignDeclareBean tempGraduationDesignDeclareBean = new GraduationDesignDeclareBean();
            tempGraduationDesignDeclareBean.setSubjectTypeId(r.getValue(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID));
            tempGraduationDesignDeclareBean.setSubjectTypeName(r.getValue(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME));
            tempGraduationDesignDeclareBean.setOriginTypeId(r.getValue(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID));
            tempGraduationDesignDeclareBean.setOriginTypeName(r.getValue(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME));
            tempGraduationDesignDeclareBean.setIsNewSubject(r.getValue(GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT));
            tempGraduationDesignDeclareBean.setIsNewTeacherMake(r.getValue(GRADUATION_DESIGN_DECLARE.IS_NEW_TEACHER_MAKE));
            tempGraduationDesignDeclareBean.setIsNewSubjectMake(r.getValue(GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT_MAKE));
            tempGraduationDesignDeclareBean.setIsOldSubjectChange(r.getValue(GRADUATION_DESIGN_DECLARE.IS_OLD_SUBJECT_CHANGE));
            tempGraduationDesignDeclareBean.setOldSubjectUsesTimes(r.getValue(GRADUATION_DESIGN_DECLARE.OLD_SUBJECT_USES_TIMES));
            tempGraduationDesignDeclareBean.setPlanPeriod(r.getValue(GRADUATION_DESIGN_DECLARE.PLAN_PERIOD));
            tempGraduationDesignDeclareBean.setAssistantTeacher(r.getValue(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER));
            tempGraduationDesignDeclareBean.setAssistantTeacherAcademic(r.getValue(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC));
            tempGraduationDesignDeclareBean.setGuideTimes(r.getValue(GRADUATION_DESIGN_DECLARE.GUIDE_TIMES));
            tempGraduationDesignDeclareBean.setGuidePeoples(r.getValue(GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES));
            tempGraduationDesignDeclareBean.setIsOkApply(r.getValue(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY));
            tempGraduationDesignDeclareBean.setGraduationDesignPresubjectId(r.getValue(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID));
            tempGraduationDesignDeclareBean.setStaffId(r.getValue(STAFF.STAFF_ID));
            tempGraduationDesignDeclareBean.setStaffName(r.getValue(USERS.as("T").REAL_NAME));
            tempGraduationDesignDeclareBean.setAcademicTitleName(r.getValue(ACADEMIC_TITLE.ACADEMIC_TITLE_NAME));
            tempGraduationDesignDeclareBean.setStudentId(r.getValue(STUDENT.STUDENT_ID));
            tempGraduationDesignDeclareBean.setStudentName(r.getValue(USERS.as("S").REAL_NAME));
            tempGraduationDesignDeclareBean.setStudentNumber(r.getValue(STUDENT.STUDENT_NUMBER));
            tempGraduationDesignDeclareBean.setOrganizeName(r.getValue(ORGANIZE.ORGANIZE_NAME));
            tempGraduationDesignDeclareBean.setPresubjectTitle(r.getValue(GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE));
            tempGraduationDesignDeclareBean.setGraduationDesignReleaseId(r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID));
            graduationDesignDeclareBeens.add(tempGraduationDesignDeclareBean);
        }
        return graduationDesignDeclareBeens;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void saveOrUpdate(GraduationDesignDeclare graduationDesignDeclare) {
        create.insertInto(GRADUATION_DESIGN_DECLARE,
                GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID,
                GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID,
                GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT,
                GRADUATION_DESIGN_DECLARE.IS_NEW_TEACHER_MAKE,
                GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT_MAKE,
                GRADUATION_DESIGN_DECLARE.IS_OLD_SUBJECT_CHANGE,
                GRADUATION_DESIGN_DECLARE.OLD_SUBJECT_USES_TIMES,
                GRADUATION_DESIGN_DECLARE.PLAN_PERIOD,
                GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER,
                GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC,
                GRADUATION_DESIGN_DECLARE.GUIDE_TIMES,
                GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES,
                GRADUATION_DESIGN_DECLARE.IS_OK_APPLY,
                GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID)
                .values(
                        graduationDesignDeclare.getSubjectTypeId(),
                        graduationDesignDeclare.getOriginTypeId(),
                        graduationDesignDeclare.getIsNewSubject(),
                        graduationDesignDeclare.getIsNewTeacherMake(),
                        graduationDesignDeclare.getIsNewSubjectMake(),
                        graduationDesignDeclare.getIsOldSubjectChange(),
                        graduationDesignDeclare.getOldSubjectUsesTimes(),
                        graduationDesignDeclare.getPlanPeriod(),
                        graduationDesignDeclare.getAssistantTeacher(),
                        graduationDesignDeclare.getAssistantTeacherAcademic(),
                        graduationDesignDeclare.getGuideTimes(),
                        graduationDesignDeclare.getGuidePeoples(),
                        graduationDesignDeclare.getIsOkApply(),
                        graduationDesignDeclare.getGraduationDesignPresubjectId()
                )
                .onDuplicateKeyUpdate()
                .set(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID, graduationDesignDeclare.getSubjectTypeId())
                .set(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID, graduationDesignDeclare.getOriginTypeId())
                .set(GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT, graduationDesignDeclare.getIsNewSubject())
                .set(GRADUATION_DESIGN_DECLARE.IS_NEW_TEACHER_MAKE, graduationDesignDeclare.getIsNewTeacherMake())
                .set(GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT_MAKE, graduationDesignDeclare.getIsNewSubjectMake())
                .set(GRADUATION_DESIGN_DECLARE.IS_OLD_SUBJECT_CHANGE, graduationDesignDeclare.getIsOldSubjectChange())
                .set(GRADUATION_DESIGN_DECLARE.OLD_SUBJECT_USES_TIMES, graduationDesignDeclare.getOldSubjectUsesTimes())
                .set(GRADUATION_DESIGN_DECLARE.PLAN_PERIOD, graduationDesignDeclare.getPlanPeriod())
                .set(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER, graduationDesignDeclare.getAssistantTeacher())
                .set(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC, graduationDesignDeclare.getAssistantTeacherAcademic())
                .set(GRADUATION_DESIGN_DECLARE.GUIDE_TIMES, graduationDesignDeclare.getGuideTimes())
                .set(GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES, graduationDesignDeclare.getGuidePeoples())
                .set(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY, graduationDesignDeclare.getIsOkApply())
                .execute();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void saveOrUpdateState(GraduationDesignDeclare graduationDesignDeclare) {
        create.insertInto(GRADUATION_DESIGN_DECLARE,
                GRADUATION_DESIGN_DECLARE.IS_OK_APPLY,
                GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID)
                .values(
                        graduationDesignDeclare.getIsOkApply(),
                        graduationDesignDeclare.getGraduationDesignPresubjectId()
                )
                .onDuplicateKeyUpdate()
                .set(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY, graduationDesignDeclare.getIsOkApply())
                .execute();
    }

    @Override
    public Result<Record1<String>> findByStaffIdRelationNeIsOkApply(int staffId, String graduationDesignReleaseId) {
        Byte isOkApply = 1;
        return create.select(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID)
                .from(GRADUATION_DESIGN_TEACHER)
                .join(GRADUATION_DESIGN_TUTOR)
                .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                .join(GRADUATION_DESIGN_PRESUBJECT)
                .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                .leftJoin(GRADUATION_DESIGN_DECLARE)
                .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                .where(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(staffId).and(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY.ne(isOkApply).or(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY.isNull())).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetch();
    }

    /**
     * 其它条件
     *
     * @param graduationDesignDeclareBean 条件
     * @return 条件
     */
    public Condition otherCondition(Condition a, GraduationDesignDeclareBean graduationDesignDeclareBean) {
        if (graduationDesignDeclareBean.getStaffId() > 0) {
            if (ObjectUtils.isEmpty(a)) {
                a = GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDeclareBean.getStaffId());
            } else {
                a = a.and(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDeclareBean.getStaffId()));
            }
        }
        return a;
    }

    /**
     * 数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String presubjectTitle = StringUtils.trimWhitespace(search.getString("presubjectTitle"));
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String organize = StringUtils.trimWhitespace(search.getString("organize"));
            String subjectType = StringUtils.trimWhitespace(search.getString("subjectType"));
            String originType = StringUtils.trimWhitespace(search.getString("originType"));
            if (StringUtils.hasLength(presubjectTitle)) {
                a = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.like(SQLQueryUtils.likeAllParam(presubjectTitle));
            }

            if (StringUtils.hasLength(studentName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = USERS.as("S").REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName));
                } else {
                    a = a.and(USERS.as("S").REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName)));
                }
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                }
            }

            if (StringUtils.hasLength(organize)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organize));
                } else {
                    a = a.and(ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organize)));
                }
            }

            if (StringUtils.hasLength(subjectType)) {
                int subjectTypeId = NumberUtils.toInt(subjectType);
                if (subjectTypeId > 0) {
                    if (ObjectUtils.isEmpty(a)) {
                        a = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID.eq(subjectTypeId);
                    } else {
                        a = a.and(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID.eq(subjectTypeId));
                    }
                }
            }

            if (StringUtils.hasLength(originType)) {
                int originTypeId = NumberUtils.toInt(originType);
                if (originTypeId > 0) {
                    if (ObjectUtils.isEmpty(a)) {
                        a = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID.eq(originTypeId);
                    } else {
                        a = a.and(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID.eq(originTypeId));
                    }
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
    public void sortCondition(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("presubject_title".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("subject_type_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("origin_type_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("is_new_subject".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("is_new_teacher_make".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_TEACHER_MAKE.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_TEACHER_MAKE.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("is_new_subject_make".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT_MAKE.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT_MAKE.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("is_old_subject_change".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_OLD_SUBJECT_CHANGE.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_OLD_SUBJECT_CHANGE.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("old_subject_uses_times".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.OLD_SUBJECT_USES_TIMES.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.OLD_SUBJECT_USES_TIMES.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("plan_period".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.PLAN_PERIOD.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.PLAN_PERIOD.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("guide_teacher".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.as("T").REAL_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = USERS.as("T").REAL_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("academic_title_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("assistant_teacher".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("assistant_teacher_academic".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("guide_times".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.GUIDE_TIMES.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.GUIDE_TIMES.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("guide_peoples".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = STUDENT.STUDENT_NUMBER.asc();
                } else {
                    sortField[0] = STUDENT.STUDENT_NUMBER.desc();
                }
            }

            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.as("S").REAL_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = USERS.as("S").REAL_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("organize_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("is_ok_apply".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_OK_APPLY.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_OK_APPLY.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
