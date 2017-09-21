/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.UsersTypeRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UsersType extends TableImpl<UsersTypeRecord> {

    private static final long serialVersionUID = 1356970489;

    /**
     * The reference instance of <code>isy.users_type</code>
     */
    public static final UsersType USERS_TYPE = new UsersType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UsersTypeRecord> getRecordType() {
        return UsersTypeRecord.class;
    }

    /**
     * The column <code>isy.users_type.users_type_id</code>.
     */
    public final TableField<UsersTypeRecord, Integer> USERS_TYPE_ID = createField("users_type_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.users_type.users_type_name</code>.
     */
    public final TableField<UsersTypeRecord, String> USERS_TYPE_NAME = createField("users_type_name", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

    /**
     * Create a <code>isy.users_type</code> table reference
     */
    public UsersType() {
        this("users_type", null);
    }

    /**
     * Create an aliased <code>isy.users_type</code> table reference
     */
    public UsersType(String alias) {
        this(alias, USERS_TYPE);
    }

    private UsersType(String alias, Table<UsersTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private UsersType(String alias, Table<UsersTypeRecord> aliased, Field<?>[] parameters) {
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
    public Identity<UsersTypeRecord, Integer> getIdentity() {
        return Keys.IDENTITY_USERS_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<UsersTypeRecord> getPrimaryKey() {
        return Keys.KEY_USERS_TYPE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<UsersTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<UsersTypeRecord>>asList(Keys.KEY_USERS_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersType as(String alias) {
        return new UsersType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public UsersType rename(String name) {
        return new UsersType(name, null);
    }
}
