package top.zbeboy.isy.service.graduate.design

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.ArrayList

/**
 * Created by zbeboy 2018-02-08 .
 **/
@Service("graduationDesignManifestService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignManifestServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<GraduationDesignDeclareBean>(), GraduationDesignManifestService {

    private val create: DSLContext = dslContext

    override fun findAllManifestByPage(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): List<GraduationDesignDeclareBean> {
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
                    .where(a)
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }
        buildManifestData(records, graduationDesignDeclareBeens)
        return graduationDesignDeclareBeens
    }

    override fun countAllManifest(graduationDesignDeclareBean: GraduationDesignDeclareBean): Int {
        val count: Record1<Int>
        val a = otherCondition(null, graduationDesignDeclareBean)
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
                    .fetchOne()
        } else {
            create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId)))
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
                    .where(a)
                    .fetchOne()
        }
        return count.value1()
    }

    override fun countManifestByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): Int {
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
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId)))
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
                    .where(a)
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    override fun exportManifestData(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): List<GraduationDesignDeclareBean> {
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
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId)))
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
                    .where(a)
            selectConditionStep.fetch()
        }
        buildManifestData(records, graduationDesignDeclareBeens)
        return graduationDesignDeclareBeens
    }


    private fun buildManifestData(records: Result<Record>, graduationDesignDeclareBeens: MutableList<GraduationDesignDeclareBean>) {
        for (r in records) {
            val tempGraduationDesignDeclareBean = GraduationDesignDeclareBean()
            tempGraduationDesignDeclareBean.subjectTypeId = r.getValue(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID)
            tempGraduationDesignDeclareBean.subjectTypeName = r.getValue(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME)
            tempGraduationDesignDeclareBean.originTypeId = r.getValue(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID)
            tempGraduationDesignDeclareBean.originTypeName = r.getValue(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME)
            tempGraduationDesignDeclareBean.guidePeoples = r.getValue(GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES)
            tempGraduationDesignDeclareBean.graduationDesignPresubjectId = r.getValue(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID)
            tempGraduationDesignDeclareBean.staffId = r.getValue(STAFF.STAFF_ID)
            tempGraduationDesignDeclareBean.staffName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)
            tempGraduationDesignDeclareBean.academicTitleName = r.getValue(ACADEMIC_TITLE.ACADEMIC_TITLE_NAME)
            tempGraduationDesignDeclareBean.studentName = r.getValue(DEFENSE_ORDER.STUDENT_NAME)
            tempGraduationDesignDeclareBean.studentNumber = r.getValue(DEFENSE_ORDER.STUDENT_NUMBER)
            tempGraduationDesignDeclareBean.presubjectTitle = r.getValue(GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE)
            tempGraduationDesignDeclareBean.graduationDesignReleaseId = r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID)
            tempGraduationDesignDeclareBean.scoreTypeName = r.getValue(SCORE_TYPE.SCORE_TYPE_NAME)
            tempGraduationDesignDeclareBean.defenseOrderId = r.getValue(DEFENSE_ORDER.DEFENSE_ORDER_ID)
            graduationDesignDeclareBeens.add(tempGraduationDesignDeclareBean)
        }
    }

    /**
     * 其它条件
     *
     * @param graduationDesignDeclareBean 条件
     * @return 条件
     */
    fun otherCondition(a: Condition?, graduationDesignDeclareBean: GraduationDesignDeclareBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(graduationDesignDeclareBean)) {
            if (!ObjectUtils.isEmpty(graduationDesignDeclareBean.staffId) && graduationDesignDeclareBean.staffId > 0) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDeclareBean.staffId))
                } else {
                    GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDeclareBean.staffId)
                }
            }

            if (StringUtils.hasLength(graduationDesignDeclareBean.graduationDesignReleaseId)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId))
                } else {
                    GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.graduationDesignReleaseId)
                }
            }
        }

        return tempCondition
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
            val studentName = StringUtils.trimWhitespace(search!!.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))

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

            if ("guide_teacher".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.desc()
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
                    sortField[0] = DEFENSE_ORDER.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NUMBER.desc()
                }
            }

            if ("student_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NAME.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NAME.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

            if ("score_type_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCORE_TYPE.SCORE_TYPE_ID.asc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc()
                } else {
                    sortField[0] = SCORE_TYPE.SCORE_TYPE_ID.desc()
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc()
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}