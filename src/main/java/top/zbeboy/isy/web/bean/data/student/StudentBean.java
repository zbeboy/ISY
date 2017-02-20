package top.zbeboy.isy.web.bean.data.student;

import top.zbeboy.isy.domain.tables.pojos.Student;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by lenovo on 2016-09-27.
 */
public class StudentBean extends Student {
    private Integer schoolId;
    private String schoolName;
    private Byte schoolIsDel;

    private Integer collegeId;
    private String collegeName;
    private Byte collegeIsDel;

    private Integer departmentId;
    private String departmentName;
    private Byte departmentIsDel;

    private Integer scienceId;
    private String scienceName;
    private Byte scienceIsDel;

    private String organizeName;
    private Byte organizeIsDel;
    private String grade;

    private String password;
    private Byte enabled;
    private Integer usersTypeId;
    private String realName;
    private String mobile;
    private String avatar;
    private Byte verifyMailbox;
    private String mailboxVerifyCode;
    private String passwordResetKey;
    private Timestamp mailboxVerifyValid;
    private Timestamp passwordResetKeyValid;
    private String langKey;
    private Date joinDate;

    private String nationName;

    private String politicalLandscapeName;

    private String roleName;

    // 楼号
    private String ridgepole;
    // 宿舍号
    private String dorm;

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

    public Byte getSchoolIsDel() {
        return schoolIsDel;
    }

    public void setSchoolIsDel(Byte schoolIsDel) {
        this.schoolIsDel = schoolIsDel;
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

    public Byte getCollegeIsDel() {
        return collegeIsDel;
    }

    public void setCollegeIsDel(Byte collegeIsDel) {
        this.collegeIsDel = collegeIsDel;
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

    public Byte getDepartmentIsDel() {
        return departmentIsDel;
    }

    public void setDepartmentIsDel(Byte departmentIsDel) {
        this.departmentIsDel = departmentIsDel;
    }

    public Integer getScienceId() {
        return scienceId;
    }

    public void setScienceId(Integer scienceId) {
        this.scienceId = scienceId;
    }

    public String getScienceName() {
        return scienceName;
    }

    public void setScienceName(String scienceName) {
        this.scienceName = scienceName;
    }

    public Byte getScienceIsDel() {
        return scienceIsDel;
    }

    public void setScienceIsDel(Byte scienceIsDel) {
        this.scienceIsDel = scienceIsDel;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Byte getEnabled() {
        return enabled;
    }

    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    public Integer getUsersTypeId() {
        return usersTypeId;
    }

    public void setUsersTypeId(Integer usersTypeId) {
        this.usersTypeId = usersTypeId;
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

    public Byte getVerifyMailbox() {
        return verifyMailbox;
    }

    public void setVerifyMailbox(Byte verifyMailbox) {
        this.verifyMailbox = verifyMailbox;
    }

    public String getMailboxVerifyCode() {
        return mailboxVerifyCode;
    }

    public void setMailboxVerifyCode(String mailboxVerifyCode) {
        this.mailboxVerifyCode = mailboxVerifyCode;
    }

    public String getPasswordResetKey() {
        return passwordResetKey;
    }

    public void setPasswordResetKey(String passwordResetKey) {
        this.passwordResetKey = passwordResetKey;
    }

    public Timestamp getMailboxVerifyValid() {
        return mailboxVerifyValid;
    }

    public void setMailboxVerifyValid(Timestamp mailboxVerifyValid) {
        this.mailboxVerifyValid = mailboxVerifyValid;
    }

    public Timestamp getPasswordResetKeyValid() {
        return passwordResetKeyValid;
    }

    public void setPasswordResetKeyValid(Timestamp passwordResetKeyValid) {
        this.passwordResetKeyValid = passwordResetKeyValid;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getNationName() {
        return nationName;
    }

    public void setNationName(String nationName) {
        this.nationName = nationName;
    }

    public String getPoliticalLandscapeName() {
        return politicalLandscapeName;
    }

    public void setPoliticalLandscapeName(String politicalLandscapeName) {
        this.politicalLandscapeName = politicalLandscapeName;
    }

    public String getRidgepole() {
        return ridgepole;
    }

    public void setRidgepole(String ridgepole) {
        this.ridgepole = ridgepole;
    }

    public String getDorm() {
        return dorm;
    }

    public void setDorm(String dorm) {
        this.dorm = dorm;
    }
}
