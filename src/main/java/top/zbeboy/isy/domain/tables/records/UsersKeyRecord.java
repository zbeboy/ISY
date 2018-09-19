/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.UsersKey;


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
public class UsersKeyRecord extends UpdatableRecordImpl<UsersKeyRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1944526327;

    /**
     * Setter for <code>isy.users_key.username</code>.
     */
    public void setUsername(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.users_key.username</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getUsername() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.users_key.user_key</code>.
     */
    public void setUserKey(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.users_key.user_key</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getUserKey() {
        return (String) get(1);
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
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return UsersKey.USERS_KEY.USERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return UsersKey.USERS_KEY.USER_KEY;
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
        return getUserKey();
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
        return getUserKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersKeyRecord value1(String value) {
        setUsername(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersKeyRecord value2(String value) {
        setUserKey(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersKeyRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UsersKeyRecord
     */
    public UsersKeyRecord() {
        super(UsersKey.USERS_KEY);
    }

    /**
     * Create a detached, initialised UsersKeyRecord
     */
    public UsersKeyRecord(String username, String userKey) {
        super(UsersKey.USERS_KEY);

        set(0, username);
        set(1, userKey);
    }
}
