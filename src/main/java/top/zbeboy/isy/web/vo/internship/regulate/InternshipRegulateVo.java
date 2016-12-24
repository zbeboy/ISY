package top.zbeboy.isy.web.vo.internship.regulate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

/**
 * Created by lenovo on 2016-12-24.
 */
public class InternshipRegulateVo {
    private String internshipRegulateId;
    @NotNull
    @Size(max = 10)
    private String studentName;
    @NotNull
    @Size(max = 20)
    private String studentNumber;
    @NotNull
    @Size(max = 15)
    private String studentTel;
    @NotNull
    @Size(max = 200)
    private String internshipContent;
    @NotNull
    @Size(max = 200)
    private String internshipProgress;
    @NotNull
    @Size(max = 20)
    private String reportWay;
    @NotNull
    private Date reportDate;
    private String schoolGuidanceTeacher;
    private String tliy = "无";
    @NotNull
    private Integer studentId;
    @NotNull
    @Size(max = 64)
    private String internshipReleaseId;
    @NotNull
    private Integer staffId;

    public String getInternshipRegulateId() {
        return internshipRegulateId;
    }

    public void setInternshipRegulateId(String internshipRegulateId) {
        this.internshipRegulateId = internshipRegulateId;
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

    public String getStudentTel() {
        return studentTel;
    }

    public void setStudentTel(String studentTel) {
        this.studentTel = studentTel;
    }

    public String getInternshipContent() {
        return internshipContent;
    }

    public void setInternshipContent(String internshipContent) {
        this.internshipContent = internshipContent;
    }

    public String getInternshipProgress() {
        return internshipProgress;
    }

    public void setInternshipProgress(String internshipProgress) {
        this.internshipProgress = internshipProgress;
    }

    public String getReportWay() {
        return reportWay;
    }

    public void setReportWay(String reportWay) {
        this.reportWay = reportWay;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getSchoolGuidanceTeacher() {
        return schoolGuidanceTeacher;
    }

    public void setSchoolGuidanceTeacher(String schoolGuidanceTeacher) {
        this.schoolGuidanceTeacher = schoolGuidanceTeacher;
    }

    public String getTliy() {
        return tliy;
    }

    public void setTliy(String tliy) {
        this.tliy = tliy;
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

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }
}
