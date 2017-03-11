package top.zbeboy.isy.service.data;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.PoliticalLandscape;
import top.zbeboy.isy.domain.tables.records.PoliticalLandscapeRecord;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2016-10-30.
 */
public interface PoliticalLandscapeService {

    /**
     * 查询全部政治面貌
     *
     * @return 全部政治面貌
     */
    List<PoliticalLandscape> findAll();

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 政治面貌
     */
    PoliticalLandscape findById(int id);

    /**
     * 通过政治面貌查询
     *
     * @param politicalLandscapeName 政治面貌
     * @return 政治面貌
     */
    List<PoliticalLandscape> findByPoliticalLandscapeName(String politicalLandscapeName);

    /**
     * 保存
     *
     * @param politicalLandscape 政治面貌
     */
    void save(PoliticalLandscape politicalLandscape);

    /**
     * 更新
     *
     * @param politicalLandscape 政治面貌
     */
    void update(PoliticalLandscape politicalLandscape);

    /**
     * 通过政治面貌查询 注：不等于政治面貌id
     *
     * @param politicalLandscapeName 政治面貌
     * @param politicalLandscapeId   政治面貌id
     * @return 政治面貌
     */
    Result<PoliticalLandscapeRecord> findByNationNameNePoliticalLandscapeId(String politicalLandscapeName, int politicalLandscapeId);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<PoliticalLandscape> dataTablesUtils);

    /**
     * 总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<PoliticalLandscape> dataTablesUtils);
}
