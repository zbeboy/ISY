package top.zbeboy.isy.glue.data

import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-09 .
 **/
interface StaffGlue {
    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): ResultUtils<List<StaffBean>>

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StaffBean>): ResultUtils<List<StaffBean>>

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