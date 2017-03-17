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
    private String changeFillStartTime;
    private String changeFillEndTime;
    private String applyTime;
    private Boolean hasController;
    private Byte commitmentBook;
    private Byte safetyResponsibilityBook;
    private Byte practiceAgreement;
    private Byte internshipApplication;
    private Byte practiceReceiving;
    private Byte securityEducationAgreement;
    private Byte parentalConsent;
    private String fileId;
    private String originalFileName;
    private String ext;

    private String fillTime;

    // 只更新实习状态
    private Byte onlyUpdateInternshipApplyState;

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

    public String getChangeFillStartTime() {
        return changeFillStartTime;
    }

    public void setChangeFillStartTime(String changeFillStartTime) {
        this.changeFillStartTime = changeFillStartTime;
    }

    public String getChangeFillEndTime() {
        return changeFillEndTime;
    }

    public void setChangeFillEndTime(String changeFillEndTime) {
        this.changeFillEndTime = changeFillEndTime;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
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

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Byte getOnlyUpdateInternshipApplyState() {
        return onlyUpdateInternshipApplyState;
    }

    public void setOnlyUpdateInternshipApplyState(Byte onlyUpdateInternshipApplyState) {
        this.onlyUpdateInternshipApplyState = onlyUpdateInternshipApplyState;
    }
}
