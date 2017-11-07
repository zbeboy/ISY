package top.zbeboy.isy.service.system

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.SystemMessage
import top.zbeboy.isy.web.bean.system.message.SystemMessageBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.sql.Timestamp
import java.util.*

/**
 * Created by zbeboy 2017-11-07 .
 **/
interface SystemMessageService {

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findById(id: String): SystemMessage

    /**
     * 通过id与接收者关联查询
     *
     * @param id         主键
     * @param acceptUser 接收者
     * @return 消息
     */
    fun findByIdAndAcceptUsersRelation(id: String, acceptUser: String): Optional<Record>

    /**
     * 系统导航栏消息显示用
     *
     * @param pageNum  当前页
     * @param pageSize 多少条
     * @param username 用户账号
     * @param isSee    是否已阅
     * @return 数据
     */
    fun findAllByPageForShow(pageNum: Int, pageSize: Int, username: String, isSee: Boolean): Result<Record>

    /**
     * 系统导航栏消息显示数据
     *
     * @param username 用户账号
     * @param isSee    是否已阅
     * @return 数据
     */
    fun countAllForShow(username: String, isSee: Boolean): Int

    /**
     * 分页查询全部
     *
     * @param paginationUtils   分页工具
     * @param systemMessageBean 额外参数
     * @return 分页数据
     */
    fun findAllByPage(paginationUtils: PaginationUtils, systemMessageBean: SystemMessageBean): Result<Record>

    /**
     * 处理返回数据
     *
     * @param paginationUtils   分页工具
     * @param records           数据
     * @param systemMessageBean 额外参数
     * @return 处理后的数据
     */
    fun dealData(paginationUtils: PaginationUtils, records: Result<Record>, systemMessageBean: SystemMessageBean): List<SystemMessageBean>

    /**
     * 根据条件统计
     *
     * @param paginationUtils   分页工具
     * @param systemMessageBean 额外参数
     * @return 统计
     */
    fun countByCondition(paginationUtils: PaginationUtils, systemMessageBean: SystemMessageBean): Int

    /**
     * 保存
     *
     * @param systemMessage 消息
     */
    fun save(systemMessage: SystemMessage)

    /**
     * 通过时间删除
     *
     * @param timestamp 时间
     */
    fun deleteByMessageDate(timestamp: Timestamp)

    /**
     * 更新
     *
     * @param systemMessage 消息
     */
    fun update(systemMessage: SystemMessage)
}