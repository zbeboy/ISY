package top.zbeboy.isy.service.graduate.design

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignArchives
import top.zbeboy.isy.domain.tables.records.GraduationDesignArchivesRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.graduate.design.archives.GraduationDesignArchivesBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2018-02-08 .
 **/
@Service("graduationDesignArchivesService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignArchivesServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<GraduationDesignArchivesBean>(), GraduationDesignArchivesService {

    private val create: DSLContext = dslContext

    override fun findByGraduationDesignPresubjectId(graduationDesignPresubjectId: String): GraduationDesignArchivesRecord {
        return create.selectFrom(GRADUATION_DESIGN_ARCHIVES)
                .where(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID.eq(graduationDesignPresubjectId))
                .fetchOne()
    }

    override fun findByArchiveNumber(archiveNumber: String): GraduationDesignArchivesRecord {
        return create.selectFrom(GRADUATION_DESIGN_ARCHIVES)
                .where(GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER.eq(archiveNumber))
                .fetchOne()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignArchivesBean>, graduateArchivesBean: GraduationDesignArchivesBean): List<GraduationDesignArchivesBean> {
        val graduateArchivesBeans = ArrayList<GraduationDesignArchivesBean>()
        val records: Result<Record>
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduateArchivesBean)
        records = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(DEFENSE_ARRANGEMENT)
                    .on(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID))
                    .join(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                    .join(DEFENSE_ORDER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(DEFENSE_ORDER.STUDENT_ID)))
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
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
            sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduateArchivesBean.graduationDesignReleaseId)))
                    .join(DEFENSE_ARRANGEMENT)
                    .on(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID))
                    .join(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                    .join(DEFENSE_ORDER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(DEFENSE_ORDER.STUDENT_ID)))
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
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
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }
        buildData(records, graduateArchivesBeans)
        return graduateArchivesBeans
    }

    override fun countAll(graduateArchivesBean: GraduationDesignArchivesBean): Int {
        val count: Record1<Int>
        val a = otherCondition(null, graduateArchivesBean)
        count = if (ObjectUtils.isEmpty(a)) {
            create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(DEFENSE_ARRANGEMENT)
                    .on(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID))
                    .join(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                    .join(DEFENSE_ORDER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(DEFENSE_ORDER.STUDENT_ID)))
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
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
                    .fetchOne()
        } else {
            create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduateArchivesBean.graduationDesignReleaseId)))
                    .join(DEFENSE_ARRANGEMENT)
                    .on(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID))
                    .join(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                    .join(DEFENSE_ORDER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(DEFENSE_ORDER.STUDENT_ID)))
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
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
                    .fetchOne()
        }
        return count.value1()
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignArchivesBean>, graduateArchivesBean: GraduationDesignArchivesBean): Int {
        val count: Record1<Int>
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduateArchivesBean)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(DEFENSE_ARRANGEMENT)
                    .on(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID))
                    .join(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                    .join(DEFENSE_ORDER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(DEFENSE_ORDER.STUDENT_ID)))
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
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
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduateArchivesBean.graduationDesignReleaseId)))
                    .join(DEFENSE_ARRANGEMENT)
                    .on(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID))
                    .join(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                    .join(DEFENSE_ORDER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(DEFENSE_ORDER.STUDENT_ID)))
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
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
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    override fun exportData(dataTablesUtils: DataTablesUtils<GraduationDesignArchivesBean>, graduationDesignArchivesBean: GraduationDesignArchivesBean): List<GraduationDesignArchivesBean> {
        val graduationDesignArchivesBeans = ArrayList<GraduationDesignArchivesBean>()
        val records: Result<Record>
        var a = searchCondition(dataTablesUtils)
        a = otherCondition(a, graduationDesignArchivesBean)
        records = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(DEFENSE_ARRANGEMENT)
                    .on(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID))
                    .join(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                    .join(DEFENSE_ORDER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(DEFENSE_ORDER.STUDENT_ID)))
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
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
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignArchivesBean.graduationDesignReleaseId)))
                    .join(DEFENSE_ARRANGEMENT)
                    .on(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID))
                    .join(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                    .join(DEFENSE_ORDER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(DEFENSE_ORDER.STUDENT_ID)))
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(ACADEMIC_TITLE)
                    .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
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
            selectConditionStep.fetch()
        }
        buildData(records, graduationDesignArchivesBeans)
        return graduationDesignArchivesBeans
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun saveAndIgnore(graduationDesignArchives: GraduationDesignArchives) {
        create.insertInto(GRADUATION_DESIGN_ARCHIVES,
                GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID,
                GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT,
                GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER,
                GRADUATION_DESIGN_ARCHIVES.NOTE)
                .values(graduationDesignArchives.graduationDesignPresubjectId,
                        graduationDesignArchives.isExcellent,
                        graduationDesignArchives.archiveNumber,
                        graduationDesignArchives.note)
                .onDuplicateKeyIgnore()
                .execute()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignArchives: GraduationDesignArchives) {
        create.insertInto(GRADUATION_DESIGN_ARCHIVES)
                .set(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID, graduationDesignArchives.graduationDesignPresubjectId)
                .set(GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER, graduationDesignArchives.archiveNumber)
                .set(GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT, graduationDesignArchives.isExcellent)
                .set(GRADUATION_DESIGN_ARCHIVES.NOTE, graduationDesignArchives.note)
                .execute()
    }

    override fun update(graduationDesignArchives: GraduationDesignArchives) {
        create.update(GRADUATION_DESIGN_ARCHIVES)
                .set(GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT, graduationDesignArchives.isExcellent)
                .set(GRADUATION_DESIGN_ARCHIVES.NOTE, graduationDesignArchives.note)
                .set(GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER, graduationDesignArchives.archiveNumber)
                .where(GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID.eq(graduationDesignArchives.graduationDesignPresubjectId))
                .execute()
    }


    /**
     * 其它条件
     *
     * @param graduateArchivesBean 条件
     * @return 条件
     */
    fun otherCondition(a: Condition?, graduateArchivesBean: GraduationDesignArchivesBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(graduateArchivesBean)) {
            if (StringUtils.hasLength(graduateArchivesBean.graduationDesignReleaseId)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduateArchivesBean.graduationDesignReleaseId))
                } else {
                    GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduateArchivesBean.graduationDesignReleaseId)
                }
            }
        }

        return tempCondition
    }

    private fun buildData(records: Result<Record>, graduateArchivesBeans: MutableList<GraduationDesignArchivesBean>) {
        for (r in records) {
            val graduateArchivesBean = GraduationDesignArchivesBean()
            graduateArchivesBean.graduationDesignReleaseId = r.getValue(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID)
            graduateArchivesBean.collegeName = r.getValue(COLLEGE.COLLEGE_NAME)
            graduateArchivesBean.collegeCode = r.getValue(COLLEGE.COLLEGE_CODE)
            graduateArchivesBean.scienceName = r.getValue(SCIENCE.SCIENCE_NAME)
            graduateArchivesBean.scienceCode = r.getValue(SCIENCE.SCIENCE_CODE)
            graduateArchivesBean.graduationDate = r.getValue(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DATE)
            graduateArchivesBean.staffName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)
            graduateArchivesBean.staffNumber = r.getValue(STAFF.STAFF_NUMBER)
            graduateArchivesBean.academicTitleName = r.getValue(ACADEMIC_TITLE.ACADEMIC_TITLE_NAME)
            graduateArchivesBean.assistantTeacher = r.getValue(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER)
            graduateArchivesBean.assistantTeacherAcademic = r.getValue(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC)
            graduateArchivesBean.assistantTeacherNumber = r.getValue(GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_NUMBER)
            graduateArchivesBean.presubjectTitle = r.getValue(GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE)
            graduateArchivesBean.subjectTypeName = r.getValue(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME)
            graduateArchivesBean.originTypeName = r.getValue(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME)
            graduateArchivesBean.studentName = r.getValue(DEFENSE_ORDER.STUDENT_NAME)
            graduateArchivesBean.studentNumber = r.getValue(DEFENSE_ORDER.STUDENT_NUMBER)
            graduateArchivesBean.scoreTypeName = r.getValue(SCORE_TYPE.SCORE_TYPE_NAME)
            graduateArchivesBean.graduationDesignPresubjectId = r.getValue(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID)
            graduateArchivesBean.isExcellent = r.getValue(GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT)
            graduateArchivesBean.archiveNumber = r.getValue(GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER)
            graduateArchivesBean.note = r.getValue(GRADUATION_DESIGN_ARCHIVES.NOTE)
            graduateArchivesBeans.add(graduateArchivesBean)
        }
    }

    /**
     * 数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<GraduationDesignArchivesBean>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val studentName = StringUtils.trimWhitespace(search!!.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val staffName = StringUtils.trimWhitespace(search.getString("staffName"))
            val staffNumber = StringUtils.trimWhitespace(search.getString("staffNumber"))

            if (StringUtils.hasLength(studentName)) {
                a = DEFENSE_ORDER.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName))
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    DEFENSE_ORDER.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(DEFENSE_ORDER.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(staffName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.like(SQLQueryUtils.likeAllParam(staffName))
                } else {
                    a!!.and(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.like(SQLQueryUtils.likeAllParam(staffName)))
                }
            }

            if (StringUtils.hasLength(staffNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber))
                } else {
                    a!!.and(STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber)))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<GraduationDesignArchivesBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("college_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("college_code".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_CODE.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_CODE.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("science_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_NAME.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = SCIENCE.SCIENCE_NAME.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("science_code".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_CODE.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = SCIENCE.SCIENCE_CODE.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("graduation_date".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DATE.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DATE.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("staff_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("staff_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STAFF.STAFF_NUMBER.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = STAFF.STAFF_NUMBER.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("academic_title_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("assistant_teacher".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("assistant_teacher_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_NUMBER.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_NUMBER.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("assistant_teacher_academic".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.ASSISTANT_TEACHER_ACADEMIC.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("presubject_title".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("subject_type_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("origin_type_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("student_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NUMBER.desc()
                }
            }

            if ("student_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NAME.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NAME.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("score_type_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCORE_TYPE.SCORE_TYPE_NAME.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = SCORE_TYPE.SCORE_TYPE_NAME.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("is_excellent".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

            if ("archive_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER.desc()
                }
            }

            if ("note".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.NOTE.asc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_ARCHIVES.NOTE.desc()
                    sortField[1] = DEFENSE_ORDER.DEFENSE_ORDER_ID.desc()
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}