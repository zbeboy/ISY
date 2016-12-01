package top.zbeboy.isy.web.vo.internship.apply;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-12-01.
 */
public class InternshipCompanyVo {
    private String internshipCompanyId;
    @NotNull
    private Integer studentId;
    @NotNull
    @Size(max = 100)
    private String internshipReleaseId;
    @NotNull
    @Size(max = 15)
    private String studentName;
    @NotNull
    @Size(max = 50)
    private String collegeClass;
    @NotNull
    @Size(max = 2)
    private String studentSex;
    @NotNull
    @Size(max = 20)
    private String studentNumber;
    @NotNull
    @Size(max = 15)
    private String phoneNumber;
    @NotNull
    @Size(max = 100)
    private String qqMailbox;
    @NotNull
    @Size(max = 20)
    private String parentalContact;
    @NotNull
    private String headmaster;
    private String headmasterContact;
    @NotNull
    @Size(max = 200)
    private String internshipCompanyName;
    @NotNull
    @Size(max = 500)
    private String internshipCompanyAddress;
    @NotNull
    @Size(max = 10)
    private String internshipCompanyContacts;
    @NotNull
    @Size(max = 20)
    private String internshipCompanyTel;
    @NotNull
    private String schoolGuidanceTeacher;
    private String schoolGuidanceTeacherTel;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
    private Byte commitmentBook;
    private Byte safetyResponsibilityBook;
    private Byte practiceAgreement;
    private Byte internshipApplication;
    private Byte practiceReceiving;
    private Byte securityEducationAgreement;
    private Byte parentalConsent;

    public String getInternshipCompanyId() {
        return internshipCompanyId;
    }

    public void setInternshipCompanyId(String internshipCompanyId) {
        this.internshipCompanyId = internshipCompanyId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getInternshipReleaseId() {
        return internshipReleaseId;
    }

    public void setInternshipReleaseId(String internshipReleaseId) {
        this.internshipReleaseId = internshipReleaseId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCollegeClass() {
        return collegeClass;
    }

    public void setCollegeClass(String collegeClass) {
        this.collegeClass = collegeClass;
    }

    public String getStudentSex() {
        return studentSex;
    }

    public void setStudentSex(String studentSex) {
        this.studentSex = studentSex;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getQqMailbox() {
        return qqMailbox;
    }

    public void setQqMailbox(String qqMailbox) {
        this.qqMailbox = qqMailbox;
    }

    public String getParentalContact() {
        return parentalContact;
    }

    public void setParentalContact(String parentalContact) {
        this.parentalContact = parentalContact;
    }

    public String getHeadmaster() {
        return headmaster;
    }

    public void setHeadmaster(String headmaster) {
        this.headmaster = headmaster;
    }

    public String getHeadmasterContact() {
        return headmasterContact;
    }

    public void setHeadmasterContact(String headmasterContact) {
        this.headmasterContact = headmasterContact;
    }

    public String getInternshipCompanyName() {
        return internshipCompanyName;
    }

    public void setInternshipCompanyName(String internshipCompanyName) {
        this.internshipCompanyName = internshipCompanyName;
    }

    public String getInternshipCompanyAddress() {
        return internshipCompanyAddress;
    }

    public void setInternshipCompanyAddress(String internshipCompanyAddress) {
        this.internshipCompanyAddress = internshipCompanyAddress;
    }

    public String getInternshipCompanyContacts() {
        return internshipCompanyContacts;
    }

    public void setInternshipCompanyContacts(String internshipCompanyContacts) {
        this.internshipCompanyContacts = internshipCompanyContacts;
    }

    public String getInternshipCompanyTel() {
        return internshipCompanyTel;
    }

    public void setInternshipCompanyTel(String internshipCompanyTel) {
        this.internshipCompanyTel = internshipCompanyTel;
    }

    public String getSchoolGuidanceTeacher() {
        return schoolGuidanceTeacher;
    }

    public void setSchoolGuidanceTeacher(String schoolGuidanceTeacher) {
        this.schoolGuidanceTeacher = schoolGuidanceTeacher;
    }

    public String getSchoolGuidanceTeacherTel() {
        return schoolGuidanceTeacherTel;
    }

    public void setSchoolGuidanceTeacherTel(String schoolGuidanceTeacherTel) {
        this.schoolGuidanceTeacherTel = schoolGuidanceTeacherTel;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Byte getCommitmentBook() {
        return commitmentBook;
    }

    public void setCommitmentBook(Byte commitmentBook) {
        this.commitmentBook = commitmentBook;
    }

    public Byte getSafetyResponsibilityBook() {
        return safetyResponsibilityBook;
    }

    public void setSafetyResponsibilityBook(Byte safetyResponsibilityBook) {
        this.safetyResponsibilityBook = safetyResponsibilityBook;
    }

    public Byte getPracticeAgreement() {
        return practiceAgreement;
    }

    public void setPracticeAgreement(Byte practiceAgreement) {
        this.practiceAgreement = practiceAgreement;
    }

    public Byte getInternshipApplication() {
        return internshipApplication;
    }

    public void setInternshipApplication(Byte internshipApplication) {
        this.internshipApplication = internshipApplication;
    }

    public Byte getPracticeReceiving() {
        return practiceReceiving;
    }

    public void setPracticeReceiving(Byte practiceReceiving) {
        this.practiceReceiving = practiceReceiving;
    }

    public Byte getSecurityEducationAgreement() {
        return securityEducationAgreement;
    }

    public void setSecurityEducationAgreement(Byte securityEducationAgreement) {
        this.securityEducationAgreement = securityEducationAgreement;
    }

    public Byte getParentalConsent() {
        return parentalConsent;
    }

    public void setParentalConsent(Byte parentalConsent) {
        this.parentalConsent = parentalConsent;
    }
}
