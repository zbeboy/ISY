/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.BuildingRecord;


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
public class Building extends TableImpl<BuildingRecord> {

    private static final long serialVersionUID = -802297452;

    /**
     * The reference instance of <code>isy.building</code>
     */
    public static final Building BUILDING = new Building();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BuildingRecord> getRecordType() {
        return BuildingRecord.class;
    }

    /**
     * The column <code>isy.building.building_id</code>.
     */
    public final TableField<BuildingRecord, Integer> BUILDING_ID = createField("building_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.building.building_name</code>.
     */
    public final TableField<BuildingRecord, String> BUILDING_NAME = createField("building_name", org.jooq.impl.SQLDataType.VARCHAR.length(30).nullable(false), this, "");

    /**
     * The column <code>isy.building.building_is_del</code>.
     */
    public final TableField<BuildingRecord, Byte> BUILDING_IS_DEL = createField("building_is_del", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>isy.building.college_id</code>.
     */
    public final TableField<BuildingRecord, Integer> COLLEGE_ID = createField("college_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>isy.building</code> table reference
     */
    public Building() {
        this("building", null);
    }

    /**
     * Create an aliased <code>isy.building</code> table reference
     */
    public Building(String alias) {
        this(alias, BUILDING);
    }

    private Building(String alias, Table<BuildingRecord> aliased) {
        this(alias, aliased, null);
    }

    private Building(String alias, Table<BuildingRecord> aliased, Field<?>[] parameters) {
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
    public Identity<BuildingRecord, Integer> getIdentity() {
        return Keys.IDENTITY_BUILDING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<BuildingRecord> getPrimaryKey() {
        return Keys.KEY_BUILDING_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<BuildingRecord>> getKeys() {
        return Arrays.<UniqueKey<BuildingRecord>>asList(Keys.KEY_BUILDING_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<BuildingRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BuildingRecord, ?>>asList(Keys.BUILDING_IBFK_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Building as(String alias) {
        return new Building(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Building rename(String name) {
        return new Building(name, null);
    }
}
