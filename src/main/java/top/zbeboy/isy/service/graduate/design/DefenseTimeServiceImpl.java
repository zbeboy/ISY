package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
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
    public void save(DefenseTime defenseTime) {
        create.insertInto(DEFENSE_TIME)
                .set(DEFENSE_TIME.DEFENSE_ARRANGEMENT_ID, defenseTime.getDefenseArrangementId())
                .set(DEFENSE_TIME.DEFENSE_START_TIME, defenseTime.getDefenseStartTime())
                .set(DEFENSE_TIME.DEFENSE_END_TIME, defenseTime.getDefenseEndTime())
                .set(DEFENSE_TIME.SORT_TIME, defenseTime.getSortTime())
                .execute();
    }
}
