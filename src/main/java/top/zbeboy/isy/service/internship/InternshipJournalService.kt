package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal
import top.zbeboy.isy.domain.tables.records.InternshipJournalRecord
import top.zbeboy.isy.web.bean.internship.journal.InternshipJournalBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-29 .
 **/
interface InternshipJournalService {
    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 实习日志
     */
    fun findById(id: String): InternshipJournal

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Result<InternshipJournalRecord>

    /**
     * 通过实习发布id与教职工id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             教职工id
     * @return 数据
     */
    fun findByInternshipReleaseIdAndStaffId(internshipReleaseId: String, staffId: Int): Result<InternshipJournalRecord>

    /**
     * 保存
     *
     * @param internshipJournal 实习日志
     */
    fun save(internshipJournal: InternshipJournal)

    /**
     * 更新
     *
     * @param internshipJournal 实习日志
     */
    fun update(internshipJournal: InternshipJournal)

    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<InternshipJournalBean>, internshipJournalBean: InternshipJournalBean): Result<Record>

    /**
     * 数据 总数
     *
     * @return 总数
     */
    fun countAll(internshipJournalBean: InternshipJournalBean): Int

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<InternshipJournalBean>, internshipJournalBean: InternshipJournalBean): Int

    /**
     * 通过id删除
     *
     * @param id id
     */
    fun deleteById(id: String)
}