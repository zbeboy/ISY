package top.zbeboy.isy.service.internship

import org.jooq.*
import top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution
import top.zbeboy.isy.domain.tables.records.InternshipTeacherDistributionRecord
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-12-21 .
 **/
interface InternshipTeacherDistributionService {
    /**
     * 通过实习发布id distinct查询班级id
     *
     * @param internshipReleaseId 实习发布id
     * @return 数据
     */
    fun findByInternshipReleaseIdDistinctOrganizeId(internshipReleaseId: String): Result<Record1<Int>>

    /**
     * 通过实习发布id 和学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Optional<Record>

    /**
     * 通过实习ids distinct学生id查询
     *
     * @param internshipReleaseIds 实习发布ids
     * @return 数据
     */
    fun findInInternshipReleaseIdsDistinctStudentId(internshipReleaseIds: List<String>): Result<Record2<Int, Int>>

    /**
     * 根据发布id查询所有指导教师
     *
     * @param internshipReleaseId 发布id
     * @return 教师们
     */
    fun findByInternshipReleaseIdDistinctStaffId(internshipReleaseId: String): Result<Record3<Int, String, String>>

    /**
     * 通过实习发布id 和指导教师id查询 学生信息
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             指导教师id
     * @return 数据
     */
    fun findByInternshipReleaseIdAndStaffIdForStudent(internshipReleaseId: String, staffId: Int): Result<Record>

    /**
     * 通过实习发布id 和学生id查询 指导教师
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    fun findByInternshipReleaseIdAndStudentIdForStaff(internshipReleaseId: String, studentId: Int): Optional<Record>

    /**
     * 通过实习发布id 和教职工id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             教职工id
     * @return 数据
     */
    fun findByInternshipReleaseIdAndStaffId(internshipReleaseId: String, staffId: Int): Result<InternshipTeacherDistributionRecord>

    /**
     * 为批量分配查询学生数据
     *
     * @param organizeIds         专业id
     * @param internshipReleaseId 实习发布id 集合
     * @param enabled             用户状态
     * @param verifyMailbox       是否已验证邮箱
     * @return 数据
     */
    fun findStudentForBatchDistributionEnabledAndVerifyMailbox(organizeIds: List<Int>, internshipReleaseId: List<String>, enabled: Byte?, verifyMailbox: Byte?): Result<Record>

    /**
     * 保存
     *
     * @param internshipTeacherDistribution 教师分配
     */
    fun save(internshipTeacherDistribution: InternshipTeacherDistribution)

    /**
     * 更新
     *
     * @param internshipTeacherDistribution 教师分配
     */
    fun updateStaffId(internshipTeacherDistribution: InternshipTeacherDistribution)

    /**
     * 通过实习发布id 和 学生id删除
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int)

    /**
     * 通过实习发布id删除
     *
     * @param internshipReleaseId 实习发布id
     */
    fun deleteByInternshipReleaseId(internshipReleaseId: String)

    /**
     * 通过比对其它实习学生id删除
     *
     * @param internshipReleaseId 实习发布id
     * @param excludeInternships  其它实习id
     */
    fun comparisonDel(internshipReleaseId: String, excludeInternships: List<String>)

    /**
     * 删除未申请学生的分配
     *
     * @param internshipReleaseId 实习发布Id
     */
    fun deleteNotApply(internshipReleaseId: String)

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<InternshipTeacherDistributionBean>, internshipReleaseId: String): List<InternshipTeacherDistributionBean>

    /**
     * 总数
     *
     * @return 总数
     */
    fun countAll(internshipReleaseId: String): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<InternshipTeacherDistributionBean>, internshipReleaseId: String): Int
}