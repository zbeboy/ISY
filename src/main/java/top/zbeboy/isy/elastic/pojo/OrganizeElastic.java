package top.zbeboy.isy.elastic.pojo;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Created by lenovo on 2017-04-09.
 */
@Document(indexName = "organize", type = "organize", shards = 1, replicas = 0, refreshInterval = "-1")
public class OrganizeElastic {
    @Id
    private String organizeId;
    private String organizeName;
    private Byte organizeIsDel;
    private Integer scienceId;
    private String grade;
    private Integer schoolId;
    private String schoolName;
    private Integer collegeId;
    private String collegeName;
    private Integer departmentId;
    private String departmentName;
    private String scienceName;

    public OrganizeElastic() {
    }

    public Integer getOrganizeId() {
        return NumberUtils.toInt(organizeId);
    }

    public void setOrganizeId(Integer organizeId) {
        this.organizeId = organizeId + "";
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
        return "OrganizeElastic{" +
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
