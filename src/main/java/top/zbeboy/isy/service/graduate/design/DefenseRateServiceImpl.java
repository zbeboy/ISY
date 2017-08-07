package top.zbeboy.isy.service.graduate.design;

/**
 * Created by zbeboy on 2017/7/28.
 */

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.DefenseRate;
import top.zbeboy.isy.domain.tables.records.DefenseRateRecord;
import top.zbeboy.isy.web.bean.graduate.design.reorder.DefenseRateBean;

import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

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
    public void saveOrUpdate(DefenseRate defenseRate) {
        create.insertInto(DEFENSE_RATE, DEFENSE_RATE.DEFENSE_ORDER_ID, DEFENSE_RATE.GRADUATION_DESIGN_TEACHER_ID, DEFENSE_RATE.GRADE)
                .values(defenseRate.getDefenseOrderId(),
                        defenseRate.getGraduationDesignTeacherId(),
                        defenseRate.getGrade())
                .onDuplicateKeyUpdate()
                .set(DEFENSE_RATE.GRADE, defenseRate.getGrade())
                .execute();
    }

    @Override
    public List<DefenseRateBean> findByDefenseOrderIdAndDefenseGroupId(String defenseOrderId, String defenseGroupId) {
        List<DefenseRateBean> defenseRateBeans = new ArrayList<>();
        Result<Record> records = create.select()
                .from(DEFENSE_GROUP_MEMBER)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .leftJoin(DEFENSE_RATE)
                .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(DEFENSE_RATE.GRADUATION_DESIGN_TEACHER_ID).and(DEFENSE_RATE.DEFENSE_ORDER_ID.eq(defenseOrderId)))
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .fetch();
        for (Record r : records) {
            DefenseRateBean defenseRateBean = new DefenseRateBean();
            defenseRateBean.setRealName(r.getValue(USERS.REAL_NAME));
            defenseRateBean.setGrade(r.getValue(DEFENSE_RATE.GRADE));
            defenseRateBeans.add(defenseRateBean);
        }
        return defenseRateBeans;
    }
}
