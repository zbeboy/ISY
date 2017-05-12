package top.zbeboy.isy.web.vo.internship.apply;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-12-01.
 */
@Data
public class GraduationPracticeCompanyVo {
    private String graduationPracticeCompanyId;
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
    private String  graduationPracticeCompanyName;
    @NotNull
    @Size(max = 500)
    private String  graduationPracticeCompanyAddress;
    @NotNull
    @Size(max = 10)
    private String  graduationPracticeCompanyContacts;
    @NotNull
    @Size(max = 20)
    private String  graduationPracticeCompanyTel;
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
}
