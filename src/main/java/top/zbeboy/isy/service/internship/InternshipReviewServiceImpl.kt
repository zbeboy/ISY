package top.zbeboy.isy.service.internship

import com.alibaba.fastjson.JSON
import org.apache.commons.lang3.math.NumberUtils
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean
import top.zbeboy.isy.web.bean.internship.review.InternshipReviewBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.sql.Timestamp
import java.util.*

/**
 * Created by zbeboy 2017-12-28 .
 **/
@Service("internshipReviewService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipReviewServiceImpl @Autowired constructor(dslContext: DSLContext) : InternshipReviewService {

    private val create: DSLContext = dslContext

    override fun findAllByPage(paginationUtils: PaginationUtils, internshipApplyBean: InternshipApplyBean): List<InternshipReviewBean> {
        val internshipReviewBeens = ArrayList<InternshipReviewBean>()
        val pageNum = paginationUtils.getPageNum()
        val pageSize = paginationUtils.getPageSize()
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, internshipApplyBean)
        val records = create.select()
                .from(INTERNSHIP_APPLY)
                .join(INTERNSHIP_RELEASE)
                .on(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                .join(STUDENT)
                .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                .leftJoin(FILES)
                .on(INTERNSHIP_APPLY.INTERNSHIP_FILE_ID.eq(FILES.FILE_ID))
                .where(a)
                .orderBy(STUDENT.STUDENT_NUMBER.asc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch()
        records.forEach { r ->
            val internshipReviewBean = InternshipReviewBean()
            internshipReviewBean.studentId = r.getValue(INTERNSHIP_APPLY.STUDENT_ID)
            internshipReviewBean.internshipReleaseId = r.getValue(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID)
            internshipReviewBean.internshipTypeId = r.getValue(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID)
            internshipReviewBean.realName = r.getValue(INTERNSHIP_RELEASE.PUBLISHER)
            internshipReviewBean.studentName = r.getValue(USERS.REAL_NAME)
            internshipReviewBean.studentNumber = r.getValue(STUDENT.STUDENT_NUMBER)
            internshipReviewBean.scienceName = r.getValue(SCIENCE.SCIENCE_NAME)
            internshipReviewBean.organizeName = r.getValue(ORGANIZE.ORGANIZE_NAME)
            internshipReviewBean.reason = r.getValue(INTERNSHIP_APPLY.REASON)
            internshipReviewBean.internshipApplyState = r.getValue(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE)
            if (!ObjectUtils.isEmpty(r.getValue<Timestamp>(INTERNSHIP_APPLY.CHANGE_FILL_START_TIME))) {
                internshipReviewBean.changeFillStartTime = DateTimeUtils.formatDate(r.getValue<Timestamp>(INTERNSHIP_APPLY.CHANGE_FILL_START_TIME))
            }
            if (!ObjectUtils.isEmpty(r.getValue<Timestamp>(INTERNSHIP_APPLY.CHANGE_FILL_END_TIME))) {
                internshipReviewBean.changeFillEndTime = DateTimeUtils.formatDate(r.getValue<Timestamp>(INTERNSHIP_APPLY.CHANGE_FILL_END_TIME))
            }
            if (!ObjectUtils.isEmpty(r.getValue<Timestamp>(INTERNSHIP_APPLY.APPLY_TIME))) {
                internshipReviewBean.applyTime = DateTimeUtils.formatDate(r.getValue<Timestamp>(INTERNSHIP_APPLY.APPLY_TIME))
            }

            internshipReviewBean.fileId = r.getValue(FILES.FILE_ID)
            internshipReviewBean.originalFileName = r.getValue(FILES.ORIGINAL_FILE_NAME)
            internshipReviewBean.ext = r.getValue(FILES.EXT)
            internshipReviewBeens.add(internshipReviewBean)
        }
        paginationUtils.setTotalDatas(countByCondition(paginationUtils, internshipApplyBean))
        return internshipReviewBeens
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
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
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

    override fun countByInternshipReleaseIdAndInternshipApplyState(internshipReleaseId: String, internshipApplyState: Int): Int {
        val count = create.selectCount()
                .from(INTERNSHIP_APPLY)
                .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(internshipApplyState)))
                .fetchOne()
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
            val studentName = StringUtils.trimWhitespace(search.getString("studentName"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val scienceName = StringUtils.trimWhitespace(search.getString("scienceName"))
            val organizeName = StringUtils.trimWhitespace(search.getString("organizeName"))
            if (StringUtils.hasLength(studentName)) {
                a = USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName))
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (!ObjectUtils.isEmpty(a)) {
                    a!!.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                } else {
                    STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                }
            }

            if (StringUtils.hasLength(scienceName)) {
                val scienceId = NumberUtils.toInt(scienceName)
                if (scienceId > 0) {
                    a = if (!ObjectUtils.isEmpty(a)) {
                        a!!.and(SCIENCE.SCIENCE_ID.eq(scienceId))
                    } else {
                        SCIENCE.SCIENCE_ID.eq(scienceId)
                    }
                }
            }

            if (StringUtils.hasLength(organizeName)) {
                val organizeId = NumberUtils.toInt(organizeName)
                if (organizeId > 0) {
                    a = if (!ObjectUtils.isEmpty(a)) {
                        a!!.and(ORGANIZE.ORGANIZE_ID.eq(organizeId))
                    } else {
                        ORGANIZE.ORGANIZE_ID.eq(organizeId)
                    }
                }
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
            if (StringUtils.hasLength(internshipApplyBean.internshipReleaseId)) {
                tempCondition = if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition!!.and(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID.eq(internshipApplyBean.internshipReleaseId))
                } else {
                    INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID.eq(internshipApplyBean.internshipReleaseId)
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