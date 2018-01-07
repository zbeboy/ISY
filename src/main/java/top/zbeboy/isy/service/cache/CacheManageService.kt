package top.zbeboy.isy.service.cache

import top.zbeboy.isy.domain.tables.pojos.*

/**
 * Created by zbeboy 2017-11-07 .
 **/
interface CacheManageService {

    /**
     * 根据用户类型查询id
     *
     * @param usersTypeName 用户类型名
     * @return 用户类型
     */
    fun findByUsersTypeName(usersTypeName: String): UsersType

    /**
     * 根据用户id查询类型
     *
     * @param usersTypeId 用户类型id
     * @return 用户类型
     */
    fun findByUsersTypeId(usersTypeId: Int): UsersType

    /**
     * 通过用户账号查询
     *
     * @param username 账号
     * @return 数据
     */
    fun getUsersKey(username: String): String

    /**
     * 获取角色院id
     *
     * @param users 用户信息
     * @return id
     */
    fun getRoleCollegeId(users: Users): Int

    /**
     * 获取角色系id
     *
     * @param users 用户信息
     * @return id
     */
    fun getRoleDepartmentId(users: Users): Int

    /**
     * 获取当前用户学校路径
     *
     * @param schoolId     学校id
     * @param collegeId    院id
     * @param departmentId 系id
     * @return 路径
     */
    fun schoolInfoPath(schoolId: Int?, collegeId: Int?, departmentId: Int): String

    /**
     * 根据系id生成学校路径
     *
     * @param departmentId 系id
     * @return 路径
     */
    fun schoolInfoPath(departmentId: Int): String

    /**
     * 通过类型名查询
     *
     * @param name 类型名
     * @return 消息类型
     */
    fun findBySystemAlertTypeName(name: String): SystemAlertType

    /**
     * 通过角色id查询出应用id并生成菜单html
     *
     * @param roles 角色
     * @return 菜单html
     */
    fun menuHtml(roles: List<Role>, username: String): String

    /**
     * 通过ids查询
     *
     * @param ids      ids
     * @param username 用户账号 缓存
     * @return 应用
     */
    fun findInIdsWithUsername(ids: List<String>, username: String): List<Application>

    /**
     * 获取该菜单下的data url
     *
     * @param application 菜单
     * @return data url
     */
    fun urlMapping(application: Application): List<String>

    /**
     * 通过角色ids查询 缓存
     *
     * @param roleIds  角色id
     * @param username 用户账号用于缓存
     * @return 数据
     */
    fun findInRoleIdsWithUsername(roleIds: List<String>, username: String): List<RoleApplication>

    /**
     * 根据用户账号查询角色
     *
     * @param username 用户账号
     * @return 角色
     */
    fun findByUsernameWithRole(username: String): List<Role>
}