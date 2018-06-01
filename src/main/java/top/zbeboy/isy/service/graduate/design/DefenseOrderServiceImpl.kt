package top.zbeboy.isy.service.graduate.design

import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.DefenseOrderDao
import top.zbeboy.isy.domain.tables.pojos.DefenseOrder
import top.zbeboy.isy.domain.tables.records.DefenseOrderRecord
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseOrderBean
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-02-06 .
 **/
@Service("defenseOrderService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class DefenseOrderServiceImpl @Autowired constructor(dslContext: DSLContext) : DefenseOrderService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var defenseOrderDao: DefenseOrderDao

    override fun findById(id: String): DefenseOrder {
        return defenseOrderDao.findById(id)
    }

    override fun findByDefenseGroupId(defenseGroupId: String): List<DefenseOrder> {
        return defenseOrderDao.fetchByDefenseGroupId(defenseGroupId)
    }

    override fun findAll(condition: DefenseOrderBean): Result<Record> {
        var a = searchCondition(condition)
        a = otherCondition(a, condition)
        return if (ObjectUtils.isEmpty(a)) {
            create.select()
                    .from(DEFENSE_ORDER)
                    .join(DEFENSE_GROUP)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID))
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .orderBy(DEFENSE_ORDER.SORT_NUM)
                    .fetch()
        } else {
            create.select()
                    .from(DEFENSE_ORDER)
                    .join(DEFENSE_GROUP)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID))
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .where(a)
                    .orderBy(DEFENSE_ORDER.SORT_NUM)
                    .fetch()
        }
    }

    override fun findBySortNumAndDefenseGroupId(sortNum: Int, defenseGroupId: String): DefenseOrderRecord {
        return create.selectFrom(DEFENSE_ORDER)
                .where(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(defenseGroupId).and(DEFENSE_ORDER.SORT_NUM.eq(sortNum)))
                .fetchOne()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(defenseOrders: List<DefenseOrder>) {
        defenseOrderDao.insert(defenseOrders)
    }

    override fun deleteByDefenseGroupId(defenseGroupId: String) {
        create.deleteFrom(DEFENSE_ORDER)
                .where(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .execute()
    }

    override fun update(defenseOrder: DefenseOrder) {
        defenseOrderDao.update(defenseOrder)
    }

    /**
     * 搜索条件
     *
     * @param condition 条件
     * @return 条件
     */
    fun searchCondition(condition: DefenseOrderBean): Condition? {
        var a: Condition? = null
        val studentName = StringUtils.trimWhitespace(condition.studentName)
        val studentNumber = StringUtils.trimWhitespace(condition.studentNumber)
        val scoreTypeId = if (condition.scoreTypeId == null) 0 else condition.scoreTypeId
        val defenseStatus = if (condition.defenseStatus == null) -1 else condition.defenseStatus
        if (StringUtils.hasLength(studentName)) {
            a = DEFENSE_ORDER.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName))
        }

        if (StringUtils.hasLength(studentNumber)) {
            a = if (!ObjectUtils.isEmpty(a)) {
                a!!.and(DEFENSE_ORDER.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
            } else {
                DEFENSE_ORDER.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
            }
        }

        if (scoreTypeId > 0) {
            a = if (!ObjectUtils.isEmpty(a)) {
                a!!.and(DEFENSE_ORDER.SCORE_TYPE_ID.eq(scoreTypeId))
            } else {
                DEFENSE_ORDER.SCORE_TYPE_ID.eq(scoreTypeId)
            }
        }

        if (defenseStatus > 0) {
            a = if (!ObjectUtils.isEmpty(a)) {
                a!!.and(DEFENSE_ORDER.DEFENSE_STATUS.eq(defenseStatus))
            } else {
                DEFENSE_ORDER.DEFENSE_STATUS.eq(defenseStatus)
            }
        }
        return a
    }

    /**
     * 其它条件参数
     *
     * @param a                搜索条件
     * @param defenseOrderBean 额外参数
     */
    private fun otherCondition(a: Condition?, defenseOrderBean: DefenseOrderBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(defenseOrderBean)) {
            if (StringUtils.hasLength(defenseOrderBean.defenseGroupId)) {
                if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition = tempCondition!!.and(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(defenseOrderBean.defenseGroupId))
                } else {
                    tempCondition = DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(defenseOrderBean.defenseGroupId)
                }
            }
        }
        return tempCondition
    }
}