package top.zbeboy.isy.glue.data

import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-11 .
 **/
interface StudentGlue {
    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): ResultUtils<List<StudentBean>>

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): ResultUtils<List<StudentBean>>

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