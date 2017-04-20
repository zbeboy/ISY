package top.zbeboy.isy.web.vo.data.organize;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-09-25.
 */
public class OrganizeVo {
    private Integer organizeId;
    @NotNull
    @Size(max = 200)
    private String organizeName;
    private Byte organizeIsDel;
    @NotNull
    @Min(1)
    private Integer scienceId;
    @NotNull
    @Size(max = 5)
    private String grade;

    private Integer schoolId;
    private String schoolName;
    private Integer collegeId;
    private String collegeName;
    private Integer departmentId;
    private String departmentName;
    private String scienceName;

    public Integer getOrganizeId() {
        return organizeId;
    }

    public void setOrganizeId(Integer organizeId) {
        this.organizeId = organizeId;
    }

    public String getOrganizeName() {
        return organizeName;
    }

    public void setOrganizeName(String organizeName) {
        this.organizeName = organizeName;
    }

    public Byte getOrganizeIsDel() {
        return organizeIsDel;
    }

    public void setOrganizeIsDel(Byte organizeIsDel) {
        this.organizeIsDel = organizeIsDel;
    }

    public Integer getScienceId() {
        return scienceId;
    }

    public void setScienceId(Integer scienceId) {
        this.scienceId = scienceId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getScienceName() {
        return scienceName;
    }

    public void setScienceName(String scienceName) {
        this.scienceName = scienceName;
    }

    @Override
    public String toString() {
        return "OrganizeVo{" +
                "organizeId=" + organizeId +
                ", organizeName='" + organizeName + '\'' +
                ", organizeIsDel=" + organizeIsDel +
                ", scienceId=" + scienceId +
                ", grade='" + grade + '\'' +
                ", schoolId=" + schoolId +
                ", schoolName='" + schoolName + '\'' +
                ", collegeId=" + collegeId +
                ", collegeName='" + collegeName + '\'' +
                ", departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", scienceName='" + scienceName + '\'' +
                '}';
    }
}
