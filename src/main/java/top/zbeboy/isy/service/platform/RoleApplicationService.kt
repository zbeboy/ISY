package top.zbeboy.isy.service.platform

import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.RoleApplication
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord

/**
 * Created by zbeboy 2017-11-16 .
 **/
interface RoleApplicationService {

    /**
     * 保存
     *
     * @param roleApplication 角色与应用
     */
    fun save(roleApplication: RoleApplication)

    /**
     * 保存
     *
     * @param roleApplication 角色与应用
     */
    fun save(roleApplication: List<RoleApplication>)

    /**
     * 通过应用id删除
     *
     * @param applicationId 应用id
     */
    fun deleteByApplicationId(applicationId: String)

    /**
     * 通过角色id删除
     *
     * @param roleId 角色id
     */
    fun deleteByRoleId(roleId: String)

    /**
     * 根据角色id查询
     *
     * @param roleId 角色id
     * @return 数据
     */
    fun findByRoleId(roleId: String): Result<RoleApplicationRecord>

    /**
     * 批量保存或更新角色
     *
     * @param applicationIds 应用ids
     * @param roleId         角色id
     */
    fun batchSaveRoleApplication(applicationIds: String, roleId: String)
}