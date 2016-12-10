package top.zbeboy.isy.web.bean.internship.review;

import java.sql.Timestamp;

/**
 * Created by zbeboy on 2016/12/6.
 */
public class InternshipReviewBean {
    private int studentId;
    private String internshipReleaseId;
    private int internshipTypeId;
    private String realName;
    private String studentName;
    private String studentNumber;
    private String scienceName;
    private String organizeName;
    private String reason;
    private int internshipApplyState;
    private Timestamp changeFillStartTime;
    private Timestamp changeFillEndTime;
    private Timestamp applyTime;
    private Boolean hasController;
    private Byte commitmentBook;
    private Byte safetyResponsibilityBook;
    private Byte practiceAgreement;
    private Byte internshipApplication;
    private Byte practiceReceiving;
    private Byte securityEducationAgreement;
    private Byte parentalConsent;

    private String fillTime;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getInternshipReleaseId() {
        return internshipReleaseId;
    }

    public void setInternshipReleaseId(String internshipReleaseId) {
        this.internshipReleaseId = internshipReleaseId;
    }

    public int getInternshipTypeId() {
        return internshipTypeId;
    }

    public void setInternshipTypeId(int internshipTypeId) {
        this.internshipTypeId = internshipTypeId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getScienceName() {
        return scienceName;
    }

    public void setScienceName(String scienceName) {
        this.scienceName = scienceName;
    }

    public String getOrganizeName() {
        return organizeName;
    }

    public void setOrganizeName(String organizeName) {
        this.organizeName = organizeName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getInternshipApplyState() {
        return internshipApplyState;
    }

    public void setInternshipApplyState(int internshipApplyState) {
        this.internshipApplyState = internshipApplyState;
    }

    public Timestamp getChangeFillStartTime() {
        return changeFillStartTime;
    }

    public void setChangeFillStartTime(Timestamp changeFillStartTime) {
        this.changeFillStartTime = changeFillStartTime;
    }

    public Timestamp getChangeFillEndTime() {
        return changeFillEndTime;
    }

    public void setChangeFillEndTime(Timestamp changeFillEndTime) {
        this.changeFillEndTime = changeFillEndTime;
    }

    public Timestamp getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Timestamp applyTime) {
        this.applyTime = applyTime;
    }

    public Boolean getHasController() {
        return hasController;
    }

    public void setHasController(Boolean hasController) {
        this.hasController = hasController;
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

    public String getFillTime() {
        return fillTime;
    }

    public void setFillTime(String fillTime) {
        this.fillTime = fillTime;
    }
}
