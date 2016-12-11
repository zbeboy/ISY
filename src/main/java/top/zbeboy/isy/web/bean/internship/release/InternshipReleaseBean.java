package top.zbeboy.isy.web.bean.internship.release;

import top.zbeboy.isy.domain.tables.pojos.InternshipRelease;
import top.zbeboy.isy.domain.tables.pojos.Science;

import java.util.List;

/**
 * Created by lenovo on 2016-11-14.
 */
public class InternshipReleaseBean extends InternshipRelease {
    private String realName;
    private String departmentName;
    private String schoolName;
    private String collegeName;
    private String internshipTypeName;
    private List<Science> sciences;
    private int schoolId;
    private int collegeId;
    private String teacherDistributionStartTimeStr;
    private String teacherDistributionEndTimeStr;
    private String startTimeStr;
    private String endTimeStr;
    private String releaseTimeStr;

    // 统计总数
    private int waitTotalData;
    private int passTotalData;
    private int failTotalData;
    private int basicApplyTotalData;
    private int companyApplyTotalData;
    private int basicFillTotalData;
    private int companyFillTotalData;

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

    public String getInternshipTypeName() {
        return internshipTypeName;
    }

    public void setInternshipTypeName(String internshipTypeName) {
        this.internshipTypeName = internshipTypeName;
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

    public List<Science> getSciences() {
        return sciences;
    }

    public void setSciences(List<Science> sciences) {
        this.sciences = sciences;
    }

    public String getTeacherDistributionStartTimeStr() {
        return teacherDistributionStartTimeStr;
    }

    public void setTeacherDistributionStartTimeStr(String teacherDistributionStartTimeStr) {
        this.teacherDistributionStartTimeStr = teacherDistributionStartTimeStr;
    }

    public String getTeacherDistributionEndTimeStr() {
        return teacherDistributionEndTimeStr;
    }

    public void setTeacherDistributionEndTimeStr(String teacherDistributionEndTimeStr) {
        this.teacherDistributionEndTimeStr = teacherDistributionEndTimeStr;
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

    public int getWaitTotalData() {
        return waitTotalData;
    }

    public void setWaitTotalData(int waitTotalData) {
        this.waitTotalData = waitTotalData;
    }

    public int getPassTotalData() {
        return passTotalData;
    }

    public void setPassTotalData(int passTotalData) {
        this.passTotalData = passTotalData;
    }

    public int getFailTotalData() {
        return failTotalData;
    }

    public void setFailTotalData(int failTotalData) {
        this.failTotalData = failTotalData;
    }

    public int getBasicApplyTotalData() {
        return basicApplyTotalData;
    }

    public void setBasicApplyTotalData(int basicApplyTotalData) {
        this.basicApplyTotalData = basicApplyTotalData;
    }

    public int getCompanyApplyTotalData() {
        return companyApplyTotalData;
    }

    public void setCompanyApplyTotalData(int companyApplyTotalData) {
        this.companyApplyTotalData = companyApplyTotalData;
    }

    public int getBasicFillTotalData() {
        return basicFillTotalData;
    }

    public void setBasicFillTotalData(int basicFillTotalData) {
        this.basicFillTotalData = basicFillTotalData;
    }

    public int getCompanyFillTotalData() {
        return companyFillTotalData;
    }

    public void setCompanyFillTotalData(int companyFillTotalData) {
        this.companyFillTotalData = companyFillTotalData;
    }
}
