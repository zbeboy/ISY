package top.zbeboy.isy.glue.system

import top.zbeboy.isy.elastic.pojo.SystemLogElastic
import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.web.bean.system.log.SystemLogBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-10-31 .
 **/
interface SystemLogGlue {
    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<SystemLogBean>): ResultUtils<List<SystemLogBean>>

    /**
     * 系统日志总数
     *
     * @return 总数
     */
    fun countAll(): Long

    /**
     * 保存
     *
     * @param systemLogElastic 系统日志
     */
    fun save(systemLogElastic: SystemLogElastic)
}