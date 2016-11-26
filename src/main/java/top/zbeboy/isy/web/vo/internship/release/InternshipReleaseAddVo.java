package top.zbeboy.isy.web.vo.internship.release;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-11-12.
 */
public class InternshipReleaseAddVo {
    private String internshipReleaseId;
    @NotNull
    @Size(max = 100)
    private String releaseTitle;
    @NotNull
    @Min(1)
    private int internshipTypeId;
    @NotNull
    private String teacherDistributionTime;
    @NotNull
    private String time;
    private int schoolId;
    private int collegeId;
    @NotNull
    @Min(1)
    private int departmentId;
    @NotNull
    private String grade;
    @NotNull
    private String scienceId;
    private Byte internshipReleaseIsDel;
    private String files;

    public String getInternshipReleaseId() {
        return internshipReleaseId;
    }

    public void setInternshipReleaseId(String internshipReleaseId) {
        this.internshipReleaseId = internshipReleaseId;
    }

    public String getReleaseTitle() {
        return releaseTitle;
    }

    public void setReleaseTitle(String releaseTitle) {
        this.releaseTitle = releaseTitle;
    }

    public int getInternshipTypeId() {
        return internshipTypeId;
    }

    public void setInternshipTypeId(int internshipTypeId) {
        this.internshipTypeId = internshipTypeId;
    }

    public String getTeacherDistributionTime() {
        return teacherDistributionTime;
    }

    public void setTeacherDistributionTime(String teacherDistributionTime) {
        this.teacherDistributionTime = teacherDistributionTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(int collegeId) {
        this.collegeId = collegeId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getScienceId() {
        return scienceId;
    }

    public void setScienceId(String scienceId) {
        this.scienceId = scienceId;
    }

    public Byte getInternshipReleaseIsDel() {
        return internshipReleaseIsDel;
    }

    public void setInternshipReleaseIsDel(Byte internshipReleaseIsDel) {
        this.internshipReleaseIsDel = internshipReleaseIsDel;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }
}
