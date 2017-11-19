package top.zbeboy.isy.glue.platform

import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.web.bean.platform.users.UsersBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-11-19 .
 **/
interface UsersGlue {
    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): ResultUtils<List<UsersBean>>

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<UsersBean>): ResultUtils<List<UsersBean>>

    /**
     * 统计有权限的用户
     *
     * @return 总数
     */
    fun countAllExistsAuthorities(): Long

    /**
     * 统计无权限的用户
     *
     * @return 总数
     */
    fun countAllNotExistsAuthorities(): Long
}