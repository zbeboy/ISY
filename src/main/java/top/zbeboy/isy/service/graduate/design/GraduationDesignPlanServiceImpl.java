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
import java.util.Optional;

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
    public GraduationDesignPlan findById(String id) {
        return graduationDesignPlanDao.findById(id);
    }

    @Override
    public Optional<Record> findByIdRelation(String id) {
        return create.select()
                .from(GRADUATION_DESIGN_PLAN)
                .join(SCHOOLROOM)
                .on(GRADUATION_DESIGN_PLAN.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(BUILDING.BUILDING_ID.eq(SCHOOLROOM.BUILDING_ID))
                .where(GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_PLAN_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Result<Record> findByGraduationDesignReleaseIdAndStaffIdOrderByAddTime(String graduationDesignReleaseId, int staffId) {
        return create.select()
                .from(GRADUATION_DESIGN_PLAN)
                .join(SCHOOLROOM)
                .on(GRADUATION_DESIGN_PLAN.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(BUILDING.BUILDING_ID.eq(SCHOOLROOM.BUILDING_ID))
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)
                .and(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(staffId)))
                .orderBy(GRADUATION_DESIGN_PLAN.ADD_TIME.asc())
                .fetch();
    }

    @Override
    public Record findByGraduationDesignTeacherIdAndLessThanAddTime(String graduationDesignTeacherId, Timestamp addTime) {
        return create.select()
                .from(GRADUATION_DESIGN_PLAN)
                .join(SCHOOLROOM)
                .on(GRADUATION_DESIGN_PLAN.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(BUILDING.BUILDING_ID.eq(SCHOOLROOM.BUILDING_ID))
                .where(GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId)
                .and(GRADUATION_DESIGN_PLAN.ADD_TIME.lessThan(addTime)))
                .orderBy(GRADUATION_DESIGN_PLAN.ADD_TIME.desc())
                .limit(0,1)
                .fetchOne();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignPlan graduationDesignPlan) {
        graduationDesignPlanDao.insert(graduationDesignPlan);
    }

    @Override
    public void update(GraduationDesignPlan graduationDesignPlan) {
        graduationDesignPlanDao.update(graduationDesignPlan);
    }

    @Override
    public void deleteById(List<String> id) {
        graduationDesignPlanDao.deleteById(id);
    }
}
