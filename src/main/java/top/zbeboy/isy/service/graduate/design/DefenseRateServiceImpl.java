package top.zbeboy.isy.service.graduate.design;

/**
 * Created by zbeboy on 2017/7/28.
 */

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.DefenseRate;
import top.zbeboy.isy.domain.tables.records.DefenseRateRecord;

import static top.zbeboy.isy.domain.Tables.DEFENSE_RATE;

@Slf4j
@Service("defenseRateService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DefenseRateServiceImpl implements DefenseRateService {

    private final DSLContext create;

    @Autowired
    public DefenseRateServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public DefenseRateRecord findByDefenseOrderIdAndGraduationDesignTeacherId(String defenseOrderId, String graduationDesignTeacherId) {
        return create.selectFrom(DEFENSE_RATE)
                .where(DEFENSE_RATE.DEFENSE_ORDER_ID.eq(defenseOrderId).and(DEFENSE_RATE.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId)))
                .fetchOne();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(DefenseRate defenseRate) {
        create.insertInto(DEFENSE_RATE, DEFENSE_RATE.DEFENSE_ORDER_ID, DEFENSE_RATE.GRADUATION_DESIGN_TEACHER_ID, DEFENSE_RATE.GRADE)
                .values(defenseRate.getDefenseOrderId(),
                        defenseRate.getGraduationDesignTeacherId(),
                        defenseRate.getGrade())
                .onDuplicateKeyUpdate()
                .set(DEFENSE_RATE.GRADE, defenseRate.getGrade())
                .execute();
    }
}
