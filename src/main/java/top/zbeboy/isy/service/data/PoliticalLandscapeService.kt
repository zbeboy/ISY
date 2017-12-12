package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.PoliticalLandscape
import top.zbeboy.isy.domain.tables.records.PoliticalLandscapeRecord
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-12 .
 **/
interface PoliticalLandscapeService {
    /**
     * 查询全部政治面貌
     *
     * @return 全部政治面貌
     */
    fun findAll(): List<PoliticalLandscape>

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 政治面貌
     */
    fun findById(id: Int): PoliticalLandscape

    /**
     * 通过政治面貌查询
     *
     * @param politicalLandscapeName 政治面貌
     * @return 政治面貌
     */
    fun findByPoliticalLandscapeName(politicalLandscapeName: String): List<PoliticalLandscape>

    /**
     * 保存
     *
     * @param politicalLandscape 政治面貌
     */
    fun save(politicalLandscape: PoliticalLandscape)

    /**
     * 更新
     *
     * @param politicalLandscape 政治面貌
     */
    fun update(politicalLandscape: PoliticalLandscape)

    /**
     * 通过政治面貌查询 注：不等于政治面貌id
     *
     * @param politicalLandscapeName 政治面貌
     * @param politicalLandscapeId   政治面貌id
     * @return 政治面貌
     */
    fun findByNationNameNePoliticalLandscapeId(politicalLandscapeName: String, politicalLandscapeId: Int): Result<PoliticalLandscapeRecord>

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<PoliticalLandscape>): Result<Record>

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
    fun countByCondition(dataTablesUtils: DataTablesUtils<PoliticalLandscape>): Int
}