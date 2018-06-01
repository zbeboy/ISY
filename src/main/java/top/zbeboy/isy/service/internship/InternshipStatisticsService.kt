package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.web.bean.internship.statistics.InternshipStatisticsBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-28 .
 **/
interface InternshipStatisticsService {
    /**
     * 分页查询 已提交数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun submittedFindAllByPage(dataTablesUtils: DataTablesUtils<InternshipStatisticsBean>, internshipStatisticsBean: InternshipStatisticsBean): Result<Record>

    /**
     * 已提交数据 总数
     *
     * @return 总数
     */
    fun submittedCountAll(internshipStatisticsBean: InternshipStatisticsBean): Int

    /**
     * 根据条件查询总数 已提交数据
     *
     * @return 条件查询总数
     */
    fun submittedCountByCondition(dataTablesUtils: DataTablesUtils<InternshipStatisticsBean>, internshipStatisticsBean: InternshipStatisticsBean): Int

    /**
     * 分页查询 未提交数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun unsubmittedFindAllByPage(dataTablesUtils: DataTablesUtils<InternshipStatisticsBean>, internshipStatisticsBean: InternshipStatisticsBean): Result<Record>

    /**
     * 未提交数据 总数
     *
     * @return 总数
     */
    fun unsubmittedCountAll(internshipStatisticsBean: InternshipStatisticsBean): Int

    /**
     * 根据条件查询总数 未提交数据
     *
     * @return 条件查询总数
     */
    fun unsubmittedCountByCondition(dataTablesUtils: DataTablesUtils<InternshipStatisticsBean>, internshipStatisticsBean: InternshipStatisticsBean): Int
}