package top.zbeboy.isy.service.internship

import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean
import top.zbeboy.isy.web.bean.internship.review.InternshipReviewBean
import top.zbeboy.isy.web.util.PaginationUtils

/**
 * Created by zbeboy 2017-12-28 .
 **/
interface InternshipReviewService {
    /**
     * 实习审核数据
     *
     * @param paginationUtils     分页工具
     * @param internshipApplyBean 额外参数
     * @return 数据
     */
    fun findAllByPage(paginationUtils: PaginationUtils, internshipApplyBean: InternshipApplyBean): List<InternshipReviewBean>

    /**
     * 根据条件统计
     *
     * @param paginationUtils     分页工具
     * @param internshipApplyBean 额外参数
     * @return 统计
     */
    fun countByCondition(paginationUtils: PaginationUtils, internshipApplyBean: InternshipApplyBean): Int

    /**
     * 统计一个实习不同状态下的数据
     *
     * @param internshipReleaseId  实习发布id
     * @param internshipApplyState 状态
     * @return 统计结果
     */
    fun countByInternshipReleaseIdAndInternshipApplyState(internshipReleaseId: String, internshipApplyState: Int): Int

}