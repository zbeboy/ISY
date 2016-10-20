package top.zbeboy.isy.web.bean.platform.role;

import top.zbeboy.isy.domain.tables.pojos.Role;

/**
 * Created by lenovo on 2016-10-16.
 */
public class RoleBean extends Role {
    private Integer collegeId;
    private String  collegeName;
    private Integer schoolId;
    private String  schoolName;
    private Integer applicationId;

    public Integer getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }
}
