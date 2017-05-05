package top.zbeboy.isy.web.vo.graduate.design.release;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/5/5.
 */
public class GraduationDesignReleaseAddVo {
    private String graduationDesignReleaseId;
    @NotNull
    @Size(max = 100)
    private String graduationDesignTitle;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
    @NotNull
    private String fillTeacherTime;
    private Integer schoolId;
    private Integer collegeId;
    @NotNull
    @Min(1)
    private Integer departmentId;
    @NotNull
    private String grade;
    private Byte graduationDesignIsDel;
    @NotNull
    @Min(1)
    private Integer scienceId;
    private String files;

    public String getGraduationDesignReleaseId() {
        return graduationDesignReleaseId;
    }

    public void setGraduationDesignReleaseId(String graduationDesignReleaseId) {
        this.graduationDesignReleaseId = graduationDesignReleaseId;
    }

    public String getGraduationDesignTitle() {
        return graduationDesignTitle;
    }

    public void setGraduationDesignTitle(String graduationDesignTitle) {
        this.graduationDesignTitle = graduationDesignTitle;
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

    public String getFillTeacherTime() {
        return fillTeacherTime;
    }

    public void setFillTeacherTime(String fillTeacherTime) {
        this.fillTeacherTime = fillTeacherTime;
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

    public Byte getGraduationDesignIsDel() {
        return graduationDesignIsDel;
    }

    public void setGraduationDesignIsDel(Byte graduationDesignIsDel) {
        this.graduationDesignIsDel = graduationDesignIsDel;
    }

    public Integer getScienceId() {
        return scienceId;
    }

    public void setScienceId(Integer scienceId) {
        this.scienceId = scienceId;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }
}
