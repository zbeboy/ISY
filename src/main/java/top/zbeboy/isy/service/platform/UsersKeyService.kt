package top.zbeboy.isy.service.platform

import top.zbeboy.isy.domain.tables.pojos.UsersKey

/**
 * Created by zbeboy 2018-01-06 .
 **/
interface UsersKeyService {
    /**
     * 保存
     *
     * @param usersKey 数据
     */
    fun save(usersKey: UsersKey)

    /**
     * 通过用户账号删除
     *
     * @param username 账号
     */
    fun deleteByUsername(username: String)
}