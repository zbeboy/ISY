package top.zbeboy.isy.web.bean.internship.review;

import lombok.Data;

/**
 * Created by zbeboy on 2016/12/6.
 */
@Data
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
}
