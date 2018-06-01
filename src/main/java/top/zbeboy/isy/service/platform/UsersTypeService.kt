package top.zbeboy.isy.service.platform

import org.jooq.Result
import top.zbeboy.isy.domain.tables.records.UsersTypeRecord

/**
 * Created by zbeboy 2017-11-16 .
 **/
interface UsersTypeService {

    /**
     * 查询全部
     *
     * @return 用户类型
     */
    fun findAll(): Result<UsersTypeRecord>

    /**
     * 当前用户类型
     *
     * @param usersTypeName 用户类型名
     * @return true or false
     */
    fun isCurrentUsersTypeName(usersTypeName: String): Boolean
}