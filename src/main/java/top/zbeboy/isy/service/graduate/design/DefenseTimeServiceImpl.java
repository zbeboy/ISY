package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.DefenseTime;

import static top.zbeboy.isy.domain.Tables.DEFENSE_TIME;

/**
 * Created by lenovo on 2017-07-09.
 */
@Slf4j
@Service("defenseTimeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DefenseTimeServiceImpl implements DefenseTimeService {

    private final DSLContext create;

    @Autowired
    public DefenseTimeServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<Record> findByDefenseArrangementId(String defenseArrangementId) {
        return create.select()
                .from(DEFENSE_TIME)
                .where(DEFENSE_TIME.DEFENSE_ARRANGEMENT_ID.eq(defenseArrangementId))
                .orderBy(DEFENSE_TIME.SORT_TIME)
                .fetch();
    }

    @Override
    public void save(DefenseTime defenseTime) {
        create.insertInto(DEFENSE_TIME)
                .set(DEFENSE_TIME.DEFENSE_ARRANGEMENT_ID, defenseTime.getDefenseArrangementId())
                .set(DEFENSE_TIME.DAY_DEFENSE_START_TIME, defenseTime.getDayDefenseStartTime())
                .set(DEFENSE_TIME.DAY_DEFENSE_END_TIME, defenseTime.getDayDefenseEndTime())
                .set(DEFENSE_TIME.SORT_TIME, defenseTime.getSortTime())
                .execute();
    }

    @Override
    public void deleteByDefenseArrangementId(String defenseArrangementId) {
        create.deleteFrom(DEFENSE_TIME)
                .where(DEFENSE_TIME.DEFENSE_ARRANGEMENT_ID.eq(defenseArrangementId))
                .execute();
    }
}
