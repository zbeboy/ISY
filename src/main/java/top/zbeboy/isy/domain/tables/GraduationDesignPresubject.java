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
import top.zbeboy.isy.domain.tables.records.GraduationDesignPresubjectRecord;


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
public class GraduationDesignPresubject extends TableImpl<GraduationDesignPresubjectRecord> {

    private static final long serialVersionUID = -1860928072;

    /**
     * The reference instance of <code>isy.graduation_design_presubject</code>
     */
    public static final GraduationDesignPresubject GRADUATION_DESIGN_PRESUBJECT = new GraduationDesignPresubject();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GraduationDesignPresubjectRecord> getRecordType() {
        return GraduationDesignPresubjectRecord.class;
    }

    /**
     * The column <code>isy.graduation_design_presubject.graduation_design_presubject_id</code>.
     */
    public final TableField<GraduationDesignPresubjectRecord, String> GRADUATION_DESIGN_PRESUBJECT_ID = createField("graduation_design_presubject_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_presubject.presubject_title</code>.
     */
    public final TableField<GraduationDesignPresubjectRecord, String> PRESUBJECT_TITLE = createField("presubject_title", org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_presubject.presubject_plan</code>.
     */
    public final TableField<GraduationDesignPresubjectRecord, String> PRESUBJECT_PLAN = createField("presubject_plan", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_presubject.update_time</code>.
     */
    public final TableField<GraduationDesignPresubjectRecord, Timestamp> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_presubject.public_level</code>.
     */
    public final TableField<GraduationDesignPresubjectRecord, Integer> PUBLIC_LEVEL = createField("public_level", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_presubject.graduation_design_release_id</code>.
     */
    public final TableField<GraduationDesignPresubjectRecord, String> GRADUATION_DESIGN_RELEASE_ID = createField("graduation_design_release_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_presubject.student_id</code>.
     */
    public final TableField<GraduationDesignPresubjectRecord, Integer> STUDENT_ID = createField("student_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>isy.graduation_design_presubject</code> table reference
     */
    public GraduationDesignPresubject() {
        this(DSL.name("graduation_design_presubject"), null);
    }

    /**
     * Create an aliased <code>isy.graduation_design_presubject</code> table reference
     */
    public GraduationDesignPresubject(String alias) {
        this(DSL.name(alias), GRADUATION_DESIGN_PRESUBJECT);
    }

    /**
     * Create an aliased <code>isy.graduation_design_presubject</code> table reference
     */
    public GraduationDesignPresubject(Name alias) {
        this(alias, GRADUATION_DESIGN_PRESUBJECT);
    }

    private GraduationDesignPresubject(Name alias, Table<GraduationDesignPresubjectRecord> aliased) {
        this(alias, aliased, null);
    }

    private GraduationDesignPresubject(Name alias, Table<GraduationDesignPresubjectRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.GRADUATION_DESIGN_PRESUBJECT_GRADUATION_DESIGN_RELEASE_ID, Indexes.GRADUATION_DESIGN_PRESUBJECT_PRESUBJECT_TITLE, Indexes.GRADUATION_DESIGN_PRESUBJECT_PRIMARY, Indexes.GRADUATION_DESIGN_PRESUBJECT_STUDENT_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<GraduationDesignPresubjectRecord> getPrimaryKey() {
        return Keys.KEY_GRADUATION_DESIGN_PRESUBJECT_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GraduationDesignPresubjectRecord>> getKeys() {
        return Arrays.<UniqueKey<GraduationDesignPresubjectRecord>>asList(Keys.KEY_GRADUATION_DESIGN_PRESUBJECT_PRIMARY, Keys.KEY_GRADUATION_DESIGN_PRESUBJECT_PRESUBJECT_TITLE, Keys.KEY_GRADUATION_DESIGN_PRESUBJECT_GRADUATION_DESIGN_RELEASE_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<GraduationDesignPresubjectRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GraduationDesignPresubjectRecord, ?>>asList(Keys.GRADUATION_DESIGN_PRESUBJECT_IBFK_1, Keys.GRADUATION_DESIGN_PRESUBJECT_IBFK_2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignPresubject as(String alias) {
        return new GraduationDesignPresubject(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignPresubject as(Name alias) {
        return new GraduationDesignPresubject(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignPresubject rename(String name) {
        return new GraduationDesignPresubject(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignPresubject rename(Name name) {
        return new GraduationDesignPresubject(name, null);
    }
}
