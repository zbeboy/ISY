package top.zbeboy.isy.service.graduate.design

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import top.zbeboy.isy.domain.Tables.DEFENSE_TIME
import top.zbeboy.isy.domain.tables.pojos.DefenseTime
import top.zbeboy.isy.domain.tables.records.DefenseTimeRecord

/**
 * Created by zbeboy 2018-02-06 .
 **/
@Service("defenseTimeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class DefenseTimeServiceImpl @Autowired constructor(dslContext: DSLContext) :DefenseTimeService {

    private val create: DSLContext = dslContext

    override fun findByDefenseArrangementId(defenseArrangementId: String): Result<Record> {
        return create.select()
                .from(DEFENSE_TIME)
                .where(DEFENSE_TIME.DEFENSE_ARRANGEMENT_ID.eq(defenseArrangementId))
                .orderBy(DEFENSE_TIME.SORT_TIME)
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(defenseTime: DefenseTime) {
        create.insertInto<DefenseTimeRecord>(DEFENSE_TIME)
                .set(DEFENSE_TIME.DEFENSE_ARRANGEMENT_ID, defenseTime.defenseArrangementId)
                .set(DEFENSE_TIME.DAY_DEFENSE_START_TIME, defenseTime.dayDefenseStartTime)
                .set(DEFENSE_TIME.DAY_DEFENSE_END_TIME, defenseTime.dayDefenseEndTime)
                .set(DEFENSE_TIME.SORT_TIME, defenseTime.sortTime)
                .execute()
    }

    override fun deleteByDefenseArrangementId(defenseArrangementId: String) {
        create.deleteFrom<DefenseTimeRecord>(DEFENSE_TIME)
                .where(DEFENSE_TIME.DEFENSE_ARRANGEMENT_ID.eq(defenseArrangementId))
                .execute()
    }
}