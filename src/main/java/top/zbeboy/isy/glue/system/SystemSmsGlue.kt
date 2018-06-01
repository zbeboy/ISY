package top.zbeboy.isy.glue.system

import top.zbeboy.isy.elastic.pojo.SystemSmsElastic
import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.web.bean.system.sms.SystemSmsBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-11-11 .
 **/
interface SystemSmsGlue {

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<SystemSmsBean>): ResultUtils<List<SystemSmsBean>>

    /**
     * 系统日志总数
     *
     * @return 总数
     */
    fun countAll(): Long

    /**
     * 保存
     *
     * @param systemSmsElastic 系统短信
     */
    fun save(systemSmsElastic: SystemSmsElastic)
}