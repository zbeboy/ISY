package top.zbeboy.isy.glue.system

import top.zbeboy.isy.elastic.pojo.SystemMailboxElastic
import top.zbeboy.isy.glue.util.ResultUtils
import top.zbeboy.isy.web.bean.system.mailbox.SystemMailboxBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2017-11-11 .
 **/
interface SystemMailboxGlue {

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<SystemMailboxBean>): ResultUtils<List<SystemMailboxBean>>

    /**
     * 系统日志总数
     *
     * @return 总数
     */
    fun countAll(): Long

    /**
     * 保存
     *
     * @param systemMailboxElastic 系统邮件
     */
    fun save(systemMailboxElastic: SystemMailboxElastic)
}