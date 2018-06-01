package top.zbeboy.isy.service.system

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.SystemAlert
import top.zbeboy.isy.domain.tables.records.SystemAlertRecord
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.sql.Timestamp
import java.util.*

/**
 * Created by zbeboy 2017-11-07 .
 **/
interface SystemAlertService {

    /**
     * 通过用户账号和主键查询
     *
     * @param username 用户账号
     * @param id   主键
     * @return 提醒内容
     */
    fun findByUsernameAndId(username: String, id: String): Optional<Record>

    /**
     * 通过用户账号，链接id，提醒类型id查询
     */
    fun findByUsernameAndLinkIdAndSystemAlertTypeId(username: String, linkId: String, systemAlertTypeId: Int): Optional<Record>

    /**
     * 系统导航栏提醒显示用
     *
     * @param pageNum  当前页
     * @param pageSize 多少条
     * @param username 用户账号
     * @param isSee    是否已阅
     * @return 数据
     */
    fun findAllByPageForShow(pageNum: Int, pageSize: Int, username: String, isSee: Boolean): Result<Record>

    /**
     * 系统导航栏提醒显示数据
     *
     * @param username 用户账号
     * @param isSee    是否已阅
     * @return 数据
     */
    fun countAllForShow(username: String, isSee: Boolean): Int

    /**
     * 分页查询全部
     *
     * @param paginationUtils 分页工具
     * @param systemAlertBean 额外参数
     * @return 分页数据
     */
    fun findAllByPage(paginationUtils: PaginationUtils, systemAlertBean: SystemAlertBean): Result<Record>

    /**
     * 处理返回数据
     *
     * @param paginationUtils 分页工具
     * @param records         数据
     * @param systemAlertBean 额外参数
     * @return 处理后的数据
     */
    fun dealData(paginationUtils: PaginationUtils, records: Result<Record>, systemAlertBean: SystemAlertBean): List<SystemAlertBean>

    /**
     * 根据条件统计
     *
     * @param paginationUtils 分页工具
     * @param systemAlertBean 额外参数
     * @return 统计
     */
    fun countByCondition(paginationUtils: PaginationUtils, systemAlertBean: SystemAlertBean): Int

    /**
     * 保存提醒
     *
     * @param systemAlert 提醒
     */
    fun save(systemAlert: SystemAlert)

    /**
     * 通过时间删除
     *
     * @param timestamp 时间
     */
    fun deleteByAlertDate(timestamp: Timestamp)

    /**
     * 更新提醒
     *
     * @param systemAlert 提醒
     */
    fun update(systemAlert: SystemAlert)
}