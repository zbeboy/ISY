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
import top.zbeboy.isy.domain.tables.records.GraduationDesignDatumGroupRecord;


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
public class GraduationDesignDatumGroup extends TableImpl<GraduationDesignDatumGroupRecord> {

    private static final long serialVersionUID = 596591790;

    /**
     * The reference instance of <code>isy.graduation_design_datum_group</code>
     */
    public static final GraduationDesignDatumGroup GRADUATION_DESIGN_DATUM_GROUP = new GraduationDesignDatumGroup();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GraduationDesignDatumGroupRecord> getRecordType() {
        return GraduationDesignDatumGroupRecord.class;
    }

    /**
     * The column <code>isy.graduation_design_datum_group.graduation_design_datum_group_id</code>.
     */
    public final TableField<GraduationDesignDatumGroupRecord, String> GRADUATION_DESIGN_DATUM_GROUP_ID = createField("graduation_design_datum_group_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_datum_group.file_id</code>.
     */
    public final TableField<GraduationDesignDatumGroupRecord, String> FILE_ID = createField("file_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_datum_group.graduation_design_teacher_id</code>.
     */
    public final TableField<GraduationDesignDatumGroupRecord, String> GRADUATION_DESIGN_TEACHER_ID = createField("graduation_design_teacher_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_datum_group.upload_time</code>.
     */
    public final TableField<GraduationDesignDatumGroupRecord, Timestamp> UPLOAD_TIME = createField("upload_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * Create a <code>isy.graduation_design_datum_group</code> table reference
     */
    public GraduationDesignDatumGroup() {
        this(DSL.name("graduation_design_datum_group"), null);
    }

    /**
     * Create an aliased <code>isy.graduation_design_datum_group</code> table reference
     */
    public GraduationDesignDatumGroup(String alias) {
        this(DSL.name(alias), GRADUATION_DESIGN_DATUM_GROUP);
    }

    /**
     * Create an aliased <code>isy.graduation_design_datum_group</code> table reference
     */
    public GraduationDesignDatumGroup(Name alias) {
        this(alias, GRADUATION_DESIGN_DATUM_GROUP);
    }

    private GraduationDesignDatumGroup(Name alias, Table<GraduationDesignDatumGroupRecord> aliased) {
        this(alias, aliased, null);
    }

    private GraduationDesignDatumGroup(Name alias, Table<GraduationDesignDatumGroupRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.GRADUATION_DESIGN_DATUM_GROUP_FILE_ID, Indexes.GRADUATION_DESIGN_DATUM_GROUP_GRADUATION_DESIGN_TEACHER_ID, Indexes.GRADUATION_DESIGN_DATUM_GROUP_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<GraduationDesignDatumGroupRecord> getPrimaryKey() {
        return Keys.KEY_GRADUATION_DESIGN_DATUM_GROUP_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GraduationDesignDatumGroupRecord>> getKeys() {
        return Arrays.<UniqueKey<GraduationDesignDatumGroupRecord>>asList(Keys.KEY_GRADUATION_DESIGN_DATUM_GROUP_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<GraduationDesignDatumGroupRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GraduationDesignDatumGroupRecord, ?>>asList(Keys.GRADUATION_DESIGN_DATUM_GROUP_IBFK_1, Keys.GRADUATION_DESIGN_DATUM_GROUP_IBFK_2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumGroup as(String alias) {
        return new GraduationDesignDatumGroup(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumGroup as(Name alias) {
        return new GraduationDesignDatumGroup(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignDatumGroup rename(String name) {
        return new GraduationDesignDatumGroup(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignDatumGroup rename(Name name) {
        return new GraduationDesignDatumGroup(name, null);
    }
}
