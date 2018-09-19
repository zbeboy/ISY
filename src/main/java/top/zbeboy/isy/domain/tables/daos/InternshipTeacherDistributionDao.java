/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.daos;


import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record3;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import top.zbeboy.isy.domain.tables.InternshipTeacherDistribution;
import top.zbeboy.isy.domain.tables.records.InternshipTeacherDistributionRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class InternshipTeacherDistributionDao extends DAOImpl<InternshipTeacherDistributionRecord, top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution, Record3<Integer, Integer, String>> {

    /**
     * Create a new InternshipTeacherDistributionDao without any configuration
     */
    public InternshipTeacherDistributionDao() {
        super(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION, top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution.class);
    }

    /**
     * Create a new InternshipTeacherDistributionDao with an attached configuration
     */
    @Autowired
    public InternshipTeacherDistributionDao(Configuration configuration) {
        super(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION, top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Record3<Integer, Integer, String> getId(top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution object) {
        return compositeKeyRecord(object.getStaffId(), object.getStudentId(), object.getInternshipReleaseId());
    }

    /**
     * Fetch records that have <code>staff_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution> fetchByStaffId(Integer... values) {
        return fetch(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID, values);
    }

    /**
     * Fetch records that have <code>student_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution> fetchByStudentId(Integer... values) {
        return fetch(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID, values);
    }

    /**
     * Fetch records that have <code>internship_release_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution> fetchByInternshipReleaseId(String... values) {
        return fetch(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID, values);
    }

    /**
     * Fetch records that have <code>username IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution> fetchByUsername(String... values) {
        return fetch(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME, values);
    }

    /**
     * Fetch records that have <code>student_real_name IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution> fetchByStudentRealName(String... values) {
        return fetch(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_REAL_NAME, values);
    }

    /**
     * Fetch records that have <code>assigner IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution> fetchByAssigner(String... values) {
        return fetch(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.ASSIGNER, values);
    }
}
