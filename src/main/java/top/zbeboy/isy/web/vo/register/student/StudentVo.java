package top.zbeboy.isy.web.vo.register.student;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-08-23.
 */
public class StudentVo {

    @NotNull
    @Min(1)
    private Integer school;

    @NotNull
    @Min(1)
    private Integer college;

    @NotNull
    @Min(1)
    private Integer department;

    @NotNull
    @Min(1)
    private Integer science;

    @NotNull
    private String grade;

    @NotNull
    @Min(1)
    private Integer organize;

    @NotNull
    @Pattern(regexp = "^[\\d]{13,}$")
    private String studentNumber;

    @NotNull
    @Pattern(regexp = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$")
    private String email;

    @NotNull
    @Pattern(regexp = "^1[0-9]{10}")
    private String mobile;

    @NotNull
    @Size(max = 30)
    private String realName;

    @NotNull
    private String phoneVerifyCode;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]\\w{5,17}$")
    private String password;

    @NotNull
    private String confirmPassword;

    public Integer getSchool() {
        return school;
    }

    public void setSchool(Integer school) {
        this.school = school;
    }

    public Integer getCollege() {
        return college;
    }

    public void setCollege(Integer college) {
        this.college = college;
    }

    public Integer getDepartment() {
        return department;
    }

    public void setDepartment(Integer department) {
        this.department = department;
    }

    public Integer getScience() {
        return science;
    }

    public void setScience(Integer science) {
        this.science = science;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getOrganize() {
        return organize;
    }

    public void setOrganize(Integer organize) {
        this.organize = organize;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhoneVerifyCode() {
        return phoneVerifyCode;
    }

    public void setPhoneVerifyCode(String phoneVerifyCode) {
        this.phoneVerifyCode = phoneVerifyCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
