/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.daos;


import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import top.zbeboy.isy.domain.tables.GraduationDesignPlan;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPlanRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class GraduationDesignPlanDao extends DAOImpl<GraduationDesignPlanRecord, top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan, String> {

    /**
     * Create a new GraduationDesignPlanDao without any configuration
     */
    public GraduationDesignPlanDao() {
        super(GraduationDesignPlan.GRADUATION_DESIGN_PLAN, top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan.class);
    }

    /**
     * Create a new GraduationDesignPlanDao with an attached configuration
     */
    @Autowired
    public GraduationDesignPlanDao(Configuration configuration) {
        super(GraduationDesignPlan.GRADUATION_DESIGN_PLAN, top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan object) {
        return object.getGraduationDesignPlanId();
    }

    /**
     * Fetch records that have <code>graduation_design_plan_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan> fetchByGraduationDesignPlanId(String... values) {
        return fetch(GraduationDesignPlan.GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_PLAN_ID, values);
    }

    /**
     * Fetch a unique record that has <code>graduation_design_plan_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan fetchOneByGraduationDesignPlanId(String value) {
        return fetchOne(GraduationDesignPlan.GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_PLAN_ID, value);
    }

    /**
     * Fetch records that have <code>scheduling IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan> fetchByScheduling(String... values) {
        return fetch(GraduationDesignPlan.GRADUATION_DESIGN_PLAN.SCHEDULING, values);
    }

    /**
     * Fetch records that have <code>supervision_time IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan> fetchBySupervisionTime(String... values) {
        return fetch(GraduationDesignPlan.GRADUATION_DESIGN_PLAN.SUPERVISION_TIME, values);
    }

    /**
     * Fetch records that have <code>guide_location IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan> fetchByGuideLocation(String... values) {
        return fetch(GraduationDesignPlan.GRADUATION_DESIGN_PLAN.GUIDE_LOCATION, values);
    }

    /**
     * Fetch records that have <code>guide_content IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan> fetchByGuideContent(String... values) {
        return fetch(GraduationDesignPlan.GRADUATION_DESIGN_PLAN.GUIDE_CONTENT, values);
    }

    /**
     * Fetch records that have <code>note IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan> fetchByNote(String... values) {
        return fetch(GraduationDesignPlan.GRADUATION_DESIGN_PLAN.NOTE, values);
    }

    /**
     * Fetch records that have <code>graduation_design_teacher_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan> fetchByGraduationDesignTeacherId(String... values) {
        return fetch(GraduationDesignPlan.GRADUATION_DESIGN_PLAN.GRADUATION_DESIGN_TEACHER_ID, values);
    }
}
