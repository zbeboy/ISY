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
import top.zbeboy.isy.domain.tables.records.SystemAlertTypeRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SystemAlertType extends TableImpl<SystemAlertTypeRecord> {

    private static final long serialVersionUID = -1759627141;

    /**
     * The reference instance of <code>isy.system_alert_type</code>
     */
    public static final SystemAlertType SYSTEM_ALERT_TYPE = new SystemAlertType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SystemAlertTypeRecord> getRecordType() {
        return SystemAlertTypeRecord.class;
    }

    /**
     * The column <code>isy.system_alert_type.system_alert_type_id</code>.
     */
    public final TableField<SystemAlertTypeRecord, Integer> SYSTEM_ALERT_TYPE_ID = createField("system_alert_type_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.system_alert_type.name</code>.
     */
    public final TableField<SystemAlertTypeRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(10).nullable(false), this, "");

    /**
     * The column <code>isy.system_alert_type.icon</code>.
     */
    public final TableField<SystemAlertTypeRecord, String> ICON = createField("icon", org.jooq.impl.SQLDataType.VARCHAR.length(30).nullable(false), this, "");

    /**
     * Create a <code>isy.system_alert_type</code> table reference
     */
    public SystemAlertType() {
        this("system_alert_type", null);
    }

    /**
     * Create an aliased <code>isy.system_alert_type</code> table reference
     */
    public SystemAlertType(String alias) {
        this(alias, SYSTEM_ALERT_TYPE);
    }

    private SystemAlertType(String alias, Table<SystemAlertTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private SystemAlertType(String alias, Table<SystemAlertTypeRecord> aliased, Field<?>[] parameters) {
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
    public Identity<SystemAlertTypeRecord, Integer> getIdentity() {
        return Keys.IDENTITY_SYSTEM_ALERT_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SystemAlertTypeRecord> getPrimaryKey() {
        return Keys.KEY_SYSTEM_ALERT_TYPE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SystemAlertTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<SystemAlertTypeRecord>>asList(Keys.KEY_SYSTEM_ALERT_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SystemAlertType as(String alias) {
        return new SystemAlertType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SystemAlertType rename(String name) {
        return new SystemAlertType(name, null);
    }
}