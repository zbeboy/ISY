package top.zbeboy.isy.service.data

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.Staff
import top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo
import top.zbeboy.isy.domain.tables.records.StaffRecord
import top.zbeboy.isy.elastic.pojo.StaffElastic
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-12-09 .
 **/
interface StaffService {
    /**
     * 通过id关联查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findByIdRelation(id: Int): Optional<Record>

    /**
     * 通过id关联查询(注：只关联users表)
     *
     * @param id 主键
     * @return 数据
     */
    fun findByIdRelationForUsers(id: Int): Optional<Record>

    /**
     * 通过主键批量查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findInIdsRelation(id: List<Int>): Result<Record>

    /**
     * 根据工号查询
     *
     * @param staffNumber 工号
     * @return 教职工们
     */
    fun findByStaffNumber(staffNumber: String): Staff?

    /**
     * 根据系id查询 有权限并且未被注销的教师
     *
     * @param departmentId  系id
     * @param b             用户状态
     * @param verifyMailbox 是否已验证邮箱
     * @return 教职工们
     */
    fun findByDepartmentIdAndEnabledAndVerifyMailboxExistsAuthoritiesRelation(departmentId: Int, b: Byte?, verifyMailbox: Byte?): Result<Record>

    /**
     * 根据用户账号查询
     *
     * @param username 用户账号
     * @return 教职工
     */
    fun findByUsername(username: String): Staff

    /**
     * 根据工号查询 注：不等于用户账号
     *
     * @param username    用户账号
     * @param staffNumber 工号
     * @return 教职工
     */
    fun findByStaffNumberNeUsername(username: String, staffNumber: String): Result<StaffRecord>

    /**
     * 保存教职工信息
     *
     * @param staffElastic 教职工
     */
    fun save(staffElastic: StaffElastic)

    /**
     * 更新教职式信息
     *
     * @param staff 教职工
     * @param usersUniqueInfo 需要单独同步
     */
    fun update(staff: Staff, usersUniqueInfo: UsersUniqueInfo?)

    /**
     * 通过用户账号关联查询 注：信息包括学校等 建议用于验证，效率不高
     *
     * @param username 用户账号
     * @return 关联信息
     */
    fun findByUsernameRelation(username: String): Optional<Record>

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
    fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): Result<Record>

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): Result<Record>

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
    fun countByConditionExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): Int

    /**
     * 根据条件统计无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 数量
     */
    fun countByConditionNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): Int
}