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
    private Integer internshipTypeId;
    @NotNull
    private String teacherDistributionTime;
    @NotNull
    private String time;
    private Integer schoolId;
    private Integer collegeId;
    @NotNull
    @Min(1)
    private Integer departmentId;
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

    public Integer getInternshipTypeId() {
        return internshipTypeId;
    }

    public void setInternshipTypeId(Integer internshipTypeId) {
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

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public Integer getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
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
