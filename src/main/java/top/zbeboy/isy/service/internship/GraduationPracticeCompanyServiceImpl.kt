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
import top.zbeboy.isy.domain.tables.daos.GraduationPracticeCompanyDao
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCompany
import top.zbeboy.isy.domain.tables.records.GraduationPracticeCompanyRecord
import top.zbeboy.isy.domain.tables.records.InternshipApplyRecord
import top.zbeboy.isy.domain.tables.records.InternshipChangeHistoryRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.internship.apply.GraduationPracticeCompanyVo
import java.sql.Timestamp
import java.time.Clock
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-27 .
 **/
@Service("graduationPracticeCompanyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationPracticeCompanyServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<GraduationPracticeCompany>(), GraduationPracticeCompanyService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationPracticeCompanyDao: GraduationPracticeCompanyDao


    override fun findById(id: String): GraduationPracticeCompany {
        return graduationPracticeCompanyDao.findById(id)
    }

    override fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Optional<Record> {
        return create.select()
                .from(GRADUATION_PRACTICE_COMPANY)
                .where(GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_COMPANY.STUDENT_ID.eq(studentId)))
                .fetchOptional()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationPracticeCompany: GraduationPracticeCompany) {
        graduationPracticeCompanyDao.insert(graduationPracticeCompany)
    }

    override fun saveWithTransaction(graduationPracticeCompanyVo: GraduationPracticeCompanyVo) {
        create.transaction { configuration ->
            val now = Timestamp(Clock.systemDefaultZone().millis())
            val state = 0
            DSL.using(configuration)
                    .insertInto<InternshipApplyRecord>(INTERNSHIP_APPLY)
                    .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_ID, UUIDUtils.getUUID())
                    .set(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID, graduationPracticeCompanyVo.internshipReleaseId)
                    .set(INTERNSHIP_APPLY.STUDENT_ID, graduationPracticeCompanyVo.studentId)
                    .set(INTERNSHIP_APPLY.APPLY_TIME, now)
                    .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE, state)
                    .execute()

            val headmasterArr = graduationPracticeCompanyVo.headmaster!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (headmasterArr.size >= 2) {
                graduationPracticeCompanyVo.headmaster = headmasterArr[0]
                graduationPracticeCompanyVo.headmasterContact = headmasterArr[1]
            }
            val schoolGuidanceTeacherArr = graduationPracticeCompanyVo.schoolGuidanceTeacher!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (schoolGuidanceTeacherArr.size >= 2) {
                graduationPracticeCompanyVo.schoolGuidanceTeacher = schoolGuidanceTeacherArr[0]
                graduationPracticeCompanyVo.schoolGuidanceTeacherTel = schoolGuidanceTeacherArr[1]
            }

            DSL.using(configuration)
                    .insertInto<GraduationPracticeCompanyRecord>(GRADUATION_PRACTICE_COMPANY)
                    .set(GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID, UUIDUtils.getUUID())
                    .set(GRADUATION_PRACTICE_COMPANY.STUDENT_ID, graduationPracticeCompanyVo.studentId)
                    .set(GRADUATION_PRACTICE_COMPANY.STUDENT_USERNAME, graduationPracticeCompanyVo.studentUsername)
                    .set(GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID, graduationPracticeCompanyVo.internshipReleaseId)
                    .set(GRADUATION_PRACTICE_COMPANY.STUDENT_NAME, graduationPracticeCompanyVo.studentName)
                    .set(GRADUATION_PRACTICE_COMPANY.COLLEGE_CLASS, graduationPracticeCompanyVo.collegeClass)
                    .set(GRADUATION_PRACTICE_COMPANY.STUDENT_SEX, graduationPracticeCompanyVo.studentSex)
                    .set(GRADUATION_PRACTICE_COMPANY.STUDENT_NUMBER, graduationPracticeCompanyVo.studentNumber)
                    .set(GRADUATION_PRACTICE_COMPANY.PHONE_NUMBER, graduationPracticeCompanyVo.phoneNumber)
                    .set(GRADUATION_PRACTICE_COMPANY.QQ_MAILBOX, graduationPracticeCompanyVo.qqMailbox)
                    .set(GRADUATION_PRACTICE_COMPANY.PARENTAL_CONTACT, graduationPracticeCompanyVo.parentalContact)
                    .set(GRADUATION_PRACTICE_COMPANY.HEADMASTER, graduationPracticeCompanyVo.headmaster)
                    .set(GRADUATION_PRACTICE_COMPANY.HEADMASTER_CONTACT, graduationPracticeCompanyVo.headmasterContact)
                    .set(GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_NAME, graduationPracticeCompanyVo.graduationPracticeCompanyName)
                    .set(GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ADDRESS, graduationPracticeCompanyVo.graduationPracticeCompanyAddress)
                    .set(GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_CONTACTS, graduationPracticeCompanyVo.graduationPracticeCompanyContacts)
                    .set(GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_TEL, graduationPracticeCompanyVo.graduationPracticeCompanyTel)
                    .set(GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER, graduationPracticeCompanyVo.schoolGuidanceTeacher)
                    .set(GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER_TEL, graduationPracticeCompanyVo.schoolGuidanceTeacherTel)
                    .set(GRADUATION_PRACTICE_COMPANY.START_TIME, DateTimeUtils.formatDate(graduationPracticeCompanyVo.startTime!!))
                    .set(GRADUATION_PRACTICE_COMPANY.END_TIME, DateTimeUtils.formatDate(graduationPracticeCompanyVo.endTime!!))
                    .execute()

            DSL.using(configuration)
                    .insertInto<InternshipChangeHistoryRecord>(INTERNSHIP_CHANGE_HISTORY)
                    .set(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_CHANGE_HISTORY_ID, UUIDUtils.getUUID())
                    .set(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_RELEASE_ID, graduationPracticeCompanyVo.internshipReleaseId)
                    .set(INTERNSHIP_CHANGE_HISTORY.STUDENT_ID, graduationPracticeCompanyVo.studentId)
                    .set(INTERNSHIP_CHANGE_HISTORY.STATE, state)
                    .set(INTERNSHIP_CHANGE_HISTORY.APPLY_TIME, now)
                    .execute()
        }
    }

    override fun update(graduationPracticeCompany: GraduationPracticeCompany) {
        graduationPracticeCompanyDao.update(graduationPracticeCompany)
    }

    override fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int) {
        create.deleteFrom<GraduationPracticeCompanyRecord>(GRADUATION_PRACTICE_COMPANY)
                .where(GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_COMPANY.STUDENT_ID.eq(studentId)))
                .execute()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationPracticeCompany>, graduationPracticeCompany: GraduationPracticeCompany): Result<Record> {
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, GRADUATION_PRACTICE_COMPANY, GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCompany.internshipReleaseId))
    }

    override fun countAll(graduationPracticeCompany: GraduationPracticeCompany): Int {
        return statisticsAllWithCondition(create, GRADUATION_PRACTICE_COMPANY, GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCompany.internshipReleaseId))
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeCompany>, graduationPracticeCompany: GraduationPracticeCompany): Int {
        return statisticsWithCondition(dataTablesUtils, create, GRADUATION_PRACTICE_COMPANY, GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCompany.internshipReleaseId))
    }

    override fun exportData(dataTablesUtils: DataTablesUtils<GraduationPracticeCompany>, graduationPracticeCompany: GraduationPracticeCompany): Result<Record> {
        return dataPagingQueryAllWithConditionNoPage(dataTablesUtils, create, GRADUATION_PRACTICE_COMPANY, GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCompany.internshipReleaseId))
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeCompany>): Condition? {
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
                a = GRADUATION_PRACTICE_COMPANY.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName))
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_COMPANY.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(GRADUATION_PRACTICE_COMPANY.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(collegeClass)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_COMPANY.COLLEGE_CLASS.like(SQLQueryUtils.likeAllParam(collegeClass))
                } else {
                    a!!.and(GRADUATION_PRACTICE_COMPANY.COLLEGE_CLASS.like(SQLQueryUtils.likeAllParam(collegeClass)))
                }
            }

            if (StringUtils.hasLength(phoneNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_COMPANY.PHONE_NUMBER.like(SQLQueryUtils.likeAllParam(phoneNumber))
                } else {
                    a!!.and(GRADUATION_PRACTICE_COMPANY.PHONE_NUMBER.like(SQLQueryUtils.likeAllParam(phoneNumber)))
                }
            }

            if (StringUtils.hasLength(headmaster)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_COMPANY.HEADMASTER.like(SQLQueryUtils.likeAllParam(headmaster))
                } else {
                    a!!.and(GRADUATION_PRACTICE_COMPANY.HEADMASTER.like(SQLQueryUtils.likeAllParam(headmaster)))
                }
            }

            if (StringUtils.hasLength(schoolGuidanceTeacher)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher))
                } else {
                    a!!.and(GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher)))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeCompany>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.STUDENT_NAME.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.STUDENT_NAME.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("student_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.STUDENT_NUMBER.desc()
                }
            }

            if ("college_class".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.COLLEGE_CLASS.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.COLLEGE_CLASS.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("student_sex".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.STUDENT_SEX.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.STUDENT_SEX.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("phone_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.PHONE_NUMBER.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.PHONE_NUMBER.desc()
                }
            }

            if ("qq_mailbox".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.QQ_MAILBOX.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.QQ_MAILBOX.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("parental_contact".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.PARENTAL_CONTACT.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.PARENTAL_CONTACT.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("headmaster".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.HEADMASTER.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.HEADMASTER.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("headmaster_contact".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.HEADMASTER_CONTACT.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.HEADMASTER_CONTACT.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("graduation_practice_company_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_NAME.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_NAME.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("graduation_practice_company_address".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ADDRESS.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ADDRESS.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("graduation_practice_company_contacts".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_CONTACTS.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_CONTACTS.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("graduation_practice_company_tel".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_TEL.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_TEL.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("school_guidance_teacher".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("school_guidance_teacher_tel".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER_TEL.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER_TEL.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("start_time".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.START_TIME.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.START_TIME.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("end_time".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.END_TIME.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.END_TIME.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("commitment_book".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.COMMITMENT_BOOK.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.COMMITMENT_BOOK.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("safety_responsibility_book".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.SAFETY_RESPONSIBILITY_BOOK.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.SAFETY_RESPONSIBILITY_BOOK.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("practice_agreement".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.PRACTICE_AGREEMENT.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.PRACTICE_AGREEMENT.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("internship_application".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.INTERNSHIP_APPLICATION.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.INTERNSHIP_APPLICATION.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("practice_receiving".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.PRACTICE_RECEIVING.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.PRACTICE_RECEIVING.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("security_education_agreement".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.SECURITY_EDUCATION_AGREEMENT.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.SECURITY_EDUCATION_AGREEMENT.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

            if ("parental_consent".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.PARENTAL_CONSENT.asc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_COMPANY.PARENTAL_CONSENT.desc()
                    sortField[1] = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID.desc()
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}