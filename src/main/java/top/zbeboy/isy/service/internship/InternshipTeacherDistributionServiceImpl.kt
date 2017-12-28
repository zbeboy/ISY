package top.zbeboy.isy.service.internship

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution
import top.zbeboy.isy.domain.tables.records.InternshipTeacherDistributionRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-12-21 .
 **/
@Service("internshipTeacherDistributionService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipTeacherDistributionServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<InternshipTeacherDistributionBean>(), InternshipTeacherDistributionService {

    private val create: DSLContext = dslContext


    override fun findByInternshipReleaseIdDistinctOrganizeId(internshipReleaseId: String): Result<Record1<Int>> {
        return create.selectDistinct(STUDENT.ORGANIZE_ID)
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .join(STUDENT)
                .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetch()
    }

    override fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Optional<Record> {
        return create.select()
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(studentId)))
                .fetchOptional()
    }

    override fun findInInternshipReleaseIdsDistinctStudentId(internshipReleaseIds: List<String>): Result<Record3<Int, String, Int>> {
        return create.selectDistinct(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID, INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_REAL_NAME, INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID)
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.`in`(internshipReleaseIds))
                .fetch()
    }

    override fun findByInternshipReleaseIdDistinctStaffId(internshipReleaseId: String): Result<Record3<Int, String, String>> {
        return create.selectDistinct(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID,
                USERS.REAL_NAME, USERS.MOBILE)
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .join(STAFF)
                .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetch()
    }

    override fun findByInternshipReleaseIdAndStaffIdForStudent(internshipReleaseId: String, staffId: Int): Result<Record> {
        return create.select()
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .join(STUDENT)
                .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(staffId)))
                .fetch()
    }

    override fun findByInternshipReleaseIdAndStudentIdForStaff(internshipReleaseId: String, studentId: Int): Optional<Record> {
        return create.select()
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .join(STAFF)
                .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(studentId)))
                .fetchOptional()
    }

    override fun findByInternshipReleaseIdAndStaffId(internshipReleaseId: String, staffId: Int): Result<InternshipTeacherDistributionRecord> {
        return create.selectFrom(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(staffId)))
                .fetch()
    }

    override fun findStudentForBatchDistributionEnabledAndVerifyMailbox(organizeIds: List<Int>, internshipReleaseId: List<String>, enabled: Byte?, verifyMailbox: Byte?): Result<Record> {
        val internshipTeacherDistributionRecords = create.selectFrom(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.`in`(internshipReleaseId)
                        .and(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID)))
        return create.select()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(STUDENT.ORGANIZE_ID.`in`(organizeIds).andNotExists(internshipTeacherDistributionRecords).and(USERS.ENABLED.eq(enabled)).and(USERS.VERIFY_MAILBOX.eq(verifyMailbox)))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(internshipTeacherDistribution: InternshipTeacherDistribution) {
        create.insertInto(INTERNSHIP_TEACHER_DISTRIBUTION)
                .set(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID, internshipTeacherDistribution.internshipReleaseId)
                .set(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID, internshipTeacherDistribution.staffId)
                .set(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID, internshipTeacherDistribution.studentId)
                .set(INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME, internshipTeacherDistribution.username)
                .execute()
    }

    override fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int) {
        create.deleteFrom(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(studentId)))
                .execute()
    }

    override fun deleteByInternshipReleaseId(internshipReleaseId: String) {
        create.deleteFrom(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .execute()
    }

    override fun comparisonDel(internshipReleaseId: String, excludeInternships: List<String>) {
        val temp = create.select(INTERNSHIP_TEACHER_DISTRIBUTION.`as`("A").STUDENT_ID)
                .from(INTERNSHIP_TEACHER_DISTRIBUTION.`as`("A"))
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.`as`("A").INTERNSHIP_RELEASE_ID.`in`(excludeInternships))

        val internshipTeacherDistributionRecords = create.select(INTERNSHIP_TEACHER_DISTRIBUTION.`as`("B").STUDENT_ID)
                .from(temp.asTable("B"))
        create.deleteFrom(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)
                        .and(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.`in`(internshipTeacherDistributionRecords)))
                .execute()
    }

    override fun deleteNotApply(internshipReleaseId: String) {
        val internshipApplyRecord = create.selectFrom(INTERNSHIP_APPLY)
                .where(INTERNSHIP_APPLY.STUDENT_ID.eq(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID).and(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID)))
        create.deleteFrom(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).andNotExists(internshipApplyRecord))
                .execute()
    }

    override fun updateStaffId(internshipTeacherDistribution: InternshipTeacherDistribution) {
        create.update(INTERNSHIP_TEACHER_DISTRIBUTION)
                .set(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID, internshipTeacherDistribution.staffId)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipTeacherDistribution.internshipReleaseId)
                        .and(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(internshipTeacherDistribution.studentId)))
                .execute()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<InternshipTeacherDistributionBean>, internshipReleaseId: String): List<InternshipTeacherDistributionBean> {
        val records: Result<Record>
        val a = searchCondition(dataTablesUtils)
        records = if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.select()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STAFF)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STAFF)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)).and(a)
            sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
            selectConditionStep.fetch()
        }

        return buildInternshipTeacherDistributionBeenList(records)
    }

    override fun countAll(internshipReleaseId: String): Int {
        return create.selectCount()
                .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetchOne().value1()
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<InternshipTeacherDistributionBean>, internshipReleaseId: String): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
            selectConditionStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STAFF)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)).and(a)
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    override fun exportData(dataTablesUtils: DataTablesUtils<InternshipTeacherDistributionBean>, internshipReleaseId: String): List<InternshipTeacherDistributionBean> {
        val records: Result<Record>
        val a = searchCondition(dataTablesUtils)
        records = if (ObjectUtils.isEmpty(a)) {
            val selectConditionStep = create.select()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STAFF)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
            selectConditionStep.fetch()
        } else {
            val selectConditionStep = create.select()
                    .from(INTERNSHIP_TEACHER_DISTRIBUTION)
                    .join(STAFF)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .join(STUDENT)
                    .on(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .where(INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)).and(a)
            selectConditionStep.fetch()
        }

        return buildInternshipTeacherDistributionBeenList(records)
    }

    /**
     * 构建分配数据
     */
    private fun buildInternshipTeacherDistributionBeenList(records: Result<Record>): ArrayList<InternshipTeacherDistributionBean> {
        val internshipTeacherDistributionBeens = ArrayList<InternshipTeacherDistributionBean>()
        for (r in records) {
            val internshipTeacherDistributionBeen = InternshipTeacherDistributionBean()
            internshipTeacherDistributionBeen.studentRealName = r.getValue(INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_REAL_NAME)
            internshipTeacherDistributionBeen.studentUsername = r.getValue(STUDENT.USERNAME)
            internshipTeacherDistributionBeen.studentNumber = r.getValue(STUDENT.STUDENT_NUMBER)
            internshipTeacherDistributionBeen.studentId = r.getValue(STUDENT.STUDENT_ID)
            internshipTeacherDistributionBeen.staffRealName = r.getValue(USERS.REAL_NAME)
            internshipTeacherDistributionBeen.staffUsername = r.getValue(STAFF.USERNAME)
            internshipTeacherDistributionBeen.staffNumber = r.getValue(STAFF.STAFF_NUMBER)
            internshipTeacherDistributionBeen.assigner = r.getValue(INTERNSHIP_TEACHER_DISTRIBUTION.ASSIGNER)
            internshipTeacherDistributionBeen.username = r.getValue(INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME)
            internshipTeacherDistributionBeens.add(internshipTeacherDistributionBeen)
        }
        return internshipTeacherDistributionBeens
    }

    /**
     * 数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<InternshipTeacherDistributionBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val studentUsername = StringUtils.trimWhitespace(search!!.getString("studentUsername"))
            val staffUsername = StringUtils.trimWhitespace(search.getString("staffUsername"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val staffNumber = StringUtils.trimWhitespace(search.getString("staffNumber"))
            val assigner = StringUtils.trimWhitespace(search.getString("assigner"))
            val username = StringUtils.trimWhitespace(search.getString("username"))
            if (StringUtils.hasLength(studentUsername)) {
                a = STUDENT.USERNAME.like(SQLQueryUtils.likeAllParam(studentUsername))
            }

            if (StringUtils.hasLength(staffUsername)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.USERNAME.like(SQLQueryUtils.likeAllParam(staffUsername))
                } else {
                    a = a!!.and(STAFF.USERNAME.like(SQLQueryUtils.likeAllParam(staffUsername)))
                }
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a = a!!.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(staffNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber))
                } else {
                    a = a!!.and(STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber)))
                }
            }

            if (StringUtils.hasLength(assigner)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = INTERNSHIP_TEACHER_DISTRIBUTION.ASSIGNER.like(SQLQueryUtils.likeAllParam(assigner))
                } else {
                    a = a!!.and(INTERNSHIP_TEACHER_DISTRIBUTION.ASSIGNER.like(SQLQueryUtils.likeAllParam(assigner)))
                }
            }

            if (StringUtils.hasLength(username)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME.like(SQLQueryUtils.likeAllParam(username))
                } else {
                    a = a!!.and(INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME.like(SQLQueryUtils.likeAllParam(username)))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<InternshipTeacherDistributionBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {

            if ("student_real_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_REAL_NAME.asc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_REAL_NAME.desc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.desc()
                }
            }

            if ("student_username".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = STUDENT.USERNAME.asc()
                } else {
                    sortField[0] = STUDENT.USERNAME.desc()
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

            if ("staff_real_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = USERS.REAL_NAME.desc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.desc()
                }
            }

            if ("staff_username".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STAFF.USERNAME.asc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = STAFF.USERNAME.desc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.desc()
                }
            }

            if ("staff_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STAFF.STAFF_NUMBER.asc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = STAFF.STAFF_NUMBER.desc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.desc()
                }
            }

            if ("real_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_TEACHER_DISTRIBUTION.ASSIGNER.asc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = INTERNSHIP_TEACHER_DISTRIBUTION.ASSIGNER.desc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.desc()
                }
            }

            if ("username".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME.asc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME.desc()
                    sortField[1] = STUDENT.STUDENT_NUMBER.desc()
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}