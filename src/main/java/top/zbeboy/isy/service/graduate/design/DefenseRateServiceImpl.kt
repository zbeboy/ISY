package top.zbeboy.isy.service.graduate.design

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.tables.pojos.DefenseRate
import top.zbeboy.isy.domain.tables.records.DefenseRateRecord
import top.zbeboy.isy.web.bean.graduate.design.reorder.DefenseRateBean
import java.util.ArrayList

import top.zbeboy.isy.domain.Tables.*
/**
 * Created by zbeboy 2018-02-06 .
 **/
@Service("defenseRateService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class DefenseRateServiceImpl @Autowired constructor(dslContext: DSLContext) :DefenseRateService{

    private val create: DSLContext = dslContext

    override fun findByDefenseOrderIdAndGraduationDesignTeacherId(defenseOrderId: String, graduationDesignTeacherId: String): DefenseRateRecord {
        return create.selectFrom<DefenseRateRecord>(DEFENSE_RATE)
                .where(DEFENSE_RATE.DEFENSE_ORDER_ID.eq(defenseOrderId).and(DEFENSE_RATE.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId)))
                .fetchOne()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun saveOrUpdate(defenseRate: DefenseRate) {
        create.insertInto<DefenseRateRecord, String, String, Double>(DEFENSE_RATE, DEFENSE_RATE.DEFENSE_ORDER_ID, DEFENSE_RATE.GRADUATION_DESIGN_TEACHER_ID, DEFENSE_RATE.GRADE)
                .values(defenseRate.defenseOrderId,
                        defenseRate.graduationDesignTeacherId,
                        defenseRate.grade)
                .onDuplicateKeyUpdate()
                .set(DEFENSE_RATE.GRADE, defenseRate.grade)
                .execute()
    }

    override fun findByDefenseOrderIdAndDefenseGroupId(defenseOrderId: String, defenseGroupId: String): List<DefenseRateBean> {
        val defenseRateBeans = ArrayList<DefenseRateBean>()
        val records = create.select()
                .from(DEFENSE_GROUP_MEMBER)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .leftJoin(DEFENSE_RATE)
                .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(DEFENSE_RATE.GRADUATION_DESIGN_TEACHER_ID).and(DEFENSE_RATE.DEFENSE_ORDER_ID.eq(defenseOrderId)))
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .fetch()
        for (r in records) {
            val defenseRateBean = DefenseRateBean()
            defenseRateBean.realName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)
            defenseRateBean.grade = r.getValue(DEFENSE_RATE.GRADE)
            defenseRateBeans.add(defenseRateBean)
        }
        return defenseRateBeans
    }

    override fun deleteByDefenseOrderId(defenseOrderId: String) {
        create.deleteFrom<DefenseRateRecord>(DEFENSE_RATE).where(DEFENSE_RATE.DEFENSE_ORDER_ID.eq(defenseOrderId)).execute()
    }
}