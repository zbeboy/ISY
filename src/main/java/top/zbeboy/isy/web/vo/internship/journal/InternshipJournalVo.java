package top.zbeboy.isy.web.vo.internship.journal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by zbeboy on 2016/12/16.
 */
public class InternshipJournalVo {
    private String internshipJournalId;
    @NotNull
    @Size(max = 10)
    private String studentName;
    @NotNull
    @Size(max = 20)
    private String studentNumber;
    @NotNull
    @Size(max = 10)
    private String organize;
    @NotNull
    @Size(max = 10)
    private String schoolGuidanceTeacher;
    @NotNull
    @Size(max = 200)
    private String graduationPracticeCompanyName;
    @NotNull
    @Size(max = 65535)
    private String internshipJournalContent;
    @NotNull
    @Size(max = 65535)
    private String internshipJournalHtml;
    @NotNull
    private Date internshipJournalDate;
    private Timestamp createDate;
    @NotNull
    private Integer studentId;
    @NotNull
    private Integer staffId;
    @NotNull
    private String internshipReleaseId;
    private String internshipJournalWord;
    private Byte isSeeStaff;

    public String getInternshipJournalId() {
        return internshipJournalId;
    }

    public void setInternshipJournalId(String internshipJournalId) {
        this.internshipJournalId = internshipJournalId;
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

    public String getOrganize() {
        return organize;
    }

    public void setOrganize(String organize) {
        this.organize = organize;
    }

    public String getSchoolGuidanceTeacher() {
        return schoolGuidanceTeacher;
    }

    public void setSchoolGuidanceTeacher(String schoolGuidanceTeacher) {
        this.schoolGuidanceTeacher = schoolGuidanceTeacher;
    }

    public String getGraduationPracticeCompanyName() {
        return graduationPracticeCompanyName;
    }

    public void setGraduationPracticeCompanyName(String graduationPracticeCompanyName) {
        this.graduationPracticeCompanyName = graduationPracticeCompanyName;
    }

    public String getInternshipJournalContent() {
        return internshipJournalContent;
    }

    public void setInternshipJournalContent(String internshipJournalContent) {
        this.internshipJournalContent = internshipJournalContent;
    }

    public String getInternshipJournalHtml() {
        return internshipJournalHtml;
    }

    public void setInternshipJournalHtml(String internshipJournalHtml) {
        this.internshipJournalHtml = internshipJournalHtml;
    }

    public Date getInternshipJournalDate() {
        return internshipJournalDate;
    }

    public void setInternshipJournalDate(Date internshipJournalDate) {
        this.internshipJournalDate = internshipJournalDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getInternshipReleaseId() {
        return internshipReleaseId;
    }

    public void setInternshipReleaseId(String internshipReleaseId) {
        this.internshipReleaseId = internshipReleaseId;
    }

    public String getInternshipJournalWord() {
        return internshipJournalWord;
    }

    public void setInternshipJournalWord(String internshipJournalWord) {
        this.internshipJournalWord = internshipJournalWord;
    }

    public Byte getIsSeeStaff() {
        return isSeeStaff;
    }

    public void setIsSeeStaff(Byte isSeeStaff) {
        this.isSeeStaff = isSeeStaff;
    }
}
