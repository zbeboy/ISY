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
import top.zbeboy.isy.domain.tables.daos.GraduationPracticeUnifyDao
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeUnify
import top.zbeboy.isy.domain.tables.records.GraduationPracticeUnifyRecord
import top.zbeboy.isy.domain.tables.records.InternshipApplyRecord
import top.zbeboy.isy.domain.tables.records.InternshipChangeHistoryRecord
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.internship.apply.GraduationPracticeUnifyVo
import java.sql.Timestamp
import java.time.Clock
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-27 .
 **/
@Service("graduationPracticeUnifyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationPracticeUnifyServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<GraduationPracticeUnify>(), GraduationPracticeUnifyService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationPracticeUnifyDao: GraduationPracticeUnifyDao


    override fun findById(id: String): GraduationPracticeUnify {
        return graduationPracticeUnifyDao.findById(id)
    }

    override fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Optional<Record> {
        return create.select()
                .from(GRADUATION_PRACTICE_UNIFY)
                .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(studentId)))
                .fetchOptional()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationPracticeUnify: GraduationPracticeUnify) {
        graduationPracticeUnifyDao.insert(graduationPracticeUnify)
    }

    override fun saveWithTransaction(graduationPracticeUnifyVo: GraduationPracticeUnifyVo) {
        create.transaction { configuration ->
            val now = Timestamp(Clock.systemDefaultZone().millis())
            val state = 0
            DSL.using(configuration)
                    .insertInto<InternshipApplyRecord>(INTERNSHIP_APPLY)
                    .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_ID, UUIDUtils.getUUID())
                    .set(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID, graduationPracticeUnifyVo.internshipReleaseId)
                    .set(INTERNSHIP_APPLY.STUDENT_ID, graduationPracticeUnifyVo.studentId)
                    .set(INTERNSHIP_APPLY.APPLY_TIME, now)
                    .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE, state)
                    .execute()

            val headmasterArr = graduationPracticeUnifyVo.headmaster!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (headmasterArr.size >= 2) {
                graduationPracticeUnifyVo.headmaster = headmasterArr[0]
                graduationPracticeUnifyVo.headmasterContact = headmasterArr[1]
            }
            val schoolGuidanceTeacherArr = graduationPracticeUnifyVo.schoolGuidanceTeacher!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (schoolGuidanceTeacherArr.size >= 2) {
                graduationPracticeUnifyVo.schoolGuidanceTeacher = schoolGuidanceTeacherArr[0]
                graduationPracticeUnifyVo.schoolGuidanceTeacherTel = schoolGuidanceTeacherArr[1]
            }

            DSL.using(configuration)
                    .insertInto<GraduationPracticeUnifyRecord>(GRADUATION_PRACTICE_UNIFY)
                    .set(GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID, UUIDUtils.getUUID())
                    .set(GRADUATION_PRACTICE_UNIFY.STUDENT_ID, graduationPracticeUnifyVo.studentId)
                    .set(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID, graduationPracticeUnifyVo.internshipReleaseId)
                    .set(GRADUATION_PRACTICE_UNIFY.STUDENT_NAME, graduationPracticeUnifyVo.studentName)
                    .set(GRADUATION_PRACTICE_UNIFY.COLLEGE_CLASS, graduationPracticeUnifyVo.collegeClass)
                    .set(GRADUATION_PRACTICE_UNIFY.STUDENT_SEX, graduationPracticeUnifyVo.studentSex)
                    .set(GRADUATION_PRACTICE_UNIFY.STUDENT_NUMBER, graduationPracticeUnifyVo.studentNumber)
                    .set(GRADUATION_PRACTICE_UNIFY.PHONE_NUMBER, graduationPracticeUnifyVo.phoneNumber)
                    .set(GRADUATION_PRACTICE_UNIFY.QQ_MAILBOX, graduationPracticeUnifyVo.qqMailbox)
                    .set(GRADUATION_PRACTICE_UNIFY.PARENTAL_CONTACT, graduationPracticeUnifyVo.parentalContact)
                    .set(GRADUATION_PRACTICE_UNIFY.HEADMASTER, graduationPracticeUnifyVo.headmaster)
                    .set(GRADUATION_PRACTICE_UNIFY.HEADMASTER_CONTACT, graduationPracticeUnifyVo.headmasterContact)
                    .set(GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_NAME, graduationPracticeUnifyVo.graduationPracticeUnifyName)
                    .set(GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ADDRESS, graduationPracticeUnifyVo.graduationPracticeUnifyAddress)
                    .set(GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_CONTACTS, graduationPracticeUnifyVo.graduationPracticeUnifyContacts)
                    .set(GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_TEL, graduationPracticeUnifyVo.graduationPracticeUnifyTel)
                    .set(GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER, graduationPracticeUnifyVo.schoolGuidanceTeacher)
                    .set(GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER_TEL, graduationPracticeUnifyVo.schoolGuidanceTeacherTel)
                    .set(GRADUATION_PRACTICE_UNIFY.START_TIME, DateTimeUtils.formatDate(graduationPracticeUnifyVo.startTime!!))
                    .set(GRADUATION_PRACTICE_UNIFY.END_TIME, DateTimeUtils.formatDate(graduationPracticeUnifyVo.endTime!!))
                    .execute()

            DSL.using(configuration)
                    .insertInto<InternshipChangeHistoryRecord>(INTERNSHIP_CHANGE_HISTORY)
                    .set(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_CHANGE_HISTORY_ID, UUIDUtils.getUUID())
                    .set(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_RELEASE_ID, graduationPracticeUnifyVo.internshipReleaseId)
                    .set(INTERNSHIP_CHANGE_HISTORY.STUDENT_ID, graduationPracticeUnifyVo.studentId)
                    .set(INTERNSHIP_CHANGE_HISTORY.STATE, state)
                    .set(INTERNSHIP_CHANGE_HISTORY.APPLY_TIME, now)
                    .execute()
        }
    }

    override fun update(graduationPracticeUnify: GraduationPracticeUnify) {
        graduationPracticeUnifyDao.update(graduationPracticeUnify)
    }

    override fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int) {
        create.deleteFrom<GraduationPracticeUnifyRecord>(GRADUATION_PRACTICE_UNIFY)
                .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(studentId)))
                .execute()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationPracticeUnify>, graduationPracticeUnify: GraduationPracticeUnify): Result<Record> {
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, GRADUATION_PRACTICE_UNIFY, GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnify.internshipReleaseId))
    }

    override fun countAll(graduationPracticeUnify: GraduationPracticeUnify): Int {
        return statisticsAllWithCondition(create, GRADUATION_PRACTICE_UNIFY, GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnify.internshipReleaseId))
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeUnify>, graduationPracticeUnify: GraduationPracticeUnify): Int {
        return statisticsWithCondition(dataTablesUtils, create, GRADUATION_PRACTICE_UNIFY, GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnify.internshipReleaseId))
    }

    override fun exportData(dataTablesUtils: DataTablesUtils<GraduationPracticeUnify>, graduationPracticeUnify: GraduationPracticeUnify): Result<Record> {
        return dataPagingQueryAllWithConditionNoPage(dataTablesUtils, create, GRADUATION_PRACTICE_UNIFY, GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnify.internshipReleaseId))
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeUnify>): Condition? {
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
                a = GRADUATION_PRACTICE_UNIFY.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName))
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_UNIFY.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(GRADUATION_PRACTICE_UNIFY.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(collegeClass)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_UNIFY.COLLEGE_CLASS.like(SQLQueryUtils.likeAllParam(collegeClass))
                } else {
                    a!!.and(GRADUATION_PRACTICE_UNIFY.COLLEGE_CLASS.like(SQLQueryUtils.likeAllParam(collegeClass)))
                }
            }

            if (StringUtils.hasLength(phoneNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_UNIFY.PHONE_NUMBER.like(SQLQueryUtils.likeAllParam(phoneNumber))
                } else {
                    a!!.and(GRADUATION_PRACTICE_UNIFY.PHONE_NUMBER.like(SQLQueryUtils.likeAllParam(phoneNumber)))
                }
            }

            if (StringUtils.hasLength(headmaster)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_UNIFY.HEADMASTER.like(SQLQueryUtils.likeAllParam(headmaster))
                } else {
                    a!!.and(GRADUATION_PRACTICE_UNIFY.HEADMASTER.like(SQLQueryUtils.likeAllParam(headmaster)))
                }
            }

            if (StringUtils.hasLength(schoolGuidanceTeacher)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher))
                } else {
                    a!!.and(GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher)))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeUnify>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.STUDENT_NAME.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.STUDENT_NAME.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("student_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.STUDENT_NUMBER.desc()
                }
            }

            if ("college_class".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.COLLEGE_CLASS.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.COLLEGE_CLASS.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("student_sex".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.STUDENT_SEX.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.STUDENT_SEX.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("phone_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.PHONE_NUMBER.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.PHONE_NUMBER.desc()
                }
            }

            if ("qq_mailbox".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.QQ_MAILBOX.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.QQ_MAILBOX.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("parental_contact".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.PARENTAL_CONTACT.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.PARENTAL_CONTACT.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("headmaster".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.HEADMASTER.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.HEADMASTER.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("headmaster_contact".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.HEADMASTER_CONTACT.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.HEADMASTER_CONTACT.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("graduation_practice_unify_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_NAME.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_NAME.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("graduation_practice_unify_address".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ADDRESS.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ADDRESS.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("graduation_practice_unify_contacts".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_CONTACTS.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_CONTACTS.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("graduation_practice_unify_tel".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_TEL.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_TEL.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("school_guidance_teacher".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("school_guidance_teacher_tel".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER_TEL.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER_TEL.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("start_time".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.START_TIME.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.START_TIME.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("end_time".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.END_TIME.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.END_TIME.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("commitment_book".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.COMMITMENT_BOOK.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.COMMITMENT_BOOK.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("safety_responsibility_book".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.SAFETY_RESPONSIBILITY_BOOK.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.SAFETY_RESPONSIBILITY_BOOK.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("practice_agreement".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.PRACTICE_AGREEMENT.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.PRACTICE_AGREEMENT.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("internship_application".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.INTERNSHIP_APPLICATION.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.INTERNSHIP_APPLICATION.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("practice_receiving".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.PRACTICE_RECEIVING.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.PRACTICE_RECEIVING.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("security_education_agreement".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.SECURITY_EDUCATION_AGREEMENT.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.SECURITY_EDUCATION_AGREEMENT.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

            if ("parental_consent".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.PARENTAL_CONSENT.asc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.asc()
                } else {
                    sortField[0] = GRADUATION_PRACTICE_UNIFY.PARENTAL_CONSENT.desc()
                    sortField[1] = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID.desc()
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }
}