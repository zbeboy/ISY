package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.GraduationDesignPlanDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPlanRecord;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zbeboy on 2017/5/27.
 */
@Slf4j
@Service("graduationDesignPlanService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignPlanServiceImpl implements GraduationDesignPlanService {

    private final DSLContext create;

    @Resource
    private GraduationDesignPlanDao graduationDesignPlanDao;

    @Autowired
    public GraduationDesignPlanServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }


    @Override
    public Result<Record> findByGraduationDesignTeacherIdOrderByAddTime(String graduationDesignTeacherId) {
        return create.select()
                .from(GRADUATION_DESIGN_PLAN)
                .join(SCHOOLROOM)
                .on(GRADUATION_DESIGN_PLAN.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(BUILDING.BUILDING_ID.eq(SCHOOLROOM.BUILDING_ID))
                .where(GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId))
                .orderBy(GRADUATION_DESIGN_PLAN.ADD_TIME.asc())
                .fetch();
    }

    @Override
    public Record findByGraduationDesignTeacherIdAndLeAddTime(String graduationDesignTeacherId, Timestamp addTime) {
        return create.select()
                .from(GRADUATION_DESIGN_PLAN)
                .join(SCHOOLROOM)
                .on(GRADUATION_DESIGN_PLAN.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(BUILDING.BUILDING_ID.eq(SCHOOLROOM.BUILDING_ID))
                .where(GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId)
                .and(GRADUATION_DESIGN_PLAN.ADD_TIME.lessThan(addTime)))
                .fetchOne();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignPlan graduationDesignPlan) {
        graduationDesignPlanDao.insert(graduationDesignPlan);
    }
}
