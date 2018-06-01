package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeUnify
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.vo.internship.apply.GraduationPracticeUnifyVo
import java.util.*

/**
 * Created by zbeboy 2017-12-27 .
 **/
interface GraduationPracticeUnifyService {
    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 毕业实习(学校统一组织校外实习)
     */
    fun findById(id: String): GraduationPracticeUnify

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
     * @param graduationPracticeUnify 毕业实习(学校统一组织校外实习)
     */
    fun save(graduationPracticeUnify: GraduationPracticeUnify)

    /**
     * 开启事务保存
     *
     * @param graduationPracticeUnifyVo 毕业实习(学校统一组织校外实习)
     */
    fun saveWithTransaction(graduationPracticeUnifyVo: GraduationPracticeUnifyVo)

    /**
     * 更新
     *
     * @param graduationPracticeUnify 毕业实习(学校统一组织校外实习)
     */
    fun update(graduationPracticeUnify: GraduationPracticeUnify)

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
    fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationPracticeUnify>, graduationPracticeUnify: GraduationPracticeUnify): Result<Record>

    /**
     * 系总数
     *
     * @return 总数
     */
    fun countAll(graduationPracticeUnify: GraduationPracticeUnify): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationPracticeUnify>, graduationPracticeUnify: GraduationPracticeUnify): Int

    /**
     * 查询
     *
     * @param dataTablesUtils         datatables工具类
     * @param graduationPracticeUnify 毕业实习(学校统一组织校外实习)
     * @return 导出数据
     */
    fun exportData(dataTablesUtils: DataTablesUtils<GraduationPracticeUnify>, graduationPracticeUnify: GraduationPracticeUnify): Result<Record>
}