package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.Student
import top.zbeboy.isy.domain.tables.records.StudentRecord
import top.zbeboy.isy.elastic.pojo.StudentElastic
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-12-11 .
 **/
interface StudentService {
    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 学生
     */
    fun findById(id: Int): Student

    /**
     * 通过id关联查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findByIdRelation(id: Int): Optional<Record>

    /**
     * 通过id关联查询 (注：只关联users表)
     *
     * @param id 主键
     * @return 数据
     */
    fun findByIdRelationForUsers(id: Int): Optional<Record>

    /**
     * 根据学号查询学生
     *
     * @param studentNumber 学号
     * @return 学生
     */
    fun findByStudentNumber(studentNumber: String): Student?

    /**
     * 根据班级id集合查询有权限未注销的学生
     *
     * @param organizeIds   班级ids
     * @param b             用户状态
     * @param verifyMailbox 是否已验证邮箱
     * @return 学生们
     */
    fun findInOrganizeIdsAndEnabledAndVerifyMailboxExistsAuthoritiesRelation(organizeIds: List<Int>, b: Byte?, verifyMailbox: Byte?): Result<Record>

    /**
     * 通过账号与系id查询
     *
     * @param username     账号
     * @param departmentId 系id
     * @return 数据
     */
    fun findByUsernameAndDepartmentId(username: String, departmentId: Int): Optional<Record>

    /**
     * 通过账号与系id查询
     *
     * @param studentNumber 学号
     * @param departmentId  系id
     * @return 数据
     */
    fun findByStudentNumberAndDepartmentId(studentNumber: String, departmentId: Int): Optional<Record>

    /**
     * 根据学号查询 注：不等于用户账号
     *
     * @param username      用户账号
     * @param studentNumber 学号
     * @return 学生
     */
    fun findByStudentNumberNeUsername(username: String, studentNumber: String): Result<StudentRecord>

    /**
     * 保存学生信息
     *
     * @param studentElastic 学生
     */
    fun save(studentElastic: StudentElastic)

    /**
     * 更新学生信息
     *
     * @param student 学生
     */
    fun update(student: Student)

    /**
     * 通过用户账号关联查询 注：信息包括学校等 建议用于验证，效率不高
     *
     * @param username 用户账号
     * @return 关联信息
     */
    fun findByUsernameRelation(username: String): Optional<Record>

    /**
     * 通过用户账号,专业id,年级关联查询
     *
     * @param username  用户账号
     * @param scienceId 专业id
     * @param grade     年级
     * @return 关联信息
     */
    fun findByUsernameAndScienceIdAndGradeRelation(username: String, scienceId: Int, grade: String): Optional<Record>

    /**
     * 通过用户账号查询
     *
     * @param username 用户账号
     * @return 学生
     */
    fun findByUsername(username: String): Student

    /**
     * 通过账号删除
     *
     * @param username 用户账号
     */
    fun deleteByUsername(username: String)

    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): Result<Record>

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): Result<Record>

    /**
     * 统计有权限的用户
     *
     * @return 总数
     */
    fun countAllExistsAuthorities(): Int

    /**
     * 统计无权限的用户
     *
     * @return 总数
     */
    fun countAllNotExistsAuthorities(): Int

    /**
     * 根据条件统计有权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 数量
     */
    fun countByConditionExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): Int

    /**
     * 根据条件统计无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 数量
     */
    fun countByConditionNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): Int

    /**
     * 根据班级id，状态 统计有权限用户
     *
     * @param organizeId 班级id
     * @param b          状态
     * @return 数量
     */
    fun countByOrganizeIdAndEnabledExistsAuthorities(organizeId: Int, b: Byte?): Int
}