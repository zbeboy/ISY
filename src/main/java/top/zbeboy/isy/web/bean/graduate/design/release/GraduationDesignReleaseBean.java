package top.zbeboy.isy.web.bean.graduate.design.release;

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;

/**
 * Created by zbeboy on 2017/5/5.
 */
public class GraduationDesignReleaseBean extends GraduationDesignRelease {
    private String realName;
    private String departmentName;
    private String schoolName;
    private String collegeName;
    private String scienceName;
    private int schoolId;
    private int collegeId;
    private String fillTeacherStartTimeStr;
    private String fillTeacherEndTimeStr;
    private String startTimeStr;
    private String endTimeStr;
    private String releaseTimeStr;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getScienceName() {
        return scienceName;
    }

    public void setScienceName(String scienceName) {
        this.scienceName = scienceName;
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

    public String getFillTeacherStartTimeStr() {
        return fillTeacherStartTimeStr;
    }

    public void setFillTeacherStartTimeStr(String fillTeacherStartTimeStr) {
        this.fillTeacherStartTimeStr = fillTeacherStartTimeStr;
    }

    public String getFillTeacherEndTimeStr() {
        return fillTeacherEndTimeStr;
    }

    public void setFillTeacherEndTimeStr(String fillTeacherEndTimeStr) {
        this.fillTeacherEndTimeStr = fillTeacherEndTimeStr;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public String getReleaseTimeStr() {
        return releaseTimeStr;
    }

    public void setReleaseTimeStr(String releaseTimeStr) {
        this.releaseTimeStr = releaseTimeStr;
    }
}
