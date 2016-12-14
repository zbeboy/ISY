package top.zbeboy.isy.service;

import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;

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
