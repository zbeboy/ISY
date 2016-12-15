package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by zbeboy on 2016/12/14.
 */
public interface InternshipJournalService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 实习日志
     */
    InternshipJournal findById(String id);

    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<InternshipJournal> dataTablesUtils, InternshipJournal internshipJournal);

    /**
     * 数据 总数
     *
     * @return 总数
     */
    int countAll(InternshipJournal internshipJournal);

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<InternshipJournal> dataTablesUtils, InternshipJournal internshipJournal);

    /**
     * 通过id查询
     *
     * @param ids 主键
     * @return 实习日志
     */
    List<InternshipJournal> findInIds(String ids);

    /**
     * 批量删除
     *
     * @param ids ids
     */
    void batchDelete(List<String> ids);
}
