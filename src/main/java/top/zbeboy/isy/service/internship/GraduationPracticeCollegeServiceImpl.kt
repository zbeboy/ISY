package top.zbeboy.isy.service.internship

import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.GraduationPracticeCollegeDao
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege
import top.zbeboy.isy.domain.tables.records.GraduationPracticeCollegeRecord
import top.zbeboy.isy.domain.tables.records.InternshipApplyRecord
import top.zbeboy.isy.domain.tables.records.InternshipChangeHistoryRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.internship.apply.GraduationPracticeCollegeVo
import java.sql.Timestamp
import java.time.Clock
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-27 .
 **/
@Service("graduationPracticeCollegeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationPracticeCollegeServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<GraduationPracticeCollege>(), GraduationPracticeCollegeService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationPracticeCollegeDao: GraduationPracticeCollegeDao


    override fun findById(id: String): GraduationPracticeCollege {
        return graduationPracticeCollegeDao.findById(id)
    }

    override fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Optional<Record> {
        return create.select()
                .from(GRADUATION_PRACTICE_COLLEGE)
                .where(GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_COLLEGE.STUDENT_ID.eq(studentId)))
                .fetchOptional()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationPracticeCollege: GraduationPracticeCollege) {
        graduationPracticeCollegeDao.insert(graduationPracticeCollege)
    }

    override fun saveWithTransaction(graduationPracticeCollegeVo: GraduationPracticeCollegeVo) {
        create.transaction { configuration ->
            val now = Timestamp(Clock.systemDefaultZone().millis())
            val state = 0
            DSL.using(configuration)
                    .insertInto<InternshipApplyRecord>(INTERNSHIP_APPLY)
                    .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_ID, UUIDUtils.getUUID())
                    .set(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID, graduationPracticeCollegeVo.internshipReleaseId)
                    .set(INTERNSHIP_APPLY.STUDENT_ID, graduationPracticeCollegeVo.studentId)
                    .set(INTERNSHIP_APPLY.APPLY_TIME, now)
                    .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE, state)
                    .execute()

            val headmasterArr = graduationPracticeCollegeVo.headmaster!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (headmasterArr.size >= 2) {
                graduationPracticeCollegeVo.headmaster = headmasterArr[0]
                graduationPracticeCollegeVo.headmasterContact = headmasterArr[1]
            }
            val schoolGuidanceTeacherArr = graduationPracticeCollegeVo.schoolGuidanceTeacher!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (schoolGuidanceTeacherArr.size >= 2) {
                graduationPracticeCollegeVo.schoolGuidanceTeacher = schoolGuidanceTeacherArr[0]
                graduationPracticeCollegeVo.schoolGuidanceTeacherTel = schoolGuidanceTeacherArr[1]
            }

            DSL.using(configuration)
                    .insertInto<GraduationPracticeCollegeRecord>(GRADUATION_PRACTICE_COLLEGE)
                    .set(GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID, UUIDUtils.getUUID())
                    .set(GRADUATION_PRACTICE_COLLEGE.STUDENT_ID, graduationPracticeCollegeVo.studentId)
                    .set(GRADUATION_PRACTICE_COLLEGE.STUDENT_USERNAME, graduationPracticeCollegeVo.studentUsername)
                    .set(GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID, graduationPracticeCollegeVo.internshipReleaseId)
                    .set(GRADUATION_PRACTICE_COLLEGE.STUDENT_NAME, graduationPracticeCollegeVo.studentName)
                    .set(GRADUATION_PRACTICE_COLLEGE.COLLEGE_CLASS, graduationPracticeCollegeVo.collegeClass)
                    .set(GRADUATION_PRACTICE_COLLEGE.STUDENT_SEX, graduationPracticeCollegeVo.studentSex)
                    .set(GRADUATION_PRACTICE_COLLEGE.STUDENT_NUMBER, graduationPracticeCollegeVo.studentNumber)
                    .set(GRADUATION_PRACTICE_COLLEGE.PHONE_NUMBER, graduationPracticeCollegeVo.phoneNumber)
                    .set(GRADUATION_PRACTICE_COLLEGE.QQ_MAILBOX, graduationPracticeCollegeVo.qqMailbox)
                    .set(GRADUATION_PRACTICE_COLLEGE.PARENTAL_CONTACT, graduationPracticeCollegeVo.parentalContact)
                    .set(GRADUATION_PRACTICE_COLLEGE.HEADMASTER, graduationPracticeCollegeVo.headmaster)
                    .set(GRADUATION_PRACTICE_COLLEGE.HEADMASTER_CONTACT, graduationPracticeCollegeVo.headmasterContact)
                    .set(GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_NAME, graduationPracticeCollegeVo.graduationPracticeCollegeName)
                    .set(GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ADDRESS, graduationPracticeCollegeVo.graduationPracticeCollegeAddress)
                    .set(GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_CONTACTS, graduationPracticeCollegeVo.graduationPracticeCollegeContacts)
                    .set(GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_TEL, graduationPracticeCollegeVo.graduationPracticeCollegeTel)
                    .set(GRADUATION_PRACTICE_COLLEGE.SCHOOL_GUIDANCE_TEACHER, graduationPracticeCollegeVo.schoolGuidanceTeacher)
                    .set(GRADUATION_PRACTICE_COLLEGE.SCHOOL_GUIDANCE_TEACHER_TEL, graduationPracticeCollegeVo.schoolGuidanceTeacherTel)
                    .set(GRADUATION_PRACTICE_COLLEGE.START_TIME, DateTimeUtils.formatDate(graduationPracticeCollegeVo.startTime!!))
                    .set(GRADUATION_PRACTICE_COLLEGE.END_TIME, DateTimeUtils.formatDate(graduationPracticeCollegeVo.endTime!!))
                    .execute()

            DSL.using(configuration)
                    .insertInto<InternshipChangeHistoryRecord>(INTERNSHIP_CHANGE_HISTORY)
                    .set(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_CHANGE_HISTORY_ID, UUIDUtils.getUUID())
                    .set(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_RELEASE_ID, graduationPracticeCollegeVo.internshipReleaseId)
                    .set(INTERNSHIP_CHANGE_HISTORY.STUDENT_ID, graduationPracticeCollegeVo.studentId)
                    .set(INTERNSHIP_CHANGE_HISTORY.STATE, state)
                    .set(INTERNSHIP_CHANGE_HISTORY.APPLY_TIME, now)
                    .execute()
        }
    }

    override fun update(graduationPracticeCollege: GraduationPracticeCollege) {
        graduationPracticeCollegeDao.update(graduationPracticeCollege)
    }

    override fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int) {
        create.deleteFrom<GraduationPracticeCollegeRecord>(GRADUATION_PRACTICE_COLLEGE)
                .where(GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_COLLEGE.STUDENT_ID.eq(studentId)))
                .execute()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationPracticeCollege>, graduationPracticeCollege: GraduationPracticeCollege): Result<Record> {
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, GRADUATION_PRACTICE_COLLEGE, GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCollege.internshipReleaseId))
    }

    override fun countAll(graduationPracticeCollege: GraduationPracticeCollege): Int {
        return statisticsAllWithCondition(create, GRADUATION_PRACTICE_COLLEGE, GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCollege.internshipReleaseId))
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeCollege>, graduationPracticeCollege: GraduationPracticeCollege): Int {
        return statisticsWithCondition(dataTablesUtils, create, GRADUATION_PRACTICE_COLLEGE, GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCollege.internshipReleaseId))
    }

    override fun exportData(dataTablesUtils: DataTablesUtils<GraduationPracticeCollege>, graduationPracticeCollege: GraduationPracticeCollege): Result<Record> {
        return dataPagingQueryAllWithConditionNoPage(dataTablesUtils, create, GRADUATION_PRACTICE_COLLEGE, GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCollege.internshipReleaseId))
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeCollege>): Condition? {
        var a: Condition? = null
        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val studentName = StringUtils.trimWhitespace(search!!.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val collegeClass = StringUtils.trimWhitespace(search.getString("collegeClass"))
            val phoneNumber = StringUtils.trimWhitespace(search.getString("phoneNumber"))
            val headmaster = StringUtils.trimWhitespace(search.getString("headmaster"))
            val schoolGuidanceTeacher = StringUtils.trimWhitespace(search.getString("schoolGuidanceTeacher"))
            if (StringUtils.hasLength(studentName)) {
                a = GRADUATION_PRACTICE_COLLEGE.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName))
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_COLLEGE.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(GRADUATION_PRACTICE_COLLEGE.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(collegeClass)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_COLLEGE.COLLEGE_CLASS.like(SQLQueryUtils.likeAllParam(collegeClass))
                } else {
                    a!!.and(GRADUATION_PRACTICE_COLLEGE.COLLEGE_CLASS.like(SQLQueryUtils.likeAllParam(collegeClass)))
                }
            }

            if (StringUtils.hasLength(phoneNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_COLLEGE.PHONE_NUMBER.like(SQLQueryUtils.likeAllParam(phoneNumber))
                } else {
                    a!!.and(GRADUATION_PRACTICE_COLLEGE.PHONE_NUMBER.like(SQLQueryUtils.likeAllParam(phoneNumber)))
                }
            }

            if (StringUtils.hasLength(headmaster)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_COLLEGE.HEADMASTER.like(SQLQueryUtils.likeAllParam(headmaster))
                } else {
                    a!!.and(GRADUATION_PRACTICE_COLLEGE.HEADMASTER.like(SQLQueryUtils.likeAllParam(headmaster)))
                }
            }

            if (StringUtils.hasLength(schoolGuidanceTeacher)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_COLLEGE.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher))
                } else {
                    a!!.and(GRADUATION_PRACTICE_COLLEGE.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher)))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeCollege>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.STUDENT_NAME.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.STUDENT_NAME.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("student_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.STUDENT_NUMBER.desc()
                }
            }

            if ("college_class".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.COLLEGE_CLASS.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.COLLEGE_CLASS.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("student_sex".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.STUDENT_SEX.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.STUDENT_SEX.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("phone_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.PHONE_NUMBER.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.PHONE_NUMBER.desc()
                }
            }

            if ("qq_mailbox".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.QQ_MAILBOX.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.QQ_MAILBOX.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("parental_contact".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.PARENTAL_CONTACT.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.PARENTAL_CONTACT.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("headmaster".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.HEADMASTER.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.HEADMASTER.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("headmaster_contact".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.HEADMASTER_CONTACT.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.HEADMASTER_CONTACT.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("graduation_practice_college_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_NAME.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_NAME.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                }
            }

            if ("graduation_practice_college_address".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ADDRESS.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ADDRESS.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("graduation_practice_college_contacts".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_CONTACTS.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_CONTACTS.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("graduation_practice_college_tel".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_TEL.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_TEL.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("school_guidance_teacher".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.SCHOOL_GUIDANCE_TEACHER.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.SCHOOL_GUIDANCE_TEACHER.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("school_guidance_teacher_tel".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.SCHOOL_GUIDANCE_TEACHER_TEL.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.SCHOOL_GUIDANCE_TEACHER_TEL.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("start_time".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.START_TIME.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.START_TIME.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("end_time".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.END_TIME.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.END_TIME.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("commitment_book".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.COMMITMENT_BOOK.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.COMMITMENT_BOOK.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("safety_responsibility_book".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.SAFETY_RESPONSIBILITY_BOOK.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.SAFETY_RESPONSIBILITY_BOOK.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("practice_agreement".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.PRACTICE_AGREEMENT.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.PRACTICE_AGREEMENT.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("internship_application".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_APPLICATION.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_APPLICATION.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("practice_receiving".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.PRACTICE_RECEIVING.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.PRACTICE_RECEIVING.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("security_education_agreement".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.SECURITY_EDUCATION_AGREEMENT.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.SECURITY_EDUCATION_AGREEMENT.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

            if ("parental_consent".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.PARENTAL_CONSENT.asc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COLLEGE.PARENTAL_CONSENT.desc()
                    sortField[1] = GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID.desc()
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}