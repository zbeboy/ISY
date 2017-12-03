package top.zbeboy.isy.service.platform

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.Role
import top.zbeboy.isy.domain.tables.records.RoleRecord
import top.zbeboy.isy.web.bean.platform.role.RoleBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2017-11-13 .
 **/
interface RoleService {

    /**
     * 通过角色名查询
     *
     * @param roleEnName 角色名
     * @return 角色
     */
    fun findByRoleEnName(roleEnName: String): Role

    /**
     * 通过角色名与角色类型查询
     *
     * @param roleName 角色名
     * @param roleType 角色类型
     * @return 数据
     */
    fun findByRoleNameAndRoleType(roleName: String, roleType: Int): Result<Record>

    /**
     * 通过角色名与角色类型查询 注：不等于角色id
     *
     * @param roleName 角色名
     * @param roleType 角色类型
     * @param roleId   角色id
     * @return 数据
     */
    fun findByRoleNameAndRoleTypeNeRoleId(roleName: String, roleType: Int, roleId: String): Result<Record>

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 角色
     */
    fun findById(id: String): Role

    /**
     * 根据角色id关联查询
     *
     * @param roleId 角色id
     * @return 关联查询结果
     */
    fun findByRoleIdRelation(roleId: String): Optional<Record>

    /**
     * 通过角色名和院id查询
     *
     * @param roleName  角色名
     * @param collegeId 院id
     * @return 结果集
     */
    fun findByRoleNameAndCollegeId(roleName: String, collegeId: Int): Result<Record>

    /**
     * 通过角色名和院id查询 注不等于角色id
     *
     * @param roleName  角色名
     * @param collegeId 院id
     * @param roleId    角色id
     * @return 结果集
     */
    fun findByRoleNameAndCollegeIdNeRoleId(roleName: String, collegeId: Int, roleId: String): Result<Record>

    /**
     * 保存
     *
     * @param role 角色
     */
    fun save(role: Role)

    /**
     * 更新
     *
     * @param role 角色
     */
    fun update(role: Role)

    /**
     * 通过主键删除
     *
     * @param id 主键
     */
    fun deleteById(id: String)

    /**
     * 批量查询
     *
     * @param ids ids
     * @return roles
     */
    fun findInRoleId(ids: List<String>): Result<RoleRecord>

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<RoleBean>, roleBean: RoleBean): Result<Record>

    /**
     * 角色总数
     *
     * @return 总数
     */
    fun countAll(roleBean: RoleBean): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<RoleBean>, roleBean: RoleBean): Int

    /**
     * 处理角色数据
     *
     * @param records 待处理数据
     * @return 集合数据
     */
    fun dealData(records: Result<Record>?): ArrayList<RoleBean>

    /**
     * 处理单条数据
     *
     * @param records 待处理数据
     * @return 单条数据
     */
    fun dealDataSingle(records: Optional<Record>): RoleBean

    /**
     * 处理角色关联数据
     *
     * @param records 待处理数据
     * @return 集合数据
     */
    fun dealDataRelation(records: Result<Record>?): ArrayList<RoleBean>

    /**
     * 处理角色关联单条数据
     *
     * @param records 待处理数据
     * @return 单条数据
     */
    fun dealDataRelationSingle(records: Optional<Record>): RoleBean

    /**
     * 查询不在院与角色关联表中的角色
     *
     * @param roleName 角色名
     * @return 数据
     */
    fun findByRoleNameNotExistsCollegeRole(roleName: String): Result<RoleRecord>

    /**
     * 查询不在院与角色关联表中的角色 注：不等于角色id
     *
     * @param roleName 角色名
     * @param roleId   角色id
     * @return 结果集
     */
    fun findByRoleNameNotExistsCollegeRoleNeRoleId(roleName: String, roleId: String): Result<RoleRecord>

    /**
     * 检查当前用户是否有此权限
     *
     * @param role 权限
     * @return true 有 false 无
     */
    fun isCurrentUserInRole(role: String): Boolean

    /**
     * 获取角色院id
     *
     * @param record 数据库信息
     * @return id
     */
    fun getRoleCollegeId(record: Optional<Record>): Int

    /**
     * 获取角色系id
     *
     * @param record 数据库信息
     * @return id
     */
    fun getRoleDepartmentId(record: Optional<Record>): Int

    /**
     * 获取设置角色时的角色数据
     *
     * @param username 被设置人的账号
     * @return 角色数据
     */
    fun getRoleData(username: String): ArrayList<Role>
}