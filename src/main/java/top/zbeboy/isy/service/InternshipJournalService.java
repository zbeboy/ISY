package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;
import top.zbeboy.isy.web.bean.internship.journal.InternshipJournalBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

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
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 保存
     *
     * @param internshipJournal 实习日志
     */
    void save(InternshipJournal internshipJournal);

    /**
     * 更新
     *
     * @param internshipJournal 实习日志
     */
    void update(InternshipJournal internshipJournal);

    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<InternshipJournalBean> dataTablesUtils, InternshipJournalBean internshipJournalBean);

    /**
     * 数据 总数
     *
     * @return 总数
     */
    int countAll(InternshipJournalBean internshipJournalBean);

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<InternshipJournalBean> dataTablesUtils,InternshipJournalBean internshipJournalBean);

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
