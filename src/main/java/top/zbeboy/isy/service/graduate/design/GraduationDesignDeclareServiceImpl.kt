package top.zbeboy.isy.service.graduate.design

import org.apache.commons.lang.math.NumberUtils
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclare
import top.zbeboy.isy.domain.tables.records.GraduationDesignDeclareRecord
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.ArrayList

/**
 * Created by zbeboy 2018-01-26 .
 **/
@Service("graduationDesignDeclareService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignDeclareServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<GraduationDesignDeclareBean>(), GraduationDesignDeclareService {

    private val create: DSLContext = dslContext


    override fun findByGraduationDesignPresubjectId(graduationDesignPresubjectId: String): GraduationDesignDeclareRecord {
        return create.selectFrom(GRADUATION_DESIGN_DECLARE)
                .where(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID.eq(graduationDesignPresubjectId))
                .fetchOne()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): List<GraduationDesignDeclareBean> {
        val graduationDesignDeclareBeens = ArrayList<GraduationDesignDeclareBean>()
        val records: Result<Record>
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduationDesignDeclareBean)
        records = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
            sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId)))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a)
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }
        buildData(records, graduationDesignDeclareBeens)
        return graduationDesignDeclareBeens
    }

    override fun countAll(graduationDesignDeclareBean: GraduationDesignDeclareBean): Int {
        val count: Record1<Int>
        val a = otherCondition(null, graduationDesignDeclareBean)
        count = if (ObjectUtils.isEmpty(a)) {
            create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .fetchOne()
        } else {
            create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId)))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a)
                    .fetchOne()
        }
        return count.value1()
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): Int {
        val count: Record1<Int>
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduationDesignDeclareBean)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId)))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a)
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    override fun exportData(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): List<GraduationDesignDeclareBean> {
        val graduationDesignDeclareBeens = ArrayList<GraduationDesignDeclareBean>()
        val records: Result<Record>
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduationDesignDeclareBean)
        records = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId)))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a)
            selectConditionStep.fetch()
        }
        buildData(records, graduationDesignDeclareBeens)
        return graduationDesignDeclareBeens
    }

    private fun buildData(records: Result<Record>, graduationDesignDeclareBeens: MutableList<GraduationDesignDeclareBean>) {
        for (r in records) {
            val tempGraduationDesignDeclareBean = GraduationDesignDeclareBean()
            tempGraduationDesignDeclareBean.subjectTypeId = r.getValue(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID)
            tempGraduationDesignDeclareBean.subjectTypeName = r.getValue(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME)
            tempGraduationDesignDeclareBean.originTypeId = r.getValue(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID)
            tempGraduationDesignDeclareBean.originTypeName = r.getValue(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME)
            tempGraduationDesignDeclareBean.isNewSubject = r.getValue(GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT)
            tempGraduationDesignDeclareBean.isNewTeacherMake = r.getValue(GRADUATION_DESIGN_DECLARE.IS_NEW_TEACHER_MAKE)
            tempGraduationDesignDeclareBean.isNewSubjectMake = r.getValue(GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT_MAKE)
            tempGraduationDesignDeclareBean.isOldSubjectChange = r.getValue(GRADUATION_DESIGN_DECLARE.IS_OLD_SUBJECT_CHANGE)
            tempGraduationDesignDeclareBean.oldSubjectUsesTimes = r.getValue(GRADUATION_DESIGN_DECLARE.OLD_SUBJECT_USES_TIMES)
            tempGraduationDesignDeclareBean.planPeriod = r.getValue(GRADUATION_DESIGN_DECLARE.PLAN_PERIOD)
            tempGraduationDesignDeclareBean.assistantTeacher = r.getValue(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER)
            tempGraduationDesignDeclareBean.assistantTeacherAcademic = r.getValue(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC)
            tempGraduationDesignDeclareBean.guideTimes = r.getValue(GRADUATION_DESIGN_DECLARE.GUIDE_TIMES)
            tempGraduationDesignDeclareBean.guidePeoples = r.getValue(GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES)
            tempGraduationDesignDeclareBean.isOkApply = r.getValue(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY)
            tempGraduationDesignDeclareBean.graduationDesignPresubjectId = r.getValue(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID)
            tempGraduationDesignDeclareBean.staffId = r.getValue(STAFF.STAFF_ID)
            tempGraduationDesignDeclareBean.staffName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)
            tempGraduationDesignDeclareBean.academicTitleName = r.getValue(ACADEMIC_TITLE.ACADEMIC_TITLE_NAME)
            tempGraduationDesignDeclareBean.studentId = r.getValue(STUDENT.STUDENT_ID)
            tempGraduationDesignDeclareBean.studentName = r.getValue(USERS.REAL_NAME)
            tempGraduationDesignDeclareBean.studentNumber = r.getValue(STUDENT.STUDENT_NUMBER)
            tempGraduationDesignDeclareBean.organizeName = r.getValue(ORGANIZE.ORGANIZE_NAME)
            tempGraduationDesignDeclareBean.presubjectTitle = r.getValue(GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE)
            tempGraduationDesignDeclareBean.publicLevel = r.getValue(GRADUATION_DESIGN_PRESUBJECT.PUBLIC_LEVEL)
            tempGraduationDesignDeclareBean.graduationDesignReleaseId = r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID)
            graduationDesignDeclareBeens.add(tempGraduationDesignDeclareBean)
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun saveOrUpdate(graduationDesignDeclare: GraduationDesignDeclare) {
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
                GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_NUMBER,
                GRADUATION_DESIGN_DECLARE.GUIDE_TIMES,
                GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES,
                GRADUATION_DESIGN_DECLARE.IS_OK_APPLY,
                GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID)
                .values(
                        graduationDesignDeclare.subjectTypeId,
                        graduationDesignDeclare.originTypeId,
                        graduationDesignDeclare.isNewSubject,
                        graduationDesignDeclare.isNewTeacherMake,
                        graduationDesignDeclare.isNewSubjectMake,
                        graduationDesignDeclare.isOldSubjectChange,
                        graduationDesignDeclare.oldSubjectUsesTimes,
                        graduationDesignDeclare.planPeriod,
                        graduationDesignDeclare.assistantTeacher,
                        graduationDesignDeclare.assistantTeacherAcademic,
                        graduationDesignDeclare.assistantTeacherNumber,
                        graduationDesignDeclare.guideTimes,
                        graduationDesignDeclare.guidePeoples,
                        graduationDesignDeclare.isOkApply,
                        graduationDesignDeclare.graduationDesignPresubjectId
                )
                .onDuplicateKeyUpdate()
                .set(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID, graduationDesignDeclare.subjectTypeId)
                .set(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID, graduationDesignDeclare.originTypeId)
                .set(GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT, graduationDesignDeclare.isNewSubject)
                .set(GRADUATION_DESIGN_DECLARE.IS_NEW_TEACHER_MAKE, graduationDesignDeclare.isNewTeacherMake)
                .set(GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT_MAKE, graduationDesignDeclare.isNewSubjectMake)
                .set(GRADUATION_DESIGN_DECLARE.IS_OLD_SUBJECT_CHANGE, graduationDesignDeclare.isOldSubjectChange)
                .set(GRADUATION_DESIGN_DECLARE.OLD_SUBJECT_USES_TIMES, graduationDesignDeclare.oldSubjectUsesTimes)
                .set(GRADUATION_DESIGN_DECLARE.PLAN_PERIOD, graduationDesignDeclare.planPeriod)
                .set(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER, graduationDesignDeclare.assistantTeacher)
                .set(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC, graduationDesignDeclare.assistantTeacherAcademic)
                .set(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_NUMBER, graduationDesignDeclare.assistantTeacherNumber)
                .set(GRADUATION_DESIGN_DECLARE.GUIDE_TIMES, graduationDesignDeclare.guideTimes)
                .set(GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES, graduationDesignDeclare.guidePeoples)
                .set(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY, graduationDesignDeclare.isOkApply)
                .execute()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun saveOrUpdateState(graduationDesignDeclare: GraduationDesignDeclare) {
        create.insertInto(GRADUATION_DESIGN_DECLARE,
                GRADUATION_DESIGN_DECLARE.IS_OK_APPLY,
                GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID)
                .values(
                        graduationDesignDeclare.isOkApply,
                        graduationDesignDeclare.graduationDesignPresubjectId
                )
                .onDuplicateKeyUpdate()
                .set(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY, graduationDesignDeclare.isOkApply)
                .execute()
    }

    override fun findByStaffIdRelationNeIsOkApply(staffId: Int, graduationDesignReleaseId: String): Result<Record1<String>> {
        return create.select(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID)
                .from(GRADUATION_DESIGN_TEACHER)
                .join(GRADUATION_DESIGN_TUTOR)
                .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                .join(GRADUATION_DESIGN_PRESUBJECT)
                .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                .leftJoin(GRADUATION_DESIGN_DECLARE)
                .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                .where(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(staffId).and(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY.ne(1).or(GRADUATION_DESIGN_DECLARE.IS_OK_APPLY.isNull)).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetch()
    }

    /**
     * 其它条件
     *
     * @param graduationDesignDeclareBean 条件
     * @return 条件
     */
    fun otherCondition(a: Condition?, graduationDesignDeclareBean: GraduationDesignDeclareBean): Condition? {
        var tempCondtion = a
        if (!ObjectUtils.isEmpty(graduationDesignDeclareBean)) {
            if (!ObjectUtils.isEmpty(graduationDesignDeclareBean.staffId) && graduationDesignDeclareBean.staffId > 0) {
                tempCondtion = if (!ObjectUtils.isEmpty(tempCondtion)) {
                    tempCondtion!!.and(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDeclareBean.staffId))
                } else {
                    GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDeclareBean.staffId)
                }
            }

            if (StringUtils.hasLength(graduationDesignDeclareBean.graduationDesignReleaseId)) {
                tempCondtion = if (!ObjectUtils.isEmpty(tempCondtion)) {
                    tempCondtion!!.and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId))
                } else {
                    GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId)
                }
            }
        }

        return tempCondtion
    }

    /**
     * 数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val presubjectTitle = StringUtils.trimWhitespace(search!!.getString("presubjectTitle"))
            val studentName = StringUtils.trimWhitespace(search.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val organize = StringUtils.trimWhitespace(search.getString("organize"))
            val subjectType = StringUtils.trimWhitespace(search.getString("subjectType"))
            val originType = StringUtils.trimWhitespace(search.getString("originType"))
            if (StringUtils.hasLength(presubjectTitle)) {
                a = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.like(SQLQueryUtils.likeAllParam(presubjectTitle))
            }

            if (StringUtils.hasLength(studentName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName))
                } else {
                    a!!.and(USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName)))
                }
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(organize)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organize))
                } else {
                    a!!.and(ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organize)))
                }
            }

            if (StringUtils.hasLength(subjectType)) {
                val subjectTypeId = NumberUtils.toInt(subjectType)
                if (subjectTypeId > 0) {
                    a = if (ObjectUtils.isEmpty(a)) {
                        GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID.eq(subjectTypeId)
                    } else {
                        a!!.and(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID.eq(subjectTypeId))
                    }
                }
            }

            if (StringUtils.hasLength(originType)) {
                val originTypeId = NumberUtils.toInt(originType)
                if (originTypeId > 0) {
                    a = if (ObjectUtils.isEmpty(a)) {
                        GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID.eq(originTypeId)
                    } else {
                        a!!.and(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID.eq(originTypeId))
                    }
                }
            }
        }
        return a
    }

    /**
     * 数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("presubject_title".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("subject_type_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("origin_type_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("is_new_subject".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("is_new_teacher_make".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_TEACHER_MAKE.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_TEACHER_MAKE.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("is_new_subject_make".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT_MAKE.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_NEW_SUBJECT_MAKE.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("is_old_subject_change".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_OLD_SUBJECT_CHANGE.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_OLD_SUBJECT_CHANGE.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("old_subject_uses_times".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.OLD_SUBJECT_USES_TIMES.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.OLD_SUBJECT_USES_TIMES.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("plan_period".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.PLAN_PERIOD.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.PLAN_PERIOD.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("guide_teacher".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.`as`("T").REAL_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = USERS.`as`("T").REAL_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("academic_title_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("assistant_teacher".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("assistant_teacher_academic".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("guide_times".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.GUIDE_TIMES.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.GUIDE_TIMES.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("guide_peoples".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("student_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = STUDENT.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = STUDENT.STUDENT_NUMBER.desc()
                }
            }

            if ("student_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = USERS.REAL_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("organize_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("is_ok_apply".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_OK_APPLY.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.IS_OK_APPLY.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}