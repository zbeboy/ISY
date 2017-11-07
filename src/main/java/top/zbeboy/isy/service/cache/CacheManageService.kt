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