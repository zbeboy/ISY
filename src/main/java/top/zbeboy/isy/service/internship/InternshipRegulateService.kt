package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.InternshipRegulate
import top.zbeboy.isy.web.bean.internship.regulate.InternshipRegulateBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-20 .
 **/
interface InternshipRegulateService {
    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 监管记录
     */
    fun findById(id: String): InternshipRegulate

    /**
     * 保存实习监管
     *
     * @param internshipRegulate 数据
     */
    fun save(internshipRegulate: InternshipRegulate)

    /**
     * 更新实习监管
     *
     * @param internshipRegulate 数据
     */
    fun update(internshipRegulate: InternshipRegulate)

    /**
     * 批量删除
     *
     * @param ids ids
     */
    fun batchDelete(ids: List<String>)

    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<InternshipRegulateBean>, internshipRegulateBean: InternshipRegulateBean): Result<Record>

    /**
     * 数据 总数
     *
     * @return 总数
     */
    fun countAll(internshipRegulateBean: InternshipRegulateBean): Int

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<InternshipRegulateBean>, internshipRegulateBean: InternshipRegulateBean): Int

    /**
     * 导出
     *
     * @param dataTablesUtils        datatables工具类
     * @param internshipRegulateBean 监管
     * @return 导出数据
     */
    fun exportData(dataTablesUtils: DataTablesUtils<InternshipRegulateBean>, internshipRegulateBean: InternshipRegulateBean): Result<Record>
}