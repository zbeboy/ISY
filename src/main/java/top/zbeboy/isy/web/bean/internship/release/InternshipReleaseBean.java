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
    private String internshipTypeName;
    private List<Science> sciences;
    private String teacherDistributionStartTimeStr;
    private String teacherDistributionEndTimeStr;
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

    public String getInternshipTypeName() {
        return internshipTypeName;
    }

    public void setInternshipTypeName(String internshipTypeName) {
        this.internshipTypeName = internshipTypeName;
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
}
