package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.GraduationDesignPlanDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPlanRecord;

import javax.annotation.Resource;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_PLAN;
import static top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_TUTOR;

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
    public Result<GraduationDesignPlanRecord> findByGraduationDesignTeacherIdOrderByAddTime(String graduationDesignTeacherId) {
        return create.selectFrom(GRADUATION_DESIGN_PLAN)
                .where(GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId))
                .orderBy(GRADUATION_DESIGN_PLAN.ADD_TIME.asc())
                .fetch();
    }
}
