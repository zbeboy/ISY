package top.zbeboy.isy.service.platform

import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo
import top.zbeboy.isy.domain.tables.records.UsersUniqueInfoRecord

/**
 * Created by zbeboy 2018-01-06 .
 **/
interface UsersUniqueInfoService {
    /**
     * 通过用户账号查询
     *
     * @param username 账号
     * @return 数据
     */
    fun findByUsername(username: String): UsersUniqueInfo

    /**
     * 根据身份证号号查询 注：不等于用户账号
     *
     * @param username 用户账号
     * @param idCard   身份证号
     * @return 数据
     */
    fun findByIdCardNeUsername(username: String, idCard: String): Result<UsersUniqueInfoRecord>

    /**
     * 保存
     *
     * @param usersUniqueInfo 数据
     */
    fun save(usersUniqueInfo: UsersUniqueInfo)

    /**
     * 更新
     *
     * @param usersUniqueInfo 数据
     */
    fun update(usersUniqueInfo: UsersUniqueInfo)

    /**
     * 保存或更新
     *
     * @param usersUniqueInfo 数据
     */
    fun saveOrUpdate(usersUniqueInfo: UsersUniqueInfo)

    /**
     * 通过用户账号删除
     *
     * @param username 账号
     */
    fun deleteByUsername(username: String)
}