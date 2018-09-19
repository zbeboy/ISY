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
import top.zbeboy.isy.domain.tables.records.InternshipApplyRecord;


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
public class InternshipApply extends TableImpl<InternshipApplyRecord> {

    private static final long serialVersionUID = 389071378;

    /**
     * The reference instance of <code>isy.internship_apply</code>
     */
    public static final InternshipApply INTERNSHIP_APPLY = new InternshipApply();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<InternshipApplyRecord> getRecordType() {
        return InternshipApplyRecord.class;
    }

    /**
     * The column <code>isy.internship_apply.internship_apply_id</code>.
     */
    public final TableField<InternshipApplyRecord, String> INTERNSHIP_APPLY_ID = createField("internship_apply_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.internship_apply.student_id</code>.
     */
    public final TableField<InternshipApplyRecord, Integer> STUDENT_ID = createField("student_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.internship_apply.internship_release_id</code>.
     */
    public final TableField<InternshipApplyRecord, String> INTERNSHIP_RELEASE_ID = createField("internship_release_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.internship_apply.internship_apply_state</code>.
     */
    public final TableField<InternshipApplyRecord, Integer> INTERNSHIP_APPLY_STATE = createField("internship_apply_state", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>isy.internship_apply.reason</code>.
     */
    public final TableField<InternshipApplyRecord, String> REASON = createField("reason", org.jooq.impl.SQLDataType.VARCHAR(500), this, "");

    /**
     * The column <code>isy.internship_apply.change_fill_start_time</code>.
     */
    public final TableField<InternshipApplyRecord, Timestamp> CHANGE_FILL_START_TIME = createField("change_fill_start_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>isy.internship_apply.change_fill_end_time</code>.
     */
    public final TableField<InternshipApplyRecord, Timestamp> CHANGE_FILL_END_TIME = createField("change_fill_end_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>isy.internship_apply.apply_time</code>.
     */
    public final TableField<InternshipApplyRecord, Timestamp> APPLY_TIME = createField("apply_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>isy.internship_apply.internship_file_id</code>.
     */
    public final TableField<InternshipApplyRecord, String> INTERNSHIP_FILE_ID = createField("internship_file_id", org.jooq.impl.SQLDataType.VARCHAR(64), this, "");

    /**
     * Create a <code>isy.internship_apply</code> table reference
     */
    public InternshipApply() {
        this(DSL.name("internship_apply"), null);
    }

    /**
     * Create an aliased <code>isy.internship_apply</code> table reference
     */
    public InternshipApply(String alias) {
        this(DSL.name(alias), INTERNSHIP_APPLY);
    }

    /**
     * Create an aliased <code>isy.internship_apply</code> table reference
     */
    public InternshipApply(Name alias) {
        this(alias, INTERNSHIP_APPLY);
    }

    private InternshipApply(Name alias, Table<InternshipApplyRecord> aliased) {
        this(alias, aliased, null);
    }

    private InternshipApply(Name alias, Table<InternshipApplyRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.INTERNSHIP_APPLY_INTERNSHIP_RELEASE_ID, Indexes.INTERNSHIP_APPLY_PRIMARY, Indexes.INTERNSHIP_APPLY_STUDENT_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<InternshipApplyRecord> getPrimaryKey() {
        return Keys.KEY_INTERNSHIP_APPLY_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<InternshipApplyRecord>> getKeys() {
        return Arrays.<UniqueKey<InternshipApplyRecord>>asList(Keys.KEY_INTERNSHIP_APPLY_PRIMARY, Keys.KEY_INTERNSHIP_APPLY_STUDENT_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<InternshipApplyRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<InternshipApplyRecord, ?>>asList(Keys.INTERNSHIP_APPLY_IBFK_1, Keys.INTERNSHIP_APPLY_IBFK_2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipApply as(String alias) {
        return new InternshipApply(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipApply as(Name alias) {
        return new InternshipApply(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public InternshipApply rename(String name) {
        return new InternshipApply(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public InternshipApply rename(Name name) {
        return new InternshipApply(name, null);
    }
}
