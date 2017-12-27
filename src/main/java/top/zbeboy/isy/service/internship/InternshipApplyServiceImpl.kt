package top.zbeboy.isy.service.internship

import com.alibaba.fastjson.JSON
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.InternshipApplyDao
import top.zbeboy.isy.domain.tables.pojos.InternshipApply
import top.zbeboy.isy.domain.tables.pojos.Science
import top.zbeboy.isy.domain.tables.records.InternshipApplyRecord
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.sql.Timestamp
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-27 .
 **/
@Service("internshipApplyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipApplyServiceImpl @Autowired constructor(dslContext: DSLContext) : InternshipApplyService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var internshipApplyDao: InternshipApplyDao

    @Resource
    open lateinit var internshipReleaseScienceService: InternshipReleaseScienceService

    @Resource
    open lateinit var internshipTypeService: InternshipTypeService

    @Resource
    open lateinit var internshipCollegeService: InternshipCollegeService

    @Resource
    open lateinit var internshipCompanyService: InternshipCompanyService

    @Resource
    open lateinit var graduationPracticeCollegeService: GraduationPracticeCollegeService

    @Resource
    open lateinit var graduationPracticeCompanyService: GraduationPracticeCompanyService

    @Resource
    open lateinit var graduationPracticeUnifyService: GraduationPracticeUnifyService


    override fun findById(id: String): InternshipApply {
        return internshipApplyDao.findById(id)
    }

    override fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Optional<Record> {
        return create.select()
                .from(INTERNSHIP_APPLY)
                .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_APPLY.STUDENT_ID.eq(studentId)))
                .fetchOptional()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(internshipApply: InternshipApply) {
        internshipApplyDao.insert(internshipApply)
    }

    override fun update(internshipApply: InternshipApply) {
        internshipApplyDao.update(internshipApply)
    }

    override fun updateStateWithInternshipReleaseIdAndState(internshipReleaseId: String, changeState: Int, updateState: Int) {
        create.update<InternshipApplyRecord>(INTERNSHIP_APPLY)
                .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE, updateState)
                .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(changeState)))
                .execute()
    }

    override fun updateStateByChangeFillEndTime(changeFillEndTime: Timestamp, changeState: Int, updateState: Int) {
        create.update<InternshipApplyRecord>(INTERNSHIP_APPLY)
                .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE, updateState)
                .where(INTERNSHIP_APPLY.CHANGE_FILL_END_TIME.le(changeFillEndTime).and(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(changeState)))
                .execute()
    }

    override fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int) {
        create.deleteFrom<InternshipApplyRecord>(INTERNSHIP_APPLY)
                .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_APPLY.STUDENT_ID.eq(studentId)))
                .execute()
    }

    override fun deleteInternshipApplyRecord(internshipTypeId: Int, internshipReleaseId: String, studentId: Int) {
        val internshipType = internshipTypeService.findByInternshipTypeId(internshipTypeId)
        when (internshipType.internshipTypeName) {
            Workbook.INTERNSHIP_COLLEGE_TYPE -> internshipCollegeService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            Workbook.INTERNSHIP_COMPANY_TYPE -> internshipCompanyService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            Workbook.GRADUATION_PRACTICE_COLLEGE_TYPE -> graduationPracticeCollegeService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            Workbook.GRADUATION_PRACTICE_UNIFY_TYPE -> graduationPracticeUnifyService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
            Workbook.GRADUATION_PRACTICE_COMPANY_TYPE -> graduationPracticeCompanyService.deleteByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId)
        }
    }

    override fun findAllByPage(paginationUtils: PaginationUtils, internshipApplyBean: InternshipApplyBean): Result<Record> {
        val pageNum = paginationUtils.getPageNum()
        val pageSize = paginationUtils.getPageSize()
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, internshipApplyBean)
        return create.select()
                .from(INTERNSHIP_APPLY)
                .join(INTERNSHIP_RELEASE)
                .on(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                .join(STUDENT)
                .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                .join(DEPARTMENT)
                .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                .leftJoin(FILES)
                .on(INTERNSHIP_APPLY.INTERNSHIP_FILE_ID.eq(FILES.FILE_ID))
                .where(a)
                .orderBy(INTERNSHIP_APPLY.APPLY_TIME.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch()
    }

    override fun dealData(paginationUtils: PaginationUtils, records: Result<Record>, internshipApplyBean: InternshipApplyBean): List<InternshipApplyBean> {
        var internshipApplyBeens: List<InternshipApplyBean> = ArrayList()
        if (records.isNotEmpty) {
            internshipApplyBeens = records.into(InternshipApplyBean::class.java)
            val format = "yyyy-MM-dd HH:mm:ss"
            internshipApplyBeens.forEach { i ->
                i.teacherDistributionStartTimeStr = DateTimeUtils.timestampToString(i.teacherDistributionStartTime!!, format)
                i.teacherDistributionEndTimeStr = DateTimeUtils.timestampToString(i.teacherDistributionEndTime!!, format)
                i.startTimeStr = DateTimeUtils.timestampToString(i.startTime!!, format)
                i.endTimeStr = DateTimeUtils.timestampToString(i.endTime!!, format)
                i.releaseTimeStr = DateTimeUtils.timestampToString(i.releaseTime!!, format)
                val records1 = internshipReleaseScienceService.findByInternshipReleaseIdRelation(i.internshipReleaseId)
                i.sciences = records1.into(Science::class.java)
            }
            paginationUtils.setTotalDatas(countByCondition(paginationUtils, internshipApplyBean))
        }
        return internshipApplyBeens
    }

    override fun countByCondition(paginationUtils: PaginationUtils, internshipApplyBean: InternshipApplyBean): Int {
        val count: Record1<Int>
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, internshipApplyBean)
        if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(INTERNSHIP_APPLY)
            count = selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_APPLY)
                    .join(INTERNSHIP_RELEASE)
                    .on(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                    .join(STUDENT)
                    .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(INTERNSHIP_TYPE)
                    .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                    .join(DEPARTMENT)
                    .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .join(SCHOOL)
                    .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                    .where(a)
            count = selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    /**
     * 搜索条件
     *
     * @param paginationUtils 分页工具
     * @return 条件
     */
    fun searchCondition(paginationUtils: PaginationUtils): Condition? {
        var a: Condition? = null
        val search = JSON.parseObject(paginationUtils.getSearchParams())
        if (!ObjectUtils.isEmpty(search)) {
            val internshipTitle = StringUtils.trimWhitespace(search.getString("internshipTitle"))
            if (StringUtils.hasLength(internshipTitle)) {
                a = INTERNSHIP_RELEASE.INTERNSHIP_TITLE.like(SQLQueryUtils.likeAllParam(internshipTitle))
            }
        }
        return a
    }

    /**
     * 其它条件参数
     *
     * @param a                   搜索条件
     * @param internshipApplyBean 额外参数
     * @return 条件
     */
    private fun otherCondition(a: Condition?, internshipApplyBean: InternshipApplyBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(internshipApplyBean)) {
            if (!ObjectUtils.isEmpty(internshipApplyBean.studentId) && internshipApplyBean.studentId > 0) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(INTERNSHIP_APPLY.STUDENT_ID.eq(internshipApplyBean.studentId))
                } else {
                    INTERNSHIP_APPLY.STUDENT_ID.eq(internshipApplyBean.studentId)
                }
            }

            if (StringUtils.hasLength(internshipApplyBean.internshipReleaseId)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipApplyBean.internshipReleaseId))
                } else {
                    INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipApplyBean.internshipReleaseId)
                }
            }

            if (!ObjectUtils.isEmpty(internshipApplyBean.internshipReleaseIsDel)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_IS_DEL.eq(internshipApplyBean.internshipReleaseIsDel))
                } else {
                    INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_IS_DEL.eq(internshipApplyBean.internshipReleaseIsDel)
                }
            }

            if (!ObjectUtils.isEmpty(internshipApplyBean.internshipApplyState)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(internshipApplyBean.internshipApplyState))
                } else {
                    INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(internshipApplyBean.internshipApplyState)
                }
            }
        }
        return tempCondition
    }

}