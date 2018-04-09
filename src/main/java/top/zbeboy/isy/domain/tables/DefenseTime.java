/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Indexes;
import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.DefenseTimeRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DefenseTime extends TableImpl<DefenseTimeRecord> {

    private static final long serialVersionUID = -1515830577;

    /**
     * The reference instance of <code>isy.defense_time</code>
     */
    public static final DefenseTime DEFENSE_TIME = new DefenseTime();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DefenseTimeRecord> getRecordType() {
        return DefenseTimeRecord.class;
    }

    /**
     * The column <code>isy.defense_time.day_defense_start_time</code>.
     */
    public final TableField<DefenseTimeRecord, String> DAY_DEFENSE_START_TIME = createField("day_defense_start_time", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>isy.defense_time.day_defense_end_time</code>.
     */
    public final TableField<DefenseTimeRecord, String> DAY_DEFENSE_END_TIME = createField("day_defense_end_time", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>isy.defense_time.sort_time</code>.
     */
    public final TableField<DefenseTimeRecord, Integer> SORT_TIME = createField("sort_time", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.defense_time.defense_arrangement_id</code>.
     */
    public final TableField<DefenseTimeRecord, String> DEFENSE_ARRANGEMENT_ID = createField("defense_arrangement_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * Create a <code>isy.defense_time</code> table reference
     */
    public DefenseTime() {
        this(DSL.name("defense_time"), null);
    }

    /**
     * Create an aliased <code>isy.defense_time</code> table reference
     */
    public DefenseTime(String alias) {
        this(DSL.name(alias), DEFENSE_TIME);
    }

    /**
     * Create an aliased <code>isy.defense_time</code> table reference
     */
    public DefenseTime(Name alias) {
        this(alias, DEFENSE_TIME);
    }

    private DefenseTime(Name alias, Table<DefenseTimeRecord> aliased) {
        this(alias, aliased, null);
    }

    private DefenseTime(Name alias, Table<DefenseTimeRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.DEFENSE_TIME_DEFENSE_ARRANGEMENT_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<DefenseTimeRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DefenseTimeRecord, ?>>asList(Keys.DEFENSE_TIME_IBFK_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseTime as(String alias) {
        return new DefenseTime(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseTime as(Name alias) {
        return new DefenseTime(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DefenseTime rename(String name) {
        return new DefenseTime(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DefenseTime rename(Name name) {
        return new DefenseTime(name, null);
    }
}
