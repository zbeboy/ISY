package top.zbeboy.isy.service.graduate.design

import com.alibaba.fastjson.JSON
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.GraduationDesignReleaseDao
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease
import top.zbeboy.isy.domain.tables.records.GraduationDesignReleaseRecord
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-14 .
 **/
@Service("graduationDesignReleaseService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignReleaseServiceImpl @Autowired constructor(dslContext: DSLContext) : GraduationDesignReleaseService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationDesignReleaseDao: GraduationDesignReleaseDao


    override fun findById(id: String): GraduationDesignRelease {
        return graduationDesignReleaseDao.findById(id)
    }

    override fun findByIdRelation(id: String): Optional<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_RELEASE)
                .join(SCIENCE)
                .on(GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                .where(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID.eq(id))
                .fetchOptional()
    }

    override fun findByGraduationDesignTitle(graduationDesignTitle: String): List<GraduationDesignRelease> {
        return graduationDesignReleaseDao.fetchByGraduationDesignTitle(graduationDesignTitle)
    }

    override fun findByGraduationDesignTitleNeGraduationDesignReleaseId(graduationDesignTitle: String, graduationDesignReleaseId: String): Result<GraduationDesignReleaseRecord> {
        return create.selectFrom(GRADUATION_DESIGN_RELEASE)
                .where(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_TITLE.eq(graduationDesignTitle).and(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID.ne(graduationDesignReleaseId)))
                .fetch()
    }

    override fun findAllByPage(paginationUtils: PaginationUtils, graduationDesignReleaseBean: GraduationDesignReleaseBean): Result<Record> {
        val pageNum = paginationUtils.getPageNum()
        val pageSize = paginationUtils.getPageSize()
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, graduationDesignReleaseBean)
        return create.select()
                .from(GRADUATION_DESIGN_RELEASE)
                .join(USERS)
                .on(GRADUATION_DESIGN_RELEASE.USERNAME.eq(USERS.USERNAME))
                .join(SCIENCE)
                .on(GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                .where(a)
                .orderBy(GRADUATION_DESIGN_RELEASE.RELEASE_TIME.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch()
    }

    override fun dealData(paginationUtils: PaginationUtils, records: Result<Record>, graduationDesignReleaseBean: GraduationDesignReleaseBean): List<GraduationDesignReleaseBean> {
        var graduationDesignReleaseBeans: List<GraduationDesignReleaseBean> = ArrayList()
        if (records.isNotEmpty) {
            graduationDesignReleaseBeans = records.into(GraduationDesignReleaseBean::class.java)
            val format = "yyyy-MM-dd HH:mm:ss"
            graduationDesignReleaseBeans.forEach { i ->
                i.fillTeacherStartTimeStr = DateTimeUtils.timestampToString(i.fillTeacherStartTime, format)
                i.fillTeacherEndTimeStr = DateTimeUtils.timestampToString(i.fillTeacherEndTime, format)
                i.startTimeStr = DateTimeUtils.timestampToString(i.startTime, format)
                i.endTimeStr = DateTimeUtils.timestampToString(i.endTime, format)
                i.releaseTimeStr = DateTimeUtils.timestampToString(i.releaseTime, format)
            }
            paginationUtils.setTotalDatas(countByCondition(paginationUtils, graduationDesignReleaseBean))
        }
        return graduationDesignReleaseBeans
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignRelease: GraduationDesignRelease) {
        graduationDesignReleaseDao.insert(graduationDesignRelease)
    }

    override fun update(graduationDesignRelease: GraduationDesignRelease) {
        graduationDesignReleaseDao.update(graduationDesignRelease)
    }

    fun countByCondition(paginationUtils: PaginationUtils, graduationDesignReleaseBean: GraduationDesignReleaseBean): Int {
        val count: Record1<Int>
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, graduationDesignReleaseBean)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_RELEASE)
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_RELEASE)
                    .join(USERS)
                    .on(GRADUATION_DESIGN_RELEASE.USERNAME.eq(USERS.USERNAME))
                    .join(SCIENCE)
                    .on(GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .join(SCHOOL)
                    .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                    .where(a)
            selectConditionStep.fetchOne()
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
            val graduationDesignTitle = StringUtils.trimWhitespace(search.getString("graduationDesignTitle"))
            if (StringUtils.hasLength(graduationDesignTitle)) {
                a = GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_TITLE.like(SQLQueryUtils.likeAllParam(graduationDesignTitle))
            }
        }
        return a
    }

    /**
     * 其它条件参数
     *
     * @param a                           搜索条件
     * @param graduationDesignReleaseBean 额外参数
     * @return 条件
     */
    private fun otherCondition(a: Condition?, graduationDesignReleaseBean: GraduationDesignReleaseBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(graduationDesignReleaseBean)) {
            if (!ObjectUtils.isEmpty(graduationDesignReleaseBean.departmentId) && graduationDesignReleaseBean.departmentId > 0) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(GRADUATION_DESIGN_RELEASE.DEPARTMENT_ID.eq(graduationDesignReleaseBean.departmentId))
                } else {
                    GRADUATION_DESIGN_RELEASE.DEPARTMENT_ID.eq(graduationDesignReleaseBean.departmentId)
                }
            }

            if (!ObjectUtils.isEmpty(graduationDesignReleaseBean.collegeId) && graduationDesignReleaseBean.collegeId!! > 0) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(COLLEGE.COLLEGE_ID.eq(graduationDesignReleaseBean.collegeId))
                } else {
                    COLLEGE.COLLEGE_ID.eq(graduationDesignReleaseBean.collegeId)
                }
            }

            if (!ObjectUtils.isEmpty(graduationDesignReleaseBean.scienceId) && graduationDesignReleaseBean.scienceId!! > 0) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(graduationDesignReleaseBean.scienceId))
                } else {
                    GRADUATION_DESIGN_RELEASE.SCIENCE_ID.eq(graduationDesignReleaseBean.scienceId)
                }
            }

            if (!ObjectUtils.isEmpty(graduationDesignReleaseBean.graduationDesignIsDel)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_IS_DEL.eq(graduationDesignReleaseBean.graduationDesignIsDel))
                } else {
                    GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_IS_DEL.eq(graduationDesignReleaseBean.graduationDesignIsDel)
                }
            }

            if (!ObjectUtils.isEmpty(graduationDesignReleaseBean.allowGrade)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(GRADUATION_DESIGN_RELEASE.ALLOW_GRADE.eq(graduationDesignReleaseBean.allowGrade))
                } else {
                    GRADUATION_DESIGN_RELEASE.ALLOW_GRADE.eq(graduationDesignReleaseBean.allowGrade)
                }
            }
        }
        return tempCondition
    }
}