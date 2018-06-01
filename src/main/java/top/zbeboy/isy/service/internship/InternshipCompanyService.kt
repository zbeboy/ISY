package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.InternshipCompany
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.internship.apply.InternshipCompanyVo
import java.util.*

/**
 * Created by zbeboy 2017-12-27 .
 **/
interface InternshipCompanyService {
    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 校外自主实习(去单位)
     */
    fun findById(id: String): InternshipCompany

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Optional<Record>

    /**
     * 保存
     *
     * @param internshipCompany 校外自主实习(去单位)
     */
    fun save(internshipCompany: InternshipCompany)

    /**
     * 开启事务保存
     *
     * @param internshipCompanyVo 校外自主实习(去单位)
     */
    fun saveWithTransaction(internshipCompanyVo: InternshipCompanyVo)

    /**
     * 更新
     *
     * @param internshipCompany 校外自主实习(去单位)
     */
    fun update(internshipCompany: InternshipCompany)

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int)

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<InternshipCompany>, internshipCompany: InternshipCompany): Result<Record>

    /**
     * 系总数
     *
     * @return 总数
     */
    fun countAll(internshipCompany: InternshipCompany): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<InternshipCompany>, internshipCompany: InternshipCompany): Int

    /**
     * 查询
     *
     * @param dataTablesUtils   datatables工具类
     * @param internshipCompany 校外自主实习(去单位)
     * @return 导出数据
     */
    fun exportData(dataTablesUtils: DataTablesUtils<InternshipCompany>, internshipCompany: InternshipCompany): Result<Record>
}