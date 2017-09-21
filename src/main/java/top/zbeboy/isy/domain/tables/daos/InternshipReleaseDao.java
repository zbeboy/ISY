/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.daos;


import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import top.zbeboy.isy.domain.tables.InternshipRelease;
import top.zbeboy.isy.domain.tables.records.InternshipReleaseRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class InternshipReleaseDao extends DAOImpl<InternshipReleaseRecord, top.zbeboy.isy.domain.tables.pojos.InternshipRelease, String> {

    /**
     * Create a new InternshipReleaseDao without any configuration
     */
    public InternshipReleaseDao() {
        super(InternshipRelease.INTERNSHIP_RELEASE, top.zbeboy.isy.domain.tables.pojos.InternshipRelease.class);
    }

    /**
     * Create a new InternshipReleaseDao with an attached configuration
     */
    @Autowired
    public InternshipReleaseDao(Configuration configuration) {
        super(InternshipRelease.INTERNSHIP_RELEASE, top.zbeboy.isy.domain.tables.pojos.InternshipRelease.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.InternshipRelease object) {
        return object.getInternshipReleaseId();
    }

    /**
     * Fetch records that have <code>internship_release_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByInternshipReleaseId(String... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>internship_release_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.InternshipRelease fetchOneByInternshipReleaseId(String value) {
        return fetchOne(InternshipRelease.INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID, value);
    }

    /**
     * Fetch records that have <code>internship_title IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByInternshipTitle(String... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.INTERNSHIP_TITLE, values);
    }

    /**
     * Fetch records that have <code>release_time IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByReleaseTime(Timestamp... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.RELEASE_TIME, values);
    }

    /**
     * Fetch records that have <code>username IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByUsername(String... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.USERNAME, values);
    }

    /**
     * Fetch records that have <code>allow_grade IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByAllowGrade(String... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.ALLOW_GRADE, values);
    }

    /**
     * Fetch records that have <code>teacher_distribution_start_time IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByTeacherDistributionStartTime(Timestamp... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.TEACHER_DISTRIBUTION_START_TIME, values);
    }

    /**
     * Fetch records that have <code>teacher_distribution_end_time IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByTeacherDistributionEndTime(Timestamp... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.TEACHER_DISTRIBUTION_END_TIME, values);
    }

    /**
     * Fetch records that have <code>start_time IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByStartTime(Timestamp... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.START_TIME, values);
    }

    /**
     * Fetch records that have <code>end_time IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByEndTime(Timestamp... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.END_TIME, values);
    }

    /**
     * Fetch records that have <code>internship_release_is_del IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByInternshipReleaseIsDel(Byte... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_IS_DEL, values);
    }

    /**
     * Fetch records that have <code>department_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByDepartmentId(Integer... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.DEPARTMENT_ID, values);
    }

    /**
     * Fetch records that have <code>internship_type_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRelease> fetchByInternshipTypeId(Integer... values) {
        return fetch(InternshipRelease.INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID, values);
    }
}
