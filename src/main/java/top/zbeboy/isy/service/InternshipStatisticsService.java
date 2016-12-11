package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.web.bean.internship.statistics.InternshipStatisticsBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

/**
 * Created by lenovo on 2016-12-10.
 */
public interface InternshipStatisticsService {

    /**
     * 分页查询 已提交数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> submittedFindAllByPage(DataTablesUtils<InternshipStatisticsBean> dataTablesUtils, InternshipStatisticsBean internshipStatisticsBean);

    /**
     * 已提交数据 总数
     *
     * @return 总数
     */
    int submittedCountAll(InternshipStatisticsBean internshipStatisticsBean);

    /**
     * 根据条件查询总数 已提交数据
     *
     * @return 条件查询总数
     */
    int submittedCountByCondition(DataTablesUtils<InternshipStatisticsBean> dataTablesUtils, InternshipStatisticsBean internshipStatisticsBean);

    /**
     * 分页查询 未提交数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> unsubmittedFindAllByPage(DataTablesUtils<InternshipStatisticsBean> dataTablesUtils, InternshipStatisticsBean internshipStatisticsBean);

    /**
     * 未提交数据 总数
     *
     * @return 总数
     */
    int unsubmittedCountAll(InternshipStatisticsBean internshipStatisticsBean);

    /**
     * 根据条件查询总数 未提交数据
     *
     * @return 条件查询总数
     */
    int unsubmittedCountByCondition(DataTablesUtils<InternshipStatisticsBean> dataTablesUtils, InternshipStatisticsBean internshipStatisticsBean);
}
