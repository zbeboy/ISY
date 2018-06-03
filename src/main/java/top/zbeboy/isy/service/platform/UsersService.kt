package top.zbeboy.isy.service.platform

import org.jooq.*
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord
import top.zbeboy.isy.domain.tables.records.UsersRecord
import top.zbeboy.isy.web.bean.platform.users.UsersBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-11-19 .
 **/
interface UsersService {
    /**
     * 根据用户名获取Users表完整信息
     *
     * @param username 用户账号
     * @return 用户信息
     */
    fun findByUsername(username: String): Users?

    /**
     * 注意 用于定时 查询未验证用户
     *
     * @param joinDate      加入时间
     * @param verifyMailbox 验证否
     * @return 用户
     */
    fun findByJoinDateAndVerifyMailbox(joinDate: Date, verifyMailbox: Byte?): Result<UsersRecord>

    /**
     * 从session中获取用户完整信息
     *
     * @return session中的用户信息
     */
    fun getUserFromSession(): Users?

    /**
     * 获取用户学校相关信息 注：用户必须是学生或教职工
     *
     * @return 用户学校相关信息
     */
    fun findUserSchoolInfo(users: Users): Optional<Record>

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户们
     */
    fun findByMobile(mobile: String): List<Users>

    /**
     * 根据手机号查询用户 注：不等于用户账号
     *
     * @param mobile   手机号
     * @param username 用户账号
     * @return 用户们
     */
    fun findByMobileNeUsername(mobile: String, username: String): Result<UsersRecord>

    /**
     * 保存用户
     *
     * @param users 用户
     */
    fun save(users: Users)

    /**
     * 更新用户
     *
     * @param users 用户
     */
    fun update(users: Users)

    /**
     * 更新注销状态
     *
     * @param ids     ids
     * @param enabled 状态
     */
    fun updateEnabled(ids: List<String>, enabled: Byte?)

    /**
     * 通过id删除
     *
     * @param username 用户账号
     */
    fun deleteById(username: String)

    /**
     * 检验用户所在院校系专业班级是否已被注销
     *
     * @param users 用户信息
     * @return true 被注销 false 未被注销
     */
    fun validSCDSOIsDel(users: Users): Boolean

    /**
     * 根据用户账号查询角色 无缓存
     *
     * @param username 用户账号
     * @return 角色
     */
    fun findByUsernameWithRole(username: String): Result<Record1<String>>

    /**
     * 根据当前用户权限查询低于当前用户权限的用户的 select
     *
     * @return 条件
     */
    fun existsAuthoritiesSelect(): Select<AuthoritiesRecord>

    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): Result<Record8<String, String, String, String, String, Byte, String, java.sql.Date>>?

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): Result<Record8<String, String, String, String, String, Byte, String, java.sql.Date>>?

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
    fun countByConditionExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): Int

    /**
     * 根据条件统计无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 数量
     */
    fun countByConditionNotExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): Int
}