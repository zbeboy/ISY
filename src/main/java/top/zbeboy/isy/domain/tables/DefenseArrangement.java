/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.sql.Timestamp;
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
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Indexes;
import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.DefenseArrangementRecord;


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
public class DefenseArrangement extends TableImpl<DefenseArrangementRecord> {

    private static final long serialVersionUID = -1986829669;

    /**
     * The reference instance of <code>isy.defense_arrangement</code>
     */
    public static final DefenseArrangement DEFENSE_ARRANGEMENT = new DefenseArrangement();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DefenseArrangementRecord> getRecordType() {
        return DefenseArrangementRecord.class;
    }

    /**
     * The column <code>isy.defense_arrangement.defense_arrangement_id</code>.
     */
    public final TableField<DefenseArrangementRecord, String> DEFENSE_ARRANGEMENT_ID = createField("defense_arrangement_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.defense_arrangement.paper_start_time</code>.
     */
    public final TableField<DefenseArrangementRecord, Timestamp> PAPER_START_TIME = createField("paper_start_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>isy.defense_arrangement.paper_end_time</code>.
     */
    public final TableField<DefenseArrangementRecord, Timestamp> PAPER_END_TIME = createField("paper_end_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>isy.defense_arrangement.defense_start_time</code>.
     */
    public final TableField<DefenseArrangementRecord, Timestamp> DEFENSE_START_TIME = createField("defense_start_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>isy.defense_arrangement.defense_end_time</code>.
     */
    public final TableField<DefenseArrangementRecord, Timestamp> DEFENSE_END_TIME = createField("defense_end_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>isy.defense_arrangement.interval_time</code>.
     */
    public final TableField<DefenseArrangementRecord, Integer> INTERVAL_TIME = createField("interval_time", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.defense_arrangement.defense_note</code>.
     */
    public final TableField<DefenseArrangementRecord, String> DEFENSE_NOTE = createField("defense_note", org.jooq.impl.SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>isy.defense_arrangement.graduation_design_release_id</code>.
     */
    public final TableField<DefenseArrangementRecord, String> GRADUATION_DESIGN_RELEASE_ID = createField("graduation_design_release_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * Create a <code>isy.defense_arrangement</code> table reference
     */
    public DefenseArrangement() {
        this(DSL.name("defense_arrangement"), null);
    }

    /**
     * Create an aliased <code>isy.defense_arrangement</code> table reference
     */
    public DefenseArrangement(String alias) {
        this(DSL.name(alias), DEFENSE_ARRANGEMENT);
    }

    /**
     * Create an aliased <code>isy.defense_arrangement</code> table reference
     */
    public DefenseArrangement(Name alias) {
        this(alias, DEFENSE_ARRANGEMENT);
    }

    private DefenseArrangement(Name alias, Table<DefenseArrangementRecord> aliased) {
        this(alias, aliased, null);
    }

    private DefenseArrangement(Name alias, Table<DefenseArrangementRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.DEFENSE_ARRANGEMENT_GRADUATION_DESIGN_RELEASE_ID, Indexes.DEFENSE_ARRANGEMENT_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DefenseArrangementRecord> getPrimaryKey() {
        return Keys.KEY_DEFENSE_ARRANGEMENT_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DefenseArrangementRecord>> getKeys() {
        return Arrays.<UniqueKey<DefenseArrangementRecord>>asList(Keys.KEY_DEFENSE_ARRANGEMENT_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<DefenseArrangementRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DefenseArrangementRecord, ?>>asList(Keys.DEFENSE_ARRANGEMENT_IBFK_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangement as(String alias) {
        return new DefenseArrangement(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangement as(Name alias) {
        return new DefenseArrangement(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DefenseArrangement rename(String name) {
        return new DefenseArrangement(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DefenseArrangement rename(Name name) {
        return new DefenseArrangement(name, null);
    }
}
