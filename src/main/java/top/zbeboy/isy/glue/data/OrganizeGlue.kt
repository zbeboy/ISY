package top.zbeboy.isy.glue.data

import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-12-03 .
 **/
interface OrganizeGlue {
    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<OrganizeBean>): ResultUtils<List<OrganizeBean>>

    /**
     * 班级总数
     *
     * @return 总数
     */
    fun countAll(): Long
}