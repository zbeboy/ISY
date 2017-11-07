package top.zbeboy.isy.service.system

import com.alibaba.fastjson.JSON
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.daos.SystemAlertDao
import top.zbeboy.isy.domain.tables.pojos.SystemAlert
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.sql.Timestamp
import java.util.*
import javax.annotation.Resource

import top.zbeboy.isy.domain.Tables.SYSTEM_ALERT
import top.zbeboy.isy.domain.Tables.SYSTEM_ALERT_TYPE
import top.zbeboy.isy.domain.tables.records.SystemAlertRecord

/**
 * Created by zbeboy 2017-11-07 .
 **/
@Service("systemAlertService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class SystemAlertServiceImpl @Autowired constructor(dslContext: DSLContext) : SystemAlertService{

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var systemAlertDao: SystemAlertDao

    override fun findByUsernameAndId(username: String, id: String): Optional<Record> {
        return create.select()
                .from(SYSTEM_ALERT)
                .join(SYSTEM_ALERT_TYPE)
                .on(SYSTEM_ALERT.SYSTEM_ALERT_TYPE_ID.eq(SYSTEM_ALERT_TYPE.SYSTEM_ALERT_TYPE_ID))
                .where(SYSTEM_ALERT.USERNAME.eq(username).and(SYSTEM_ALERT.SYSTEM_ALERT_ID.eq(id)))
                .fetchOptional()
    }

    override fun findByUsernameAndLinkIdAndSystemAlertTypeId(username: String, linkId: String, systemAlertTypeId: Int): Optional<Record> {
        return create.select()
                .from(SYSTEM_ALERT)
                .where(SYSTEM_ALERT.USERNAME.eq(username).and(SYSTEM_ALERT.LINK_ID.eq(linkId)).and(SYSTEM_ALERT.SYSTEM_ALERT_TYPE_ID.eq(systemAlertTypeId)))
                .fetchOptional()
    }

    override fun findAllByPageForShow(pageNum: Int, pageSize: Int, username: String, isSee: Boolean): Result<Record> {
        var b: Byte? = 0
        if (isSee) {
            b = 1
        }
        return create.select()
                .from(SYSTEM_ALERT)
                .join(SYSTEM_ALERT_TYPE)
                .on(SYSTEM_ALERT.SYSTEM_ALERT_TYPE_ID.eq(SYSTEM_ALERT_TYPE.SYSTEM_ALERT_TYPE_ID))
                .where(SYSTEM_ALERT.USERNAME.eq(username).and(SYSTEM_ALERT.IS_SEE.eq(b)))
                .orderBy(SYSTEM_ALERT.ALERT_DATE.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch()
    }

    override fun countAllForShow(username: String, isSee: Boolean): Int {
        var b: Byte? = 0
        if (isSee) {
            b = 1
        }
        val record = create.selectCount()
                .from(SYSTEM_ALERT)
                .where(SYSTEM_ALERT.USERNAME.eq(username).and(SYSTEM_ALERT.IS_SEE.eq(b)))
                .fetchOne()
        return record.value1()
    }

    override fun findAllByPage(paginationUtils: PaginationUtils, systemAlertBean: SystemAlertBean): Result<Record> {
        val pageNum = paginationUtils.pageNum
        val pageSize = paginationUtils.pageSize
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, systemAlertBean)
        return create.select()
                .from(SYSTEM_ALERT)
                .join(SYSTEM_ALERT_TYPE)
                .on(SYSTEM_ALERT.SYSTEM_ALERT_TYPE_ID.eq(SYSTEM_ALERT_TYPE.SYSTEM_ALERT_TYPE_ID))
                .where(a)
                .orderBy(SYSTEM_ALERT.ALERT_DATE.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch()
    }

    override fun dealData(paginationUtils: PaginationUtils, records: Result<Record>, systemAlertBean: SystemAlertBean): List<SystemAlertBean> {
        var systemAlertBeens: List<SystemAlertBean> = ArrayList()
        if (records.isNotEmpty) {
            systemAlertBeens = records.into(SystemAlertBean::class.java)
            systemAlertBeens.forEach { i -> i.alertDateStr = DateTimeUtils.formatDate(i.alertDate, "yyyy年MM月dd日 HH:mm:ss") }
            paginationUtils.totalDatas = countByCondition(paginationUtils, systemAlertBean)
        }
        return systemAlertBeens
    }

    override fun countByCondition(paginationUtils: PaginationUtils, systemAlertBean: SystemAlertBean): Int {
        val count: Record1<Int>
        var a = searchCondition(paginationUtils)
        a = otherCondition(a, systemAlertBean)
        count = if (ObjectUtils.isEmpty(a)) {
            val selectJoinStep = create.selectCount()
                    .from(SYSTEM_ALERT)
            selectJoinStep.fetchOne()
        } else {
            val selectConditionStep = create.selectCount()
                    .from(SYSTEM_ALERT)
                    .join(SYSTEM_ALERT_TYPE)
                    .on(SYSTEM_ALERT.SYSTEM_ALERT_TYPE_ID.eq(SYSTEM_ALERT_TYPE.SYSTEM_ALERT_TYPE_ID))
                    .where(a)
            selectConditionStep.fetchOne()
        }
        return count.value1()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(systemAlert: SystemAlert) {
        systemAlertDao.insert(systemAlert)
    }

    override fun deleteByAlertDate(timestamp: Timestamp) {
        create.deleteFrom(SYSTEM_ALERT).where(SYSTEM_ALERT.ALERT_DATE.le(timestamp)).execute()
    }

    override fun update(systemAlert: SystemAlert) {
        systemAlertDao.update(systemAlert)
    }

    /**
     * 搜索条件
     *
     * @param paginationUtils 分页工具
     * @return 条件
     */
    fun searchCondition(paginationUtils: PaginationUtils): Condition? {
        var a: Condition? = null
        val search = JSON.parseObject(paginationUtils.searchParams)
        if (!ObjectUtils.isEmpty(search)) {
            val alertContent = StringUtils.trimWhitespace(search.getString("alertContent"))
            if (StringUtils.hasLength(alertContent)) {
                a = SYSTEM_ALERT.ALERT_CONTENT.like(SQLQueryUtils.likeAllParam(alertContent))
            }
        }
        return a
    }

    /**
     * 其它条件参数
     *
     * @param a               搜索条件
     * @param systemAlertBean 额外参数
     * @return 条件
     */
    private fun otherCondition(a: Condition?, systemAlertBean: SystemAlertBean): Condition? {
        var condition = a
        if (!ObjectUtils.isEmpty(systemAlertBean)) {
            if (StringUtils.hasLength(systemAlertBean.username)) {
                condition = if (!ObjectUtils.isEmpty(condition)) {
                    condition!!.and(SYSTEM_ALERT.USERNAME.eq(systemAlertBean.username))
                } else {
                    SYSTEM_ALERT.USERNAME.eq(systemAlertBean.username)
                }
            }
        }
        return condition
    }
}