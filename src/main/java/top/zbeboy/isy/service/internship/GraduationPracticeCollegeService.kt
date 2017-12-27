package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.internship.apply.GraduationPracticeCollegeVo
import java.util.*

/**
 * Created by zbeboy 2017-12-27 .
 **/
interface GraduationPracticeCollegeService {
    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 毕业实习(校内)
     */
    fun findById(id: String): GraduationPracticeCollege

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
     * @param graduationPracticeCollege 毕业实习(校内)
     */
    fun save(graduationPracticeCollege: GraduationPracticeCollege)

    /**
     * 开启事务保存
     *
     * @param graduationPracticeCollegeVo 毕业实习(校内)
     */
    fun saveWithTransaction(graduationPracticeCollegeVo: GraduationPracticeCollegeVo)

    /**
     * 更新
     *
     * @param graduationPracticeCollege 毕业实习(校内)
     */
    fun update(graduationPracticeCollege: GraduationPracticeCollege)

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
    fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationPracticeCollege>, graduationPracticeCollege: GraduationPracticeCollege): Result<Record>

    /**
     * 系总数
     *
     * @return 总数
     */
    fun countAll(graduationPracticeCollege: GraduationPracticeCollege): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeCollege>, graduationPracticeCollege: GraduationPracticeCollege): Int


    /**
     * 查询
     *
     * @param dataTablesUtils           datatables工具类
     * @param graduationPracticeCollege 毕业实习(校内)
     * @return 导出数据
     */
    fun exportData(dataTablesUtils: DataTablesUtils<GraduationPracticeCollege>, graduationPracticeCollege: GraduationPracticeCollege): Result<Record>
}