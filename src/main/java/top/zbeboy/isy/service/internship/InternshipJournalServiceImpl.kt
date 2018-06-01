package top.zbeboy.isy.service.internship

import org.jooq.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.INTERNSHIP_JOURNAL
import org.jooq.impl.DSL.*
import top.zbeboy.isy.domain.Tables.INTERNSHIP_TEACHER_DISTRIBUTION
import top.zbeboy.isy.domain.tables.daos.InternshipJournalDao
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal
import top.zbeboy.isy.domain.tables.records.InternshipJournalRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.internship.journal.InternshipJournalBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.text.ParseException
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-29 .
 **/
@Service("internshipJournalService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipJournalServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<InternshipJournalBean>(), InternshipJournalService {

    private val log = LoggerFactory.getLogger(InternshipJournalServiceImpl::class.java)

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var internshipJournalDao: InternshipJournalDao

    override fun findById(id: String): InternshipJournal {
        return internshipJournalDao.findById(id)
    }

    override fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Result<InternshipJournalRecord> {
        return create.selectFrom(INTERNSHIP_JOURNAL)
                .where(INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_JOURNAL.STUDENT_ID.eq(studentId)))
                .fetch()
    }

    override fun findByInternshipReleaseIdAndStaffId(internshipReleaseId: String, staffId: Int): Result<InternshipJournalRecord> {
        return create.selectFrom(INTERNSHIP_JOURNAL)
                .where(INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_JOURNAL.STAFF_ID.eq(staffId)))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(internshipJournal: InternshipJournal) {
        internshipJournalDao.insert(internshipJournal)
    }

    override fun update(internshipJournal: InternshipJournal) {
        internshipJournalDao.update(internshipJournal)
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<InternshipJournalBean>, internshipJournalBean: InternshipJournalBean): Result<Record> {
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, INTERNSHIP_JOURNAL, extraCondition(internshipJournalBean))
    }

    override fun countAll(internshipJournalBean: InternshipJournalBean): Int {
        return statisticsAllWithCondition(create, INTERNSHIP_JOURNAL, extraCondition(internshipJournalBean))
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<InternshipJournalBean>, internshipJournalBean: InternshipJournalBean): Int {
        return statisticsWithCondition(dataTablesUtils, create, INTERNSHIP_JOURNAL, extraCondition(internshipJournalBean))
    }

    override fun deleteById(id: String) {
        internshipJournalDao.deleteById(id)
    }

    override fun countTeamJournalNum(internshipReleaseId: String, staffId: Int): Result<out Record3<String, String, out Any>> {
        val countAlias = InternshipJournalBean.JOURNAL_NUM
        val journalTable =
                create.select(INTERNSHIP_JOURNAL.STUDENT_NUMBER,
                        INTERNSHIP_JOURNAL.STUDENT_ID,
                        count(INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID).`as`(countAlias))
                        .from(INTERNSHIP_JOURNAL)
                        .where(INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)
                                .and(INTERNSHIP_JOURNAL.STAFF_ID.eq(staffId)))
                        .groupBy(INTERNSHIP_JOURNAL.STUDENT_ID)
        return create.select(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_REAL_NAME,
                journalTable.field(INTERNSHIP_JOURNAL.STUDENT_NUMBER),
                journalTable.field(countAlias))
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .leftJoin(journalTable)
                .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(journalTable.field(INTERNSHIP_JOURNAL.STUDENT_ID)))
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)
                        .and(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(staffId)))
                .fetch()
    }

    /**
     * 额外参数条件
     *
     * @param internshipJournalBean 条件
     * @return 条件语句
     */
    private fun extraCondition(internshipJournalBean: InternshipJournalBean): Condition {
        var extraCondition = INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipJournalBean.internshipReleaseId)
        if (!ObjectUtils.isEmpty(internshipJournalBean.studentId)) {
            extraCondition = extraCondition.and(INTERNSHIP_JOURNAL.STUDENT_ID.eq(internshipJournalBean.studentId))
        }
        if (!ObjectUtils.isEmpty(internshipJournalBean.staffId)) {
            extraCondition = extraCondition.and(INTERNSHIP_JOURNAL.STAFF_ID.eq(internshipJournalBean.staffId))
        }
        return extraCondition
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<InternshipJournalBean>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val studentName = StringUtils.trimWhitespace(search!!.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val organize = StringUtils.trimWhitespace(search.getString("organize"))
            val guidanceTeacher = StringUtils.trimWhitespace(search.getString("guidanceTeacher"))
            val createDate = StringUtils.trimWhitespace(search.getString("createDate"))

            if (StringUtils.hasLength(studentName)) {
                a = INTERNSHIP_JOURNAL.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName))
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    INTERNSHIP_JOURNAL.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(INTERNSHIP_JOURNAL.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(organize)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    INTERNSHIP_JOURNAL.ORGANIZE.like(SQLQueryUtils.likeAllParam(organize))
                } else {
                    a!!.and(INTERNSHIP_JOURNAL.ORGANIZE.like(SQLQueryUtils.likeAllParam(organize)))
                }
            }

            if (StringUtils.hasLength(guidanceTeacher)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(guidanceTeacher))
                } else {
                    a!!.and(INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(guidanceTeacher)))
                }
            }

            if (StringUtils.hasLength(createDate)) {
                try {
                    val format = "yyyy-MM-dd HH:mm:ss"
                    val createDateArr = DateTimeUtils.splitDateTime("至", createDate)
                    a = if (ObjectUtils.isEmpty(a)) {
                        INTERNSHIP_JOURNAL.CREATE_DATE.ge(DateTimeUtils.formatDateToTimestamp(createDateArr[0], format)).and(INTERNSHIP_JOURNAL.CREATE_DATE.le(DateTimeUtils.formatDateToTimestamp(createDateArr[1], format)))
                    } else {
                        a!!.and(INTERNSHIP_JOURNAL.CREATE_DATE.ge(DateTimeUtils.formatDateToTimestamp(createDateArr[0], format))).and(INTERNSHIP_JOURNAL.CREATE_DATE.le(DateTimeUtils.formatDateToTimestamp(createDateArr[1], format)))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<InternshipJournalBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_JOURNAL.STUDENT_NAME.asc()
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.asc()
                } else {
                    sortField[0] = INTERNSHIP_JOURNAL.STUDENT_NAME.desc()
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.desc()
                }
            }

            if ("student_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_JOURNAL.STUDENT_NUMBER.asc()
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.asc()
                } else {
                    sortField[0] = INTERNSHIP_JOURNAL.STUDENT_NUMBER.desc()
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.desc()
                }
            }

            if ("organize".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_JOURNAL.ORGANIZE.asc()
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.asc()
                } else {
                    sortField[0] = INTERNSHIP_JOURNAL.ORGANIZE.desc()
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.desc()
                }
            }

            if ("school_guidance_teacher".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER.asc()
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.asc()
                } else {
                    sortField[0] = INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER.desc()
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.desc()
                }
            }

            if ("create_date".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_JOURNAL.CREATE_DATE.asc()
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.asc()
                } else {
                    sortField[0] = INTERNSHIP_JOURNAL.CREATE_DATE.desc()
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.desc()
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}