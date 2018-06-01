package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCompany
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.internship.apply.GraduationPracticeCompanyVo
import java.util.*

/**
 * Created by zbeboy 2017-12-27 .
 **/
interface GraduationPracticeCompanyService {
    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 毕业实习(校外)
     */
    fun findById(id: String): GraduationPracticeCompany

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
     * @param graduationPracticeCompany 毕业实习(校外)
     */
    fun save(graduationPracticeCompany: GraduationPracticeCompany)

    /**
     * 开启事务保存
     *
     * @param graduationPracticeCompanyVo 毕业实习(校外)
     */
    fun saveWithTransaction(graduationPracticeCompanyVo: GraduationPracticeCompanyVo)

    /**
     * 更新
     *
     * @param graduationPracticeCompany 毕业实习(校外)
     */
    fun update(graduationPracticeCompany: GraduationPracticeCompany)

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
    fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationPracticeCompany>, graduationPracticeCompany: GraduationPracticeCompany): Result<Record>

    /**
     * 系总数
     *
     * @return 总数
     */
    fun countAll(graduationPracticeCompany: GraduationPracticeCompany): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeCompany>, graduationPracticeCompany: GraduationPracticeCompany): Int

    /**
     * 查询
     *
     * @param dataTablesUtils           datatables工具类
     * @param graduationPracticeCompany 毕业实习(校外)
     * @return 导出数据
     */
    fun exportData(dataTablesUtils: DataTablesUtils<GraduationPracticeCompany>, graduationPracticeCompany: GraduationPracticeCompany): Result<Record>
}