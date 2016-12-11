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
}
