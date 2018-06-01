package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.Nation
import top.zbeboy.isy.domain.tables.records.NationRecord
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-12 .
 **/
interface NationService {
    /**
     * 查询全部民族
     *
     * @return 全部民族
     */
    fun findAll(): List<Nation>

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 民族
     */
    fun findById(id: Int): Nation

    /**
     * 通过民族查询
     *
     * @param nationName 民族
     * @return 民族
     */
    fun findByNationName(nationName: String): List<Nation>

    /**
     * 保存
     *
     * @param nation 民族
     */
    fun save(nation: Nation)

    /**
     * 更新
     *
     * @param nation 民族
     */
    fun update(nation: Nation)

    /**
     * 通过民族查询 注：不等于民族id
     *
     * @param nationName 民族
     * @param nationId   民族id
     * @return 民族
     */
    fun findByNationNameNeNationId(nationName: String, nationId: Int): Result<NationRecord>

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<Nation>): Result<Record>

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
    fun countByCondition(dataTablesUtils: DataTablesUtils<Nation>): Int
}