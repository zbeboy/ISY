package top.zbeboy.isy.service.internship

import org.apache.commons.lang3.math.NumberUtils
import org.jooq.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.daos.InternshipRegulateDao
import top.zbeboy.isy.domain.tables.pojos.InternshipRegulate
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.internship.regulate.InternshipRegulateBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.text.ParseException
import javax.annotation.Resource

import top.zbeboy.isy.domain.Tables.INTERNSHIP_REGULATE
/**
 * Created by zbeboy 2017-12-29 .
 **/
@Service("internshipRegulateService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipRegulateServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<InternshipRegulateBean>(),InternshipRegulateService {

    private val log = LoggerFactory.getLogger(InternshipRegulateServiceImpl::class.java)

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var internshipRegulateDao: InternshipRegulateDao


    override fun findById(id: String): InternshipRegulate {
        return internshipRegulateDao.findById(id)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(internshipRegulate: InternshipRegulate) {
        internshipRegulateDao.insert(internshipRegulate)
    }

    override fun update(internshipRegulate: InternshipRegulate) {
        internshipRegulateDao.update(internshipRegulate)
    }

    override fun batchDelete(ids: List<String>) {
        internshipRegulateDao.deleteById(ids)
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<InternshipRegulateBean>, internshipRegulateBean: InternshipRegulateBean): Result<Record> {
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, INTERNSHIP_REGULATE, extraCondition(internshipRegulateBean))
    }

    override fun countAll(internshipRegulateBean: InternshipRegulateBean): Int {
        return statisticsAllWithCondition(create, INTERNSHIP_REGULATE, extraCondition(internshipRegulateBean))
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<InternshipRegulateBean>, internshipRegulateBean: InternshipRegulateBean): Int {
        return statisticsWithCondition(dataTablesUtils, create, INTERNSHIP_REGULATE, extraCondition(internshipRegulateBean))
    }

    override fun exportData(dataTablesUtils: DataTablesUtils<InternshipRegulateBean>, internshipRegulateBean: InternshipRegulateBean): Result<Record> {
        return dataPagingQueryAllWithConditionNoPage(dataTablesUtils, create, INTERNSHIP_REGULATE, extraCondition(internshipRegulateBean))
    }

    /**
     * 额外参数条件
     *
     * @param internshipRegulateBean 条件
     * @return 条件语句
     */
    private fun extraCondition(internshipRegulateBean: InternshipRegulateBean): Condition {
        var extraCondition = INTERNSHIP_REGULATE.INTERNSHIP_RELEASE_ID.eq(internshipRegulateBean.internshipReleaseId)
        if (!ObjectUtils.isEmpty(internshipRegulateBean.staffId)) {
            extraCondition = extraCondition.and(INTERNSHIP_REGULATE.STAFF_ID.eq(internshipRegulateBean.staffId))
        }
        return extraCondition
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<InternshipRegulateBean>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val staffId = StringUtils.trimWhitespace(search!!.getString("staffId"))
            val studentName = StringUtils.trimWhitespace(search.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val studentTel = StringUtils.trimWhitespace(search.getString("studentTel"))
            val schoolGuidanceTeacher = StringUtils.trimWhitespace(search.getString("schoolGuidanceTeacher"))
            val createDate = StringUtils.trimWhitespace(search.getString("createDate"))

            if (StringUtils.hasLength(studentName)) {
                a = INTERNSHIP_REGULATE.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName))
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    INTERNSHIP_REGULATE.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(INTERNSHIP_REGULATE.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(staffId)) {
                if (NumberUtils.isDigits(staffId)) {
                    val tempStaffId = NumberUtils.toInt(staffId)
                    if (tempStaffId > 0) {
                        a = if (ObjectUtils.isEmpty(a)) {
                            INTERNSHIP_REGULATE.STAFF_ID.eq(tempStaffId)
                        } else {
                            a!!.and(INTERNSHIP_REGULATE.STAFF_ID.eq(tempStaffId))
                        }
                    }
                }
            }

            if (StringUtils.hasLength(studentTel)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    INTERNSHIP_REGULATE.STUDENT_TEL.like(SQLQueryUtils.likeAllParam(studentTel))
                } else {
                    a!!.and(INTERNSHIP_REGULATE.STUDENT_TEL.like(SQLQueryUtils.likeAllParam(studentTel)))
                }
            }

            if (StringUtils.hasLength(schoolGuidanceTeacher)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher))
                } else {
                    a!!.and(INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher)))
                }
            }

            if (StringUtils.hasLength(createDate)) {
                try {
                    val format = "yyyy-MM-dd HH:mm:ss"
                    val createDateArr = DateTimeUtils.splitDateTime("至", createDate)
                    a = if (ObjectUtils.isEmpty(a)) {
                        INTERNSHIP_REGULATE.CREATE_DATE.ge(DateTimeUtils.formatDateToTimestamp(createDateArr[0], format)).and(INTERNSHIP_REGULATE.CREATE_DATE.le(DateTimeUtils.formatDateToTimestamp(createDateArr[1], format)))
                    } else {
                        a!!.and(INTERNSHIP_REGULATE.CREATE_DATE.ge(DateTimeUtils.formatDateToTimestamp(createDateArr[0], format))).and(INTERNSHIP_REGULATE.CREATE_DATE.le(DateTimeUtils.formatDateToTimestamp(createDateArr[1], format)))
                    }
                } catch (e: ParseException) {
                    log.error("Format time error, error is {}", e)
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<InternshipRegulateBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_NAME.asc()
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.asc()
                } else {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_NAME.desc()
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.desc()
                }
            }

            if ("student_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_NUMBER.asc()
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.asc()
                } else {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_NUMBER.desc()
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.desc()
                }
            }

            if ("student_tel".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_TEL.asc()
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.asc()
                } else {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_TEL.desc()
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.desc()
                }
            }

            if ("school_guidance_teacher".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER.asc()
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.asc()
                } else {
                    sortField[0] = INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER.desc()
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.desc()
                }
            }

            if ("create_date".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_REGULATE.CREATE_DATE.asc()
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.asc()
                } else {
                    sortField[0] = INTERNSHIP_REGULATE.CREATE_DATE.desc()
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.desc()
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}