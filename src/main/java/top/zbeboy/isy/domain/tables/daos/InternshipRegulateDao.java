/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.daos;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import top.zbeboy.isy.domain.tables.InternshipRegulate;
import top.zbeboy.isy.domain.tables.records.InternshipRegulateRecord;


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
public class InternshipRegulateDao extends DAOImpl<InternshipRegulateRecord, top.zbeboy.isy.domain.tables.pojos.InternshipRegulate, String> {

    /**
     * Create a new InternshipRegulateDao without any configuration
     */
    public InternshipRegulateDao() {
        super(InternshipRegulate.INTERNSHIP_REGULATE, top.zbeboy.isy.domain.tables.pojos.InternshipRegulate.class);
    }

    /**
     * Create a new InternshipRegulateDao with an attached configuration
     */
    @Autowired
    public InternshipRegulateDao(Configuration configuration) {
        super(InternshipRegulate.INTERNSHIP_REGULATE, top.zbeboy.isy.domain.tables.pojos.InternshipRegulate.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.InternshipRegulate object) {
        return object.getInternshipRegulateId();
    }

    /**
     * Fetch records that have <code>internship_regulate_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByInternshipRegulateId(String... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>internship_regulate_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.InternshipRegulate fetchOneByInternshipRegulateId(String value) {
        return fetchOne(InternshipRegulate.INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID, value);
    }

    /**
     * Fetch records that have <code>student_name IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByStudentName(String... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.STUDENT_NAME, values);
    }

    /**
     * Fetch records that have <code>student_number IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByStudentNumber(String... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.STUDENT_NUMBER, values);
    }

    /**
     * Fetch records that have <code>student_tel IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByStudentTel(String... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.STUDENT_TEL, values);
    }

    /**
     * Fetch records that have <code>internship_content IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByInternshipContent(String... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.INTERNSHIP_CONTENT, values);
    }

    /**
     * Fetch records that have <code>internship_progress IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByInternshipProgress(String... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.INTERNSHIP_PROGRESS, values);
    }

    /**
     * Fetch records that have <code>report_way IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByReportWay(String... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.REPORT_WAY, values);
    }

    /**
     * Fetch records that have <code>report_date IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByReportDate(Date... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.REPORT_DATE, values);
    }

    /**
     * Fetch records that have <code>school_guidance_teacher IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchBySchoolGuidanceTeacher(String... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER, values);
    }

    /**
     * Fetch records that have <code>tliy IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByTliy(String... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.TLIY, values);
    }

    /**
     * Fetch records that have <code>create_date IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByCreateDate(Timestamp... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.CREATE_DATE, values);
    }

    /**
     * Fetch records that have <code>student_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByStudentId(Integer... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.STUDENT_ID, values);
    }

    /**
     * Fetch records that have <code>internship_release_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByInternshipReleaseId(String... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.INTERNSHIP_RELEASE_ID, values);
    }

    /**
     * Fetch records that have <code>staff_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipRegulate> fetchByStaffId(Integer... values) {
        return fetch(InternshipRegulate.INTERNSHIP_REGULATE.STAFF_ID, values);
    }
}
