/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.sql.Timestamp;
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
import top.zbeboy.isy.domain.tables.records.PersistentLoginsRecord;


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
public class PersistentLogins extends TableImpl<PersistentLoginsRecord> {

    private static final long serialVersionUID = 418570854;

    /**
     * The reference instance of <code>isy.persistent_logins</code>
     */
    public static final PersistentLogins PERSISTENT_LOGINS = new PersistentLogins();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PersistentLoginsRecord> getRecordType() {
        return PersistentLoginsRecord.class;
    }

    /**
     * The column <code>isy.persistent_logins.username</code>.
     */
    public final TableField<PersistentLoginsRecord, String> USERNAME = createField("username", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.persistent_logins.series</code>.
     */
    public final TableField<PersistentLoginsRecord, String> SERIES = createField("series", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.persistent_logins.token</code>.
     */
    public final TableField<PersistentLoginsRecord, String> TOKEN = createField("token", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.persistent_logins.last_used</code>.
     */
    public final TableField<PersistentLoginsRecord, Timestamp> LAST_USED = createField("last_used", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * Create a <code>isy.persistent_logins</code> table reference
     */
    public PersistentLogins() {
        this(DSL.name("persistent_logins"), null);
    }

    /**
     * Create an aliased <code>isy.persistent_logins</code> table reference
     */
    public PersistentLogins(String alias) {
        this(DSL.name(alias), PERSISTENT_LOGINS);
    }

    /**
     * Create an aliased <code>isy.persistent_logins</code> table reference
     */
    public PersistentLogins(Name alias) {
        this(alias, PERSISTENT_LOGINS);
    }

    private PersistentLogins(Name alias, Table<PersistentLoginsRecord> aliased) {
        this(alias, aliased, null);
    }

    private PersistentLogins(Name alias, Table<PersistentLoginsRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.PERSISTENT_LOGINS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PersistentLoginsRecord> getPrimaryKey() {
        return Keys.KEY_PERSISTENT_LOGINS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PersistentLoginsRecord>> getKeys() {
        return Arrays.<UniqueKey<PersistentLoginsRecord>>asList(Keys.KEY_PERSISTENT_LOGINS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersistentLogins as(String alias) {
        return new PersistentLogins(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersistentLogins as(Name alias) {
        return new PersistentLogins(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PersistentLogins rename(String name) {
        return new PersistentLogins(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PersistentLogins rename(Name name) {
        return new PersistentLogins(name, null);
    }
}
