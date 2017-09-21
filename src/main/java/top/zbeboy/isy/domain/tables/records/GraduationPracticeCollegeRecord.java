/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import java.sql.Date;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.GraduationPracticeCollege;


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
public class GraduationPracticeCollegeRecord extends UpdatableRecordImpl<GraduationPracticeCollegeRecord> {

    private static final long serialVersionUID = -1635359345;

    /**
     * Setter for <code>isy.graduation_practice_college.graduation_practice_college_id</code>.
     */
    public void setGraduationPracticeCollegeId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.graduation_practice_college_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationPracticeCollegeId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.student_name</code>.
     */
    public void setStudentName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.student_name</code>.
     */
    @NotNull
    @Size(max = 15)
    public String getStudentName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.college_class</code>.
     */
    public void setCollegeClass(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.college_class</code>.
     */
    @NotNull
    @Size(max = 50)
    public String getCollegeClass() {
        return (String) get(2);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.student_sex</code>.
     */
    public void setStudentSex(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.student_sex</code>.
     */
    @NotNull
    @Size(max = 2)
    public String getStudentSex() {
        return (String) get(3);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.student_number</code>.
     */
    public void setStudentNumber(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.student_number</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getStudentNumber() {
        return (String) get(4);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.phone_number</code>.
     */
    public void setPhoneNumber(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.phone_number</code>.
     */
    @NotNull
    @Size(max = 15)
    public String getPhoneNumber() {
        return (String) get(5);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.qq_mailbox</code>.
     */
    public void setQqMailbox(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.qq_mailbox</code>.
     */
    @NotNull
    @Size(max = 100)
    public String getQqMailbox() {
        return (String) get(6);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.parental_contact</code>.
     */
    public void setParentalContact(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.parental_contact</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getParentalContact() {
        return (String) get(7);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.headmaster</code>.
     */
    public void setHeadmaster(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.headmaster</code>.
     */
    @NotNull
    @Size(max = 10)
    public String getHeadmaster() {
        return (String) get(8);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.headmaster_contact</code>.
     */
    public void setHeadmasterContact(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.headmaster_contact</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getHeadmasterContact() {
        return (String) get(9);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.graduation_practice_college_name</code>.
     */
    public void setGraduationPracticeCollegeName(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.graduation_practice_college_name</code>.
     */
    @NotNull
    @Size(max = 200)
    public String getGraduationPracticeCollegeName() {
        return (String) get(10);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.graduation_practice_college_address</code>.
     */
    public void setGraduationPracticeCollegeAddress(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.graduation_practice_college_address</code>.
     */
    @NotNull
    @Size(max = 500)
    public String getGraduationPracticeCollegeAddress() {
        return (String) get(11);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.graduation_practice_college_contacts</code>.
     */
    public void setGraduationPracticeCollegeContacts(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.graduation_practice_college_contacts</code>.
     */
    @NotNull
    @Size(max = 10)
    public String getGraduationPracticeCollegeContacts() {
        return (String) get(12);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.graduation_practice_college_tel</code>.
     */
    public void setGraduationPracticeCollegeTel(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.graduation_practice_college_tel</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getGraduationPracticeCollegeTel() {
        return (String) get(13);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.school_guidance_teacher</code>.
     */
    public void setSchoolGuidanceTeacher(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.school_guidance_teacher</code>.
     */
    @NotNull
    @Size(max = 10)
    public String getSchoolGuidanceTeacher() {
        return (String) get(14);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.school_guidance_teacher_tel</code>.
     */
    public void setSchoolGuidanceTeacherTel(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.school_guidance_teacher_tel</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getSchoolGuidanceTeacherTel() {
        return (String) get(15);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.start_time</code>.
     */
    public void setStartTime(Date value) {
        set(16, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.start_time</code>.
     */
    @NotNull
    public Date getStartTime() {
        return (Date) get(16);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.end_time</code>.
     */
    public void setEndTime(Date value) {
        set(17, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.end_time</code>.
     */
    @NotNull
    public Date getEndTime() {
        return (Date) get(17);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.commitment_book</code>.
     */
    public void setCommitmentBook(Byte value) {
        set(18, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.commitment_book</code>.
     */
    public Byte getCommitmentBook() {
        return (Byte) get(18);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.safety_responsibility_book</code>.
     */
    public void setSafetyResponsibilityBook(Byte value) {
        set(19, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.safety_responsibility_book</code>.
     */
    public Byte getSafetyResponsibilityBook() {
        return (Byte) get(19);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.practice_agreement</code>.
     */
    public void setPracticeAgreement(Byte value) {
        set(20, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.practice_agreement</code>.
     */
    public Byte getPracticeAgreement() {
        return (Byte) get(20);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.internship_application</code>.
     */
    public void setInternshipApplication(Byte value) {
        set(21, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.internship_application</code>.
     */
    public Byte getInternshipApplication() {
        return (Byte) get(21);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.practice_receiving</code>.
     */
    public void setPracticeReceiving(Byte value) {
        set(22, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.practice_receiving</code>.
     */
    public Byte getPracticeReceiving() {
        return (Byte) get(22);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.security_education_agreement</code>.
     */
    public void setSecurityEducationAgreement(Byte value) {
        set(23, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.security_education_agreement</code>.
     */
    public Byte getSecurityEducationAgreement() {
        return (Byte) get(23);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.parental_consent</code>.
     */
    public void setParentalConsent(Byte value) {
        set(24, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.parental_consent</code>.
     */
    public Byte getParentalConsent() {
        return (Byte) get(24);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.student_id</code>.
     */
    public void setStudentId(Integer value) {
        set(25, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.student_id</code>.
     */
    @NotNull
    public Integer getStudentId() {
        return (Integer) get(25);
    }

    /**
     * Setter for <code>isy.graduation_practice_college.internship_release_id</code>.
     */
    public void setInternshipReleaseId(String value) {
        set(26, value);
    }

    /**
     * Getter for <code>isy.graduation_practice_college.internship_release_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getInternshipReleaseId() {
        return (String) get(26);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GraduationPracticeCollegeRecord
     */
    public GraduationPracticeCollegeRecord() {
        super(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE);
    }

    /**
     * Create a detached, initialised GraduationPracticeCollegeRecord
     */
    public GraduationPracticeCollegeRecord(String graduationPracticeCollegeId, String studentName, String collegeClass, String studentSex, String studentNumber, String phoneNumber, String qqMailbox, String parentalContact, String headmaster, String headmasterContact, String graduationPracticeCollegeName, String graduationPracticeCollegeAddress, String graduationPracticeCollegeContacts, String graduationPracticeCollegeTel, String schoolGuidanceTeacher, String schoolGuidanceTeacherTel, Date startTime, Date endTime, Byte commitmentBook, Byte safetyResponsibilityBook, Byte practiceAgreement, Byte internshipApplication, Byte practiceReceiving, Byte securityEducationAgreement, Byte parentalConsent, Integer studentId, String internshipReleaseId) {
        super(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE);

        set(0, graduationPracticeCollegeId);
        set(1, studentName);
        set(2, collegeClass);
        set(3, studentSex);
        set(4, studentNumber);
        set(5, phoneNumber);
        set(6, qqMailbox);
        set(7, parentalContact);
        set(8, headmaster);
        set(9, headmasterContact);
        set(10, graduationPracticeCollegeName);
        set(11, graduationPracticeCollegeAddress);
        set(12, graduationPracticeCollegeContacts);
        set(13, graduationPracticeCollegeTel);
        set(14, schoolGuidanceTeacher);
        set(15, schoolGuidanceTeacherTel);
        set(16, startTime);
        set(17, endTime);
        set(18, commitmentBook);
        set(19, safetyResponsibilityBook);
        set(20, practiceAgreement);
        set(21, internshipApplication);
        set(22, practiceReceiving);
        set(23, securityEducationAgreement);
        set(24, parentalConsent);
        set(25, studentId);
        set(26, internshipReleaseId);
    }
}
