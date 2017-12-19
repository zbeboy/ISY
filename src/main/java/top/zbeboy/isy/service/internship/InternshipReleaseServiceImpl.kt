package top.zbeboy.isy.service.internship

import com.alibaba.fastjson.JSON
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.InternshipReleaseDao
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease
import top.zbeboy.isy.domain.tables.pojos.Science
import top.zbeboy.isy.domain.tables.records.InternshipReleaseRecord
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.sql.Timestamp
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-19 .
 **/
@Service("internshipReleaseService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipReleaseServiceImpl @Autowired constructor(dslContext: DSLContext) : InternshipReleaseService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var internshipReleaseDao: InternshipReleaseDao

    @Resource
    open lateinit var internshipReleaseScienceService: InternshipReleaseScienceService


    override fun findByEndTime(endTime: Timestamp): Result<InternshipReleaseRecord> {
        return create.selectFrom<InternshipReleaseRecord>(INTERNSHIP_RELEASE)
                .where(INTERNSHIP_RELEASE.END_TIME.le(endTime))
                .fetch()
    }

    override fun findById(internshipReleaseId: String): InternshipRelease {
        return internshipReleaseDao.findById(internshipReleaseId)
    }

    override fun findByIdRelation(internshipReleaseId: String): Optional<Record> {
        return create.select()
                .from(INTERNSHIP_RELEASE)
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID))
                .join(DEPARTMENT)
                .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetchOptional()
    }

    override fun findByReleaseTitle(releaseTitle: String): List<InternshipRelease> {
        return internshipReleaseDao.fetchByInternshipTitle(releaseTitle)
    }

    override fun findByReleaseTitleNeInternshipReleaseId(releaseTitle: String, internshipReleaseId: String): Result<InternshipReleaseRecord> {
        return create.selectFrom<InternshipReleaseRecord>(INTERNSHIP_RELEASE)
                .where(INTERNSHIP_RELEASE.INTERNSHIP_TITLE.eq(releaseTitle).and(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID.ne(internshipReleaseId)))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(internshipRelease: InternshipRelease) {
        internshipReleaseDao.insert(internshipRelease)
    }

    override fun update(internshipRelease: InternshipRelease) {
        internshipReleaseDao.update(internshipRelease)
    }

    override fun findAllByPage(paginationUtils: PaginationUtils, internshipReleaseBean: InternshipReleaseBean): Result<Record> {
        val pageNum = paginationUtils.getPageNum()
        val pageSize = paginationUtils.getPageSize()
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, internshipReleaseBean)
        return create.select()
                .from(INTERNSHIP_RELEASE)
                .join(USERS)
                .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                .join(DEPARTMENT)
                .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                .where(a)
                .orderBy(INTERNSHIP_RELEASE.RELEASE_TIME.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch()

    }

    override fun dealData(paginationUtils: PaginationUtils, records: Result<Record>, internshipReleaseBean: InternshipReleaseBean): List<InternshipReleaseBean> {
        var internshipReleaseBeens: List<InternshipReleaseBean> = ArrayList()
        if (records.isNotEmpty) {
            internshipReleaseBeens = records.into(InternshipReleaseBean::class.java)
            val format = "yyyy-MM-dd HH:mm:ss"
            internshipReleaseBeens.forEach { i ->
                i.teacherDistributionStartTimeStr = DateTimeUtils.timestampToString(i.teacherDistributionStartTime, format)
                i.teacherDistributionEndTimeStr = DateTimeUtils.timestampToString(i.teacherDistributionEndTime, format)
                i.startTimeStr = DateTimeUtils.timestampToString(i.startTime, format)
                i.endTimeStr = DateTimeUtils.timestampToString(i.endTime, format)
                i.releaseTimeStr = DateTimeUtils.timestampToString(i.releaseTime, format)
                val records1 = internshipReleaseScienceService.findByInternshipReleaseIdRelation(i.internshipReleaseId)
                i.sciences = records1.into(Science::class.java)
            }
            paginationUtils.setTotalDatas(countByCondition(paginationUtils, internshipReleaseBean))
        }
        return internshipReleaseBeens
    }

    override fun countByCondition(paginationUtils: PaginationUtils, internshipReleaseBean: InternshipReleaseBean): Int {
        val count: Record1<Int>
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, internshipReleaseBean)
        if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(INTERNSHIP_RELEASE)
            count = selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_RELEASE)
                    .join(USERS)
                    .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                    .join(DEPARTMENT)
                    .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(INTERNSHIP_TYPE)
                    .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
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
     * @param a                     搜索条件
     * @param internshipReleaseBean 额外参数
     * @return 条件
     */
    private fun otherCondition(a: Condition?, internshipReleaseBean: InternshipReleaseBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(internshipReleaseBean)) {
            if (!ObjectUtils.isEmpty(internshipReleaseBean.departmentId) && internshipReleaseBean.departmentId > 0) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(internshipReleaseBean.departmentId))
                } else {
                    INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(internshipReleaseBean.departmentId)
                }
            }

            if (!ObjectUtils.isEmpty(internshipReleaseBean.collegeId) && internshipReleaseBean.collegeId!! > 0) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(COLLEGE.COLLEGE_ID.eq(internshipReleaseBean.collegeId))
                } else {
                    COLLEGE.COLLEGE_ID.eq(internshipReleaseBean.collegeId)
                }
            }

            if (!ObjectUtils.isEmpty(internshipReleaseBean.internshipReleaseIsDel)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_IS_DEL.eq(internshipReleaseBean.internshipReleaseIsDel))
                } else {
                    INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_IS_DEL.eq(internshipReleaseBean.internshipReleaseIsDel)
                }
            }

            if (!ObjectUtils.isEmpty(internshipReleaseBean.allowGrade)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(INTERNSHIP_RELEASE.ALLOW_GRADE.eq(internshipReleaseBean.allowGrade))
                } else {
                    INTERNSHIP_RELEASE.ALLOW_GRADE.eq(internshipReleaseBean.allowGrade)
                }
            }
        }
        return tempCondition
    }
}