package top.zbeboy.isy.service.internship

import org.apache.commons.lang.math.NumberUtils
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.records.InternshipApplyRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.internship.statistics.InternshipStatisticsBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-28 .
 **/
@Service("internshipStatisticsService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipStatisticsServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<InternshipStatisticsBean>(), InternshipStatisticsService {

    private val create: DSLContext = dslContext

    override fun submittedFindAllByPage(dataTablesUtils: DataTablesUtils<InternshipStatisticsBean>, internshipStatisticsBean: InternshipStatisticsBean): Result<Record> {
        var a = searchCondition(dataTablesUtils)
        a = submittedOtherCondition(a, internshipStatisticsBean)
        return if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(INTERNSHIP_APPLY)
                    .join(STUDENT)
                    .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
            sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.CONDITION_TYPE)
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(INTERNSHIP_APPLY)
                    .join(STUDENT)
                    .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(a)
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }
    }

    override fun submittedCountAll(internshipStatisticsBean: InternshipStatisticsBean): Int {
        val a = submittedOtherCondition(null, internshipStatisticsBean)
        return if (ObjectUtils.isEmpty(a)) {
            create.selectCount()
                    .from(INTERNSHIP_APPLY)
                    .fetchOne().value1()
        } else {
            create.selectCount()
                    .from(INTERNSHIP_APPLY)
                    .where(a)
                    .fetchOne().value1()
        }
    }

    override fun submittedCountByCondition(dataTablesUtils: DataTablesUtils<InternshipStatisticsBean>, internshipStatisticsBean: InternshipStatisticsBean): Int {
        val count: Record1<Int>
        var a = searchCondition(dataTablesUtils)
        a = submittedOtherCondition(a, internshipStatisticsBean)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(INTERNSHIP_APPLY)
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_APPLY)
                    .join(STUDENT)
                    .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(a)
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    /**
     * 组装exists条件
     *
     * @param internshipStatisticsBean 实习统计
     * @return select
     */
    private fun existsInternshipApplySelect(internshipStatisticsBean: InternshipStatisticsBean): Select<InternshipApplyRecord> {
        return create.selectFrom(INTERNSHIP_APPLY)
                .where(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .and(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipStatisticsBean.internshipReleaseId))
    }

    override fun unsubmittedFindAllByPage(dataTablesUtils: DataTablesUtils<InternshipStatisticsBean>, internshipStatisticsBean: InternshipStatisticsBean): Result<Record> {
        var a = searchCondition(dataTablesUtils)
        a = unsubmittedOtherCondition(a, internshipStatisticsBean)
        return if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.select()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .andNotExists(existsInternshipApplySelect(internshipStatisticsBean))
            sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.CONDITION_TYPE)
            selectJoinStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(a)
                    .andNotExists(existsInternshipApplySelect(internshipStatisticsBean))
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }
    }

    override fun unsubmittedCountAll(internshipStatisticsBean: InternshipStatisticsBean): Int {
        val a = unsubmittedOtherCondition(null, internshipStatisticsBean)
        return if (ObjectUtils.isEmpty(a)) {
            create.selectCount()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .andNotExists(existsInternshipApplySelect(internshipStatisticsBean))
                    .fetchOne().value1()
        } else {
            create.selectCount()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(a)
                    .andNotExists(existsInternshipApplySelect(internshipStatisticsBean))
                    .fetchOne().value1()
        }
    }

    override fun unsubmittedCountByCondition(dataTablesUtils: DataTablesUtils<InternshipStatisticsBean>, internshipStatisticsBean: InternshipStatisticsBean): Int {
        val count: Record1<Int>
        var a = searchCondition(dataTablesUtils)
        a = unsubmittedOtherCondition(a, internshipStatisticsBean)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .andNotExists(existsInternshipApplySelect(internshipStatisticsBean))
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(a)
                    .andNotExists(existsInternshipApplySelect(internshipStatisticsBean))
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<InternshipStatisticsBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val studentName = StringUtils.trimWhitespace(search!!.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val scienceName = StringUtils.trimWhitespace(search.getString("scienceName"))
            val organizeName = StringUtils.trimWhitespace(search.getString("organizeName"))
            val internshipApplyState = StringUtils.trimWhitespace(search.getString("internshipApplyState"))
            if (StringUtils.hasLength(studentName)) {
                a = USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName))
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(scienceName)) {
                val scienceId = NumberUtils.toInt(scienceName)
                if (scienceId > 0) {
                    a = if (ObjectUtils.isEmpty(a)) {
                        SCIENCE.SCIENCE_ID.eq(scienceId)
                    } else {
                        a!!.and(SCIENCE.SCIENCE_ID.eq(scienceId))
                    }
                }
            }

            if (StringUtils.hasLength(organizeName)) {
                val organizeId = NumberUtils.toInt(organizeName)
                if (organizeId > 0) {
                    a = if (ObjectUtils.isEmpty(a)) {
                        ORGANIZE.ORGANIZE_ID.eq(organizeId)
                    } else {
                        a!!.and(ORGANIZE.ORGANIZE_ID.eq(organizeId))
                    }
                }
            }

            if (StringUtils.hasLength(internshipApplyState)) {
                val internshipApplyStateNum = NumberUtils.toInt(internshipApplyState)
                if (internshipApplyStateNum > -1) {
                    a = if (ObjectUtils.isEmpty(a)) {
                        INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(internshipApplyStateNum)
                    } else {
                        a!!.and(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(internshipApplyStateNum))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<InternshipStatisticsBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.REAL_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
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

            if ("science_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = SCIENCE.SCIENCE_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("organize_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("internship_apply_state".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }

    /**
     * 其它条件参数
     *
     * @param a                   搜索条件
     * @param internshipStatisticsBean 额外参数
     * @return 条件
     */
    private fun submittedOtherCondition(a: Condition?, internshipStatisticsBean: InternshipStatisticsBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(internshipStatisticsBean)) {
            if (StringUtils.hasLength(internshipStatisticsBean.internshipReleaseId)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipStatisticsBean.internshipReleaseId))
                } else {
                    INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipStatisticsBean.internshipReleaseId)
                }

            }
        }
        return tempCondition
    }

    /**
     * 其它条件参数
     *
     * @param a                   搜索条件
     * @param internshipStatisticsBean 额外参数
     * @return 条件
     */
    private fun unsubmittedOtherCondition(a: Condition?, internshipStatisticsBean: InternshipStatisticsBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(internshipStatisticsBean)) {
            if (StringUtils.hasLength(internshipStatisticsBean.internshipReleaseId)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipStatisticsBean.internshipReleaseId))
                } else {
                    INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipStatisticsBean.internshipReleaseId)
                }

            }
        }
        return tempCondition
    }
}