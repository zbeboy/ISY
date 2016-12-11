package top.zbeboy.isy.service;

import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean;
import top.zbeboy.isy.web.bean.internship.review.InternshipReviewBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import java.util.List;

/**
 * Created by zbeboy on 2016/12/7.
 */
public interface InternshipReviewService {

    /**
     * 实习审核数据
     *
     * @param paginationUtils     分页工具
     * @param internshipApplyBean 额外参数
     * @return 数据
     */
    List<InternshipReviewBean> findAllByPage(PaginationUtils paginationUtils, InternshipApplyBean internshipApplyBean);

    /**
     * 根据条件统计
     *
     * @param paginationUtils     分页工具
     * @param internshipApplyBean 额外参数
     * @return 统计
     */
    int countByCondition(PaginationUtils paginationUtils, InternshipApplyBean internshipApplyBean);

    /**
     * 统计一个实习不同状态下的数据
     *
     * @param internshipReleaseId  实习发布id
     * @param internshipApplyState 状态
     * @return 统计结果
     */
    int countByInternshipReleaseIdAndInternshipApplyState(String internshipReleaseId, int internshipApplyState);
}
