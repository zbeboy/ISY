/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.daos;


import java.sql.Date;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import top.zbeboy.isy.domain.tables.GraduationPracticeCollege;
import top.zbeboy.isy.domain.tables.records.GraduationPracticeCollegeRecord;


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
public class GraduationPracticeCollegeDao extends DAOImpl<GraduationPracticeCollegeRecord, top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege, String> {

    /**
     * Create a new GraduationPracticeCollegeDao without any configuration
     */
    public GraduationPracticeCollegeDao() {
        super(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE, top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege.class);
    }

    /**
     * Create a new GraduationPracticeCollegeDao with an attached configuration
     */
    @Autowired
    public GraduationPracticeCollegeDao(Configuration configuration) {
        super(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE, top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege object) {
        return object.getGraduationPracticeCollegeId();
    }

    /**
     * Fetch records that have <code>graduation_practice_college_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByGraduationPracticeCollegeId(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>graduation_practice_college_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege fetchOneByGraduationPracticeCollegeId(String value) {
        return fetchOne(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID, value);
    }

    /**
     * Fetch records that have <code>student_name IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByStudentName(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.STUDENT_NAME, values);
    }

    /**
     * Fetch records that have <code>college_class IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByCollegeClass(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.COLLEGE_CLASS, values);
    }

    /**
     * Fetch records that have <code>student_sex IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByStudentSex(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.STUDENT_SEX, values);
    }

    /**
     * Fetch records that have <code>student_number IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByStudentNumber(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.STUDENT_NUMBER, values);
    }

    /**
     * Fetch records that have <code>phone_number IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByPhoneNumber(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.PHONE_NUMBER, values);
    }

    /**
     * Fetch records that have <code>qq_mailbox IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByQqMailbox(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.QQ_MAILBOX, values);
    }

    /**
     * Fetch records that have <code>parental_contact IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByParentalContact(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.PARENTAL_CONTACT, values);
    }

    /**
     * Fetch records that have <code>headmaster IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByHeadmaster(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.HEADMASTER, values);
    }

    /**
     * Fetch records that have <code>headmaster_contact IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByHeadmasterContact(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.HEADMASTER_CONTACT, values);
    }

    /**
     * Fetch records that have <code>graduation_practice_college_name IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByGraduationPracticeCollegeName(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_NAME, values);
    }

    /**
     * Fetch records that have <code>graduation_practice_college_address IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByGraduationPracticeCollegeAddress(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ADDRESS, values);
    }

    /**
     * Fetch records that have <code>graduation_practice_college_contacts IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByGraduationPracticeCollegeContacts(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_CONTACTS, values);
    }

    /**
     * Fetch records that have <code>graduation_practice_college_tel IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByGraduationPracticeCollegeTel(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_TEL, values);
    }

    /**
     * Fetch records that have <code>school_guidance_teacher IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchBySchoolGuidanceTeacher(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.SCHOOL_GUIDANCE_TEACHER, values);
    }

    /**
     * Fetch records that have <code>school_guidance_teacher_tel IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchBySchoolGuidanceTeacherTel(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.SCHOOL_GUIDANCE_TEACHER_TEL, values);
    }

    /**
     * Fetch records that have <code>start_time IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByStartTime(Date... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.START_TIME, values);
    }

    /**
     * Fetch records that have <code>end_time IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByEndTime(Date... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.END_TIME, values);
    }

    /**
     * Fetch records that have <code>commitment_book IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByCommitmentBook(Byte... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.COMMITMENT_BOOK, values);
    }

    /**
     * Fetch records that have <code>safety_responsibility_book IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchBySafetyResponsibilityBook(Byte... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.SAFETY_RESPONSIBILITY_BOOK, values);
    }

    /**
     * Fetch records that have <code>practice_agreement IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByPracticeAgreement(Byte... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.PRACTICE_AGREEMENT, values);
    }

    /**
     * Fetch records that have <code>internship_application IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByInternshipApplication(Byte... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_APPLICATION, values);
    }

    /**
     * Fetch records that have <code>practice_receiving IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByPracticeReceiving(Byte... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.PRACTICE_RECEIVING, values);
    }

    /**
     * Fetch records that have <code>security_education_agreement IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchBySecurityEducationAgreement(Byte... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.SECURITY_EDUCATION_AGREEMENT, values);
    }

    /**
     * Fetch records that have <code>parental_consent IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByParentalConsent(Byte... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.PARENTAL_CONSENT, values);
    }

    /**
     * Fetch records that have <code>student_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByStudentId(Integer... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.STUDENT_ID, values);
    }

    /**
     * Fetch records that have <code>student_username IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByStudentUsername(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.STUDENT_USERNAME, values);
    }

    /**
     * Fetch records that have <code>internship_release_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege> fetchByInternshipReleaseId(String... values) {
        return fetch(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID, values);
    }
}
