package top.zbeboy.isy.elastic.pojo;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by lenovo on 2017-04-12.
 */
@Document(indexName = "staff", type = "staff", shards = 1, replicas = 0, refreshInterval = "-1")
public class StaffElastic {
    @Id
    private String staffId;
    private String  staffNumber;
    private Date birthday;
    private String  sex;
    private String  idCard;
    private String  familyResidence;
    private Integer politicalLandscapeId;
    private String  politicalLandscapeName;
    private Integer nationId;
    private String  nationName;
    private String  post;
    private Integer schoolId;
    private String schoolName;
    private Integer collegeId;
    private String collegeName;
    private Integer departmentId;
    private String departmentName;
    private String  username;
    private Byte enabled;
    private String realName;
    private String mobile;
    private String avatar;
    private String langKey;
    private Date joinDate;
    /**
     * -1 : 无权限
     * 0 :  有权限
     * 1 : 系统
     * 2 : 管理员
     */
    private Integer authorities;
    /**
     * 以空格分隔的角色名
     */
    private String roleName;

    public StaffElastic() {
    }

    public Integer getStaffId() {
        return NumberUtils.toInt(staffId);
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId + "";
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getFamilyResidence() {
        return familyResidence;
    }

    public void setFamilyResidence(String familyResidence) {
        this.familyResidence = familyResidence;
    }

    public Integer getPoliticalLandscapeId() {
        return politicalLandscapeId;
    }

    public void setPoliticalLandscapeId(Integer politicalLandscapeId) {
        this.politicalLandscapeId = politicalLandscapeId;
    }

    public Integer getNationId() {
        return nationId;
    }

    public void setNationId(Integer nationId) {
        this.nationId = nationId;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Byte getEnabled() {
        return enabled;
    }

    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Integer getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Integer authorities) {
        this.authorities = authorities;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPoliticalLandscapeName() {
        return politicalLandscapeName;
    }

    public void setPoliticalLandscapeName(String politicalLandscapeName) {
        this.politicalLandscapeName = politicalLandscapeName;
    }

    public String getNationName() {
        return nationName;
    }

    public void setNationName(String nationName) {
        this.nationName = nationName;
    }
}
