/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import java.sql.Date;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record14;
import org.jooq.Row14;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.Users;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UsersRecord extends UpdatableRecordImpl<UsersRecord> implements Record14<String, String, Byte, Integer, String, String, String, Byte, String, String, Timestamp, Timestamp, String, Date> {

    private static final long serialVersionUID = 1813961955;

    /**
     * Setter for <code>isy.users.username</code>.
     */
    public void setUsername(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.users.username</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getUsername() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.users.password</code>.
     */
    public void setPassword(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.users.password</code>.
     */
    @NotNull
    @Size(max = 300)
    public String getPassword() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.users.enabled</code>.
     */
    public void setEnabled(Byte value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.users.enabled</code>.
     */
    @NotNull
    public Byte getEnabled() {
        return (Byte) get(2);
    }

    /**
     * Setter for <code>isy.users.users_type_id</code>.
     */
    public void setUsersTypeId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.users.users_type_id</code>.
     */
    @NotNull
    public Integer getUsersTypeId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>isy.users.real_name</code>.
     */
    public void setRealName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.users.real_name</code>.
     */
    @Size(max = 30)
    public String getRealName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>isy.users.mobile</code>.
     */
    public void setMobile(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.users.mobile</code>.
     */
    @NotNull
    @Size(max = 15)
    public String getMobile() {
        return (String) get(5);
    }

    /**
     * Setter for <code>isy.users.avatar</code>.
     */
    public void setAvatar(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>isy.users.avatar</code>.
     */
    @Size(max = 500)
    public String getAvatar() {
        return (String) get(6);
    }

    /**
     * Setter for <code>isy.users.verify_mailbox</code>.
     */
    public void setVerifyMailbox(Byte value) {
        set(7, value);
    }

    /**
     * Getter for <code>isy.users.verify_mailbox</code>.
     */
    public Byte getVerifyMailbox() {
        return (Byte) get(7);
    }

    /**
     * Setter for <code>isy.users.mailbox_verify_code</code>.
     */
    public void setMailboxVerifyCode(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>isy.users.mailbox_verify_code</code>.
     */
    @Size(max = 20)
    public String getMailboxVerifyCode() {
        return (String) get(8);
    }

    /**
     * Setter for <code>isy.users.password_reset_key</code>.
     */
    public void setPasswordResetKey(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>isy.users.password_reset_key</code>.
     */
    @Size(max = 20)
    public String getPasswordResetKey() {
        return (String) get(9);
    }

    /**
     * Setter for <code>isy.users.mailbox_verify_valid</code>.
     */
    public void setMailboxVerifyValid(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>isy.users.mailbox_verify_valid</code>.
     */
    public Timestamp getMailboxVerifyValid() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>isy.users.password_reset_key_valid</code>.
     */
    public void setPasswordResetKeyValid(Timestamp value) {
        set(11, value);
    }

    /**
     * Getter for <code>isy.users.password_reset_key_valid</code>.
     */
    public Timestamp getPasswordResetKeyValid() {
        return (Timestamp) get(11);
    }

    /**
     * Setter for <code>isy.users.lang_key</code>.
     */
    public void setLangKey(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>isy.users.lang_key</code>.
     */
    @Size(max = 20)
    public String getLangKey() {
        return (String) get(12);
    }

    /**
     * Setter for <code>isy.users.join_date</code>.
     */
    public void setJoinDate(Date value) {
        set(13, value);
    }

    /**
     * Getter for <code>isy.users.join_date</code>.
     */
    public Date getJoinDate() {
        return (Date) get(13);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record14 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<String, String, Byte, Integer, String, String, String, Byte, String, String, Timestamp, Timestamp, String, Date> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<String, String, Byte, Integer, String, String, String, Byte, String, String, Timestamp, Timestamp, String, Date> valuesRow() {
        return (Row14) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Users.USERS.USERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Users.USERS.PASSWORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field3() {
        return Users.USERS.ENABLED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return Users.USERS.USERS_TYPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Users.USERS.REAL_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Users.USERS.MOBILE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Users.USERS.AVATAR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field8() {
        return Users.USERS.VERIFY_MAILBOX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Users.USERS.MAILBOX_VERIFY_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Users.USERS.PASSWORD_RESET_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field11() {
        return Users.USERS.MAILBOX_VERIFY_VALID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field12() {
        return Users.USERS.PASSWORD_RESET_KEY_VALID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return Users.USERS.LANG_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field14() {
        return Users.USERS.JOIN_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component3() {
        return getEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getUsersTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getRealName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getMobile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getAvatar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component8() {
        return getVerifyMailbox();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getMailboxVerifyCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component10() {
        return getPasswordResetKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component11() {
        return getMailboxVerifyValid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component12() {
        return getPasswordResetKeyValid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component13() {
        return getLangKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date component14() {
        return getJoinDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value3() {
        return getEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getUsersTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getRealName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getMobile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getAvatar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value8() {
        return getVerifyMailbox();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getMailboxVerifyCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getPasswordResetKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value11() {
        return getMailboxVerifyValid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value12() {
        return getPasswordResetKeyValid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getLangKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value14() {
        return getJoinDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value1(String value) {
        setUsername(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value2(String value) {
        setPassword(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value3(Byte value) {
        setEnabled(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value4(Integer value) {
        setUsersTypeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value5(String value) {
        setRealName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value6(String value) {
        setMobile(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value7(String value) {
        setAvatar(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value8(Byte value) {
        setVerifyMailbox(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value9(String value) {
        setMailboxVerifyCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value10(String value) {
        setPasswordResetKey(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value11(Timestamp value) {
        setMailboxVerifyValid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value12(Timestamp value) {
        setPasswordResetKeyValid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value13(String value) {
        setLangKey(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord value14(Date value) {
        setJoinDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRecord values(String value1, String value2, Byte value3, Integer value4, String value5, String value6, String value7, Byte value8, String value9, String value10, Timestamp value11, Timestamp value12, String value13, Date value14) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UsersRecord
     */
    public UsersRecord() {
        super(Users.USERS);
    }

    /**
     * Create a detached, initialised UsersRecord
     */
    public UsersRecord(String username, String password, Byte enabled, Integer usersTypeId, String realName, String mobile, String avatar, Byte verifyMailbox, String mailboxVerifyCode, String passwordResetKey, Timestamp mailboxVerifyValid, Timestamp passwordResetKeyValid, String langKey, Date joinDate) {
        super(Users.USERS);

        set(0, username);
        set(1, password);
        set(2, enabled);
        set(3, usersTypeId);
        set(4, realName);
        set(5, mobile);
        set(6, avatar);
        set(7, verifyMailbox);
        set(8, mailboxVerifyCode);
        set(9, passwordResetKey);
        set(10, mailboxVerifyValid);
        set(11, passwordResetKeyValid);
        set(12, langKey);
        set(13, joinDate);
    }
}
