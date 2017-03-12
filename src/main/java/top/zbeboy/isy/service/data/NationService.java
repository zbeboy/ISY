package top.zbeboy.isy.service.data;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Nation;
import top.zbeboy.isy.domain.tables.records.NationRecord;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2016-10-30.
 */
public interface NationService {

    /**
     * 查询全部民族
     *
     * @return 全部民族
     */
    List<Nation> findAll();

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 民族
     */
    Nation findById(int id);

    /**
     * 通过民族查询
     *
     * @param nationName 民族
     * @return 民族
     */
    List<Nation> findByNationName(String nationName);

    /**
     * 保存
     *
     * @param nation 民族
     */
    void save(Nation nation);

    /**
     * 更新
     *
     * @param nation 民族
     */
    void update(Nation nation);

    /**
     * 通过民族查询 注：不等于民族id
     *
     * @param nationName 民族
     * @param nationId   民族id
     * @return 民族
     */
    Result<NationRecord> findByNationNameNeNationId(String nationName, int nationId);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<Nation> dataTablesUtils);

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
    int countByCondition(DataTablesUtils<Nation> dataTablesUtils);
}
