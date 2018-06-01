package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.AcademicTitle
import top.zbeboy.isy.domain.tables.records.AcademicTitleRecord
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-12 .
 **/
interface AcademicTitleService {
    /**
     * 查询全部职称
     *
     * @return 全部职称
     */
    fun findAll(): List<AcademicTitle>

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 职称
     */
    fun findById(id: Int): AcademicTitle

    /**
     * 通过职称查询
     *
     * @param academicTitleName 职称
     * @return 职称
     */
    fun findByAcademicTitleName(academicTitleName: String): List<AcademicTitle>

    /**
     * 保存
     *
     * @param academicTitle 职称
     */
    fun save(academicTitle: AcademicTitle)

    /**
     * 更新
     *
     * @param academicTitle 职称
     */
    fun update(academicTitle: AcademicTitle)

    /**
     * 通过职称查询 注：不等于职称id
     *
     * @param academicTitleName 职称
     * @param academicTitleId   职称id
     * @return 职称
     */
    fun findByAcademicTitleNameNeAcademicTitleId(academicTitleName: String, academicTitleId: Int): Result<AcademicTitleRecord>

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<AcademicTitle>): Result<Record>

    /**
     * 总数
     *
     * @return 总数
     */
    fun countAll(): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<AcademicTitle>): Int
}