package top.zbeboy.isy.service.system

import com.alibaba.fastjson.JSON
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.SYSTEM_MESSAGE
import top.zbeboy.isy.domain.Tables.USERS
import top.zbeboy.isy.domain.tables.daos.SystemMessageDao
import top.zbeboy.isy.domain.tables.pojos.SystemMessage
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.system.message.SystemMessageBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.sql.Timestamp
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-07 .
 **/
@Service("systemMessageService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class SystemMessageServiceImpl @Autowired constructor(dslContext: DSLContext) : SystemMessageService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var systemMessageDao: SystemMessageDao

    override fun findById(id: String): SystemMessage {
        return systemMessageDao.findById(id)
    }

    override fun findByIdAndAcceptUsersRelation(id: String, acceptUser: String): Optional<Record> {
        return create.select()
                .from(SYSTEM_MESSAGE)
                .join(USERS)
                .on(SYSTEM_MESSAGE.SEND_USERS.eq(USERS.USERNAME))
                .where(SYSTEM_MESSAGE.SYSTEM_MESSAGE_ID.eq(id).and(SYSTEM_MESSAGE.ACCEPT_USERS.eq(acceptUser)))
                .fetchOptional()
    }

    override fun findAllByPageForShow(pageNum: Int, pageSize: Int, username: String, isSee: Boolean): Result<Record> {
        var b: Byte? = 0
        if (isSee) {
            b = 1
        }
        return create.select()
                .from(SYSTEM_MESSAGE)
                .join(USERS)
                .on(SYSTEM_MESSAGE.SEND_USERS.eq(USERS.USERNAME))
                .where(SYSTEM_MESSAGE.ACCEPT_USERS.eq(username).and(SYSTEM_MESSAGE.IS_SEE.eq(b)))
                .orderBy(SYSTEM_MESSAGE.MESSAGE_DATE.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch()
    }

    override fun countAllForShow(username: String, isSee: Boolean): Int {
        var b: Byte? = 0
        if (isSee) {
            b = 1
        }
        val record = create.selectCount()
                .from(SYSTEM_MESSAGE)
                .where(SYSTEM_MESSAGE.ACCEPT_USERS.eq(username).and(SYSTEM_MESSAGE.IS_SEE.eq(b)))
                .fetchOne()
        return record.value1()
    }

    override fun findAllByPage(paginationUtils: PaginationUtils, systemMessageBean: SystemMessageBean): Result<Record> {
        val pageNum = paginationUtils.getPageNum()
        val pageSize = paginationUtils.getPageSize()
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, systemMessageBean)
        return create.select()
                .from(SYSTEM_MESSAGE)
                .join(USERS)
                .on(SYSTEM_MESSAGE.SEND_USERS.eq(USERS.USERNAME))
                .where(a)
                .orderBy(SYSTEM_MESSAGE.MESSAGE_DATE.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch()
    }

    override fun dealData(paginationUtils: PaginationUtils, records: Result<Record>, systemMessageBean: SystemMessageBean): List<SystemMessageBean> {
        var systemMessageBeens: List<SystemMessageBean> = ArrayList()
        if (records.isNotEmpty) {
            systemMessageBeens = records.into(SystemMessageBean::class.java)
            systemMessageBeens.forEach { i -> i.messageDateStr = DateTimeUtils.formatDate(i.messageDate, "yyyy年MM月dd日 HH:mm:ss") }
            paginationUtils.setTotalDatas(countByCondition(paginationUtils, systemMessageBean))
        }
        return systemMessageBeens
    }

    override fun countByCondition(paginationUtils: PaginationUtils, systemMessageBean: SystemMessageBean): Int {
        val count: Record1<Int>
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, systemMessageBean)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(SYSTEM_MESSAGE)
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(SYSTEM_MESSAGE)
                    .join(USERS)
                    .on(SYSTEM_MESSAGE.SEND_USERS.eq(USERS.USERNAME))
                    .where(a)
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(systemMessage: SystemMessage) {
        systemMessageDao.insert(systemMessage)
    }

    override fun deleteByMessageDate(timestamp: Timestamp) {
        create.deleteFrom(SYSTEM_MESSAGE).where(SYSTEM_MESSAGE.MESSAGE_DATE.le(timestamp)).execute()
    }

    override fun update(systemMessage: SystemMessage) {
        systemMessageDao.update(systemMessage)
    }

    /**
     * 搜索条件
     *
     * @param paginationUtils 分页工具
     * @return 条件
     */
    fun searchCondition(paginationUtils: PaginationUtils): Condition? {
        var a: Condition? = null
        val search = JSON.parseObject(paginationUtils.getSearchParams())
        if (!ObjectUtils.isEmpty(search)) {
            val messageTitle = StringUtils.trimWhitespace(search.getString("messageTitle"))
            if (StringUtils.hasLength(messageTitle)) {
                a = SYSTEM_MESSAGE.MESSAGE_TITLE.like(SQLQueryUtils.likeAllParam(messageTitle))
            }
        }
        return a
    }

    /**
     * 其它条件参数
     *
     * @param a                 搜索条件
     * @param systemMessageBean 额外参数
     * @return 条件
     */
    private fun otherCondition(a: Condition?, systemMessageBean: SystemMessageBean): Condition? {
        var condition = a
        if (!ObjectUtils.isEmpty(systemMessageBean)) {
            if (StringUtils.hasLength(systemMessageBean.acceptUsers)) {
                condition = if (!ObjectUtils.isEmpty(condition)) {
                    condition!!.and(SYSTEM_MESSAGE.ACCEPT_USERS.eq(systemMessageBean.acceptUsers))
                } else {
                    SYSTEM_MESSAGE.ACCEPT_USERS.eq(systemMessageBean.acceptUsers)
                }
            }
        }
        return condition
    }
}