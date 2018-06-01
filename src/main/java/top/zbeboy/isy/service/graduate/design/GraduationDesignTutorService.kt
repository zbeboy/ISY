package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTutor
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2018-01-19 .
 **/
interface GraduationDesignTutorService {
    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 结果
     */
    fun findById(id: String): GraduationDesignTutor

    /**
     * 通过学生id与发布id查询指导教师信息
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业发布id
     * @return 指导教师信息
     */
    fun findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(studentId: Int, graduationDesignReleaseId: String): Optional<Record>

    /**
     * 通过学生id与发布id查询指导教师信息
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业发布id
     * @return 指导教师信息
     */
    fun findByStudentIdAndGraduationDesignReleaseIdRelation(studentId: Int, graduationDesignReleaseId: String): Optional<Record>

    /**
     * 通过指导教师id与发布id关联查询学生信息
     *
     * @param staffId                   教师id
     * @param graduationDesignReleaseId 毕业发布id
     * @return 数据
     */
    fun findByStaffIdAndGraduationDesignReleaseIdRelationForStudent(staffId: Int, graduationDesignReleaseId: String): Result<Record>

    /**
     * 通过指导教师id与发布id关联查询学生信息
     *
     * @param graduationDesignTeacherId 指导教师id
     * @return 数据
     */
    fun findByGraduationDesignTeacherIdRelationForStudent(graduationDesignTeacherId: String): Result<Record>

    /**
     * 统计未填报学生数
     *
     * @param graduationDesignReleaseBean 毕业发布
     * @return 学生数
     */
    fun countNotFillStudent(graduationDesignReleaseBean: GraduationDesignReleaseBean): Int

    /**
     * 统计填报学生数
     *
     * @param graduationDesignReleaseBean 毕业发布
     * @return 学生数
     */
    fun countFillStudent(graduationDesignReleaseBean: GraduationDesignReleaseBean): Int

    /**
     * 根据 指导教师id删除
     *
     * @param graduationDesignTeacherId 指导教师id
     */
    fun deleteByGraduationDesignTeacherId(graduationDesignTeacherId: String)

    /**
     * 保存
     *
     * @param graduationDesignTutor 数据
     */
    fun save(graduationDesignTutor: GraduationDesignTutor)

    /**
     * 更新
     *
     * @param graduationDesignTutor 数据
     */
    fun update(graduationDesignTutor: GraduationDesignTutor)

    /**
     * 根据主键删除
     *
     * @param ids 主键
     */
    fun deleteByIds(ids: List<String>)

    /**
     * 已填报学生数据
     *
     * @param dataTablesUtils datatables 工具
     * @return 数据
     */
    fun findAllFillByPage(dataTablesUtils: DataTablesUtils<GraduationDesignTutorBean>, condition: GraduationDesignTutorBean): List<GraduationDesignTutorBean>

    /**
     * 统计已填报学生
     *
     * @return 结果
     */
    fun countAllFill(condition: GraduationDesignTutorBean): Int

    /**
     * 根据条件统计已填报学生
     *
     * @param dataTablesUtils datatables 工具
     * @return 结果
     */
    fun countFillByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignTutorBean>, condition: GraduationDesignTutorBean): Int

    /**
     * 未填报学生数据
     *
     * @param dataTablesUtils datatables 工具
     * @return 数据
     */
    fun findAllNotFillByPage(dataTablesUtils: DataTablesUtils<StudentBean>, condition: GraduationDesignRelease): Result<Record>

    /**
     * 统计未填报学生
     *
     * @return 结果
     */
    fun countAllNotFill(condition: GraduationDesignRelease): Int

    /**
     * 根据条件统计未填报学生
     *
     * @param dataTablesUtils datatables 工具
     * @return 结果
     */
    fun countNotFillByCondition(dataTablesUtils: DataTablesUtils<StudentBean>, condition: GraduationDesignRelease): Int

    /**
     * 教师指导人数
     *
     * @param graduationDesignReleaseId 发布id
     * @param staffId                   教师id
     * @return 人数
     */
    fun countByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId: String, staffId: Int): Int
}