/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Indexes;
import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.UsersKeyRecord;


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
public class UsersKey extends TableImpl<UsersKeyRecord> {

    private static final long serialVersionUID = -60329646;

    /**
     * The reference instance of <code>isy.users_key</code>
     */
    public static final UsersKey USERS_KEY = new UsersKey();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UsersKeyRecord> getRecordType() {
        return UsersKeyRecord.class;
    }

    /**
     * The column <code>isy.users_key.username</code>.
     */
    public final TableField<UsersKeyRecord, String> USERNAME = createField("username", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.users_key.user_key</code>.
     */
    public final TableField<UsersKeyRecord, String> USER_KEY = createField("user_key", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * Create a <code>isy.users_key</code> table reference
     */
    public UsersKey() {
        this(DSL.name("users_key"), null);
    }

    /**
     * Create an aliased <code>isy.users_key</code> table reference
     */
    public UsersKey(String alias) {
        this(DSL.name(alias), USERS_KEY);
    }

    /**
     * Create an aliased <code>isy.users_key</code> table reference
     */
    public UsersKey(Name alias) {
        this(alias, USERS_KEY);
    }

    private UsersKey(Name alias, Table<UsersKeyRecord> aliased) {
        this(alias, aliased, null);
    }

    private UsersKey(Name alias, Table<UsersKeyRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Isy.ISY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.USERS_KEY_PRIMARY, Indexes.USERS_KEY_USER_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<UsersKeyRecord> getPrimaryKey() {
        return Keys.KEY_USERS_KEY_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<UsersKeyRecord>> getKeys() {
        return Arrays.<UniqueKey<UsersKeyRecord>>asList(Keys.KEY_USERS_KEY_PRIMARY, Keys.KEY_USERS_KEY_USER_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersKey as(String alias) {
        return new UsersKey(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersKey as(Name alias) {
        return new UsersKey(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public UsersKey rename(String name) {
        return new UsersKey(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UsersKey rename(Name name) {
        return new UsersKey(name, null);
    }
}
