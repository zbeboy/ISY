package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.InternshipCollege
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.internship.apply.InternshipCollegeVo
import java.util.*

/**
 * Created by zbeboy 2017-12-27 .
 **/
interface InternshipCollegeService {
    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 校外自主实习(去单位)
     */
    fun findById(id: String): InternshipCollege

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
     * @param internshipCollege 校外自主实习(去单位)
     */
    fun save(internshipCollege: InternshipCollege)

    /**
     * 开启事务保存
     *
     * @param internshipCollegeVo 校外自主实习(去单位)
     */
    fun saveWithTransaction(internshipCollegeVo: InternshipCollegeVo)

    /**
     * 更新
     *
     * @param internshipCollege 校外自主实习(去单位)
     */
    fun update(internshipCollege: InternshipCollege)

    /**
     * 通过实习发布id与学生id删除
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
    fun findAllByPage(dataTablesUtils: DataTablesUtils<InternshipCollege>, internshipCollege: InternshipCollege): Result<Record>

    /**
     * 系总数
     *
     * @return 总数
     */
    fun countAll(internshipCollege: InternshipCollege): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<InternshipCollege>, internshipCollege: InternshipCollege): Int

    /**
     * 导出
     *
     * @param dataTablesUtils   datatables工具类
     * @param internshipCollege 校外自主实习(去单位)
     * @return 导出数据
     */
    fun exportData(dataTablesUtils: DataTablesUtils<InternshipCollege>, internshipCollege: InternshipCollege): Result<Record>
}