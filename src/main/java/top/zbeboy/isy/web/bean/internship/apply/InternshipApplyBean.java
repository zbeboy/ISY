package top.zbeboy.isy.web.bean.internship.apply;

import top.zbeboy.isy.domain.tables.pojos.InternshipApply;
import top.zbeboy.isy.domain.tables.pojos.Science;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by lenovo on 2016-11-29.
 */
public class InternshipApplyBean extends InternshipApply {
    private String internshipTitle;
    private Timestamp releaseTime;
    private String username;
    private String allowGrade;
    private Timestamp teacherDistributionStartTime;
    private Timestamp teacherDistributionEndTime;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte internshipReleaseIsDel;
    private Integer departmentId;
    private Integer internshipTypeId;
    private List<Science> sciences;
    private String teacherDistributionStartTimeStr;
    private String teacherDistributionEndTimeStr;
    private String startTimeStr;
    private String endTimeStr;
    private String releaseTimeStr;
    private String realName;
    private String departmentName;
    private String schoolName;
    private String collegeName;
    private String internshipTypeName;
    private int schoolId;
    private int collegeId;
    private String fileId;
    private String originalFileName;
    private String ext;

    public String getInternshipTitle() {
        return internshipTitle;
    }

    public void setInternshipTitle(String internshipTitle) {
        this.internshipTitle = internshipTitle;
    }

    public Timestamp getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Timestamp releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAllowGrade() {
        return allowGrade;
    }

    public void setAllowGrade(String allowGrade) {
        this.allowGrade = allowGrade;
    }

    public Timestamp getTeacherDistributionStartTime() {
        return teacherDistributionStartTime;
    }

    public void setTeacherDistributionStartTime(Timestamp teacherDistributionStartTime) {
        this.teacherDistributionStartTime = teacherDistributionStartTime;
    }

    public Timestamp getTeacherDistributionEndTime() {
        return teacherDistributionEndTime;
    }

    public void setTeacherDistributionEndTime(Timestamp teacherDistributionEndTime) {
        this.teacherDistributionEndTime = teacherDistributionEndTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Byte getInternshipReleaseIsDel() {
        return internshipReleaseIsDel;
    }

    public void setInternshipReleaseIsDel(Byte internshipReleaseIsDel) {
        this.internshipReleaseIsDel = internshipReleaseIsDel;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getInternshipTypeId() {
        return internshipTypeId;
    }

    public void setInternshipTypeId(Integer internshipTypeId) {
        this.internshipTypeId = internshipTypeId;
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
}
