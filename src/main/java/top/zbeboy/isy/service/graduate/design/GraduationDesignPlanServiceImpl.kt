package top.zbeboy.isy.service.graduate.design

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.GraduationDesignPlanDao
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan
import java.sql.Timestamp
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-22 .
 **/
@Service("graduationDesignPlanService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignPlanServiceImpl @Autowired constructor(dslContext: DSLContext) : GraduationDesignPlanService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationDesignPlanDao: GraduationDesignPlanDao

    override fun findById(id: String): GraduationDesignPlan {
        return graduationDesignPlanDao.findById(id)
    }

    override fun findByIdRelation(id: String): Optional<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_PLAN)
                .join(SCHOOLROOM)
                .on(GRADUATION_DESIGN_PLAN.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(BUILDING.BUILDING_ID.eq(SCHOOLROOM.BUILDING_ID))
                .where(GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_PLAN_ID.eq(id))
                .fetchOptional()
    }

    override fun findByGraduationDesignReleaseIdAndStaffIdOrderByAddTime(graduationDesignReleaseId: String, staffId: Int): Result<Record> {
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
                .fetch()
    }

    override fun findByGraduationDesignTeacherIdAndLessThanAddTime(graduationDesignTeacherId: String, addTime: Timestamp): Record? {
        return create.select()
                .from(GRADUATION_DESIGN_PLAN)
                .join(SCHOOLROOM)
                .on(GRADUATION_DESIGN_PLAN.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(BUILDING.BUILDING_ID.eq(SCHOOLROOM.BUILDING_ID))
                .where(GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId)
                        .and(GRADUATION_DESIGN_PLAN.ADD_TIME.lessThan(addTime)))
                .orderBy(GRADUATION_DESIGN_PLAN.ADD_TIME.desc())
                .limit(0, 1)
                .fetchOne()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignPlan: GraduationDesignPlan) {
        graduationDesignPlanDao.insert(graduationDesignPlan)
    }

    override fun update(graduationDesignPlan: GraduationDesignPlan) {
        graduationDesignPlanDao.update(graduationDesignPlan)
    }

    override fun deleteById(id: List<String>) {
        graduationDesignPlanDao.deleteById(id)
    }
}