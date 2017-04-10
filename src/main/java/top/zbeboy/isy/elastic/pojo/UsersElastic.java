package top.zbeboy.isy.elastic.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import top.zbeboy.isy.domain.tables.pojos.Role;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by lenovo on 2017-04-10.
 */
@Document(indexName = "users", type = "users", shards = 1, replicas = 0, refreshInterval = "-1")
public class UsersElastic {
    @Id
    private String username;
    private String password;
    private Byte enabled;
    private Integer usersTypeId;
    private String usersTypeName;
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

    public UsersElastic() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getUsersTypeName() {
        return usersTypeName;
    }

    public void setUsersTypeName(String usersTypeName) {
        this.usersTypeName = usersTypeName;
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

    @Override
    public String toString() {
        return "UsersElastic{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", usersTypeId=" + usersTypeId +
                ", usersTypeName='" + usersTypeName + '\'' +
                ", realName='" + realName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", avatar='" + avatar + '\'' +
                ", verifyMailbox=" + verifyMailbox +
                ", mailboxVerifyCode='" + mailboxVerifyCode + '\'' +
                ", passwordResetKey='" + passwordResetKey + '\'' +
                ", mailboxVerifyValid=" + mailboxVerifyValid +
                ", passwordResetKeyValid=" + passwordResetKeyValid +
                ", langKey='" + langKey + '\'' +
                ", joinDate=" + joinDate +
                ", authorities=" + authorities +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
