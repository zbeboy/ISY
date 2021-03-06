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
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Indexes;
import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.GraduationDesignDeclareDataRecord;


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
public class GraduationDesignDeclareData extends TableImpl<GraduationDesignDeclareDataRecord> {

    private static final long serialVersionUID = -1535725176;

    /**
     * The reference instance of <code>isy.graduation_design_declare_data</code>
     */
    public static final GraduationDesignDeclareData GRADUATION_DESIGN_DECLARE_DATA = new GraduationDesignDeclareData();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GraduationDesignDeclareDataRecord> getRecordType() {
        return GraduationDesignDeclareDataRecord.class;
    }

    /**
     * The column <code>isy.graduation_design_declare_data.graduation_design_declare_data_id</code>.
     */
    public final TableField<GraduationDesignDeclareDataRecord, String> GRADUATION_DESIGN_DECLARE_DATA_ID = createField("graduation_design_declare_data_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_declare_data.graduation_date</code>.
     */
    public final TableField<GraduationDesignDeclareDataRecord, String> GRADUATION_DATE = createField("graduation_date", org.jooq.impl.SQLDataType.VARCHAR(30), this, "");

    /**
     * The column <code>isy.graduation_design_declare_data.department_name</code>.
     */
    public final TableField<GraduationDesignDeclareDataRecord, String> DEPARTMENT_NAME = createField("department_name", org.jooq.impl.SQLDataType.VARCHAR(200), this, "");

    /**
     * The column <code>isy.graduation_design_declare_data.science_name</code>.
     */
    public final TableField<GraduationDesignDeclareDataRecord, String> SCIENCE_NAME = createField("science_name", org.jooq.impl.SQLDataType.VARCHAR(200), this, "");

    /**
     * The column <code>isy.graduation_design_declare_data.organize_names</code>.
     */
    public final TableField<GraduationDesignDeclareDataRecord, String> ORGANIZE_NAMES = createField("organize_names", org.jooq.impl.SQLDataType.VARCHAR(150), this, "");

    /**
     * The column <code>isy.graduation_design_declare_data.organize_peoples</code>.
     */
    public final TableField<GraduationDesignDeclareDataRecord, String> ORGANIZE_PEOPLES = createField("organize_peoples", org.jooq.impl.SQLDataType.VARCHAR(150), this, "");

    /**
     * The column <code>isy.graduation_design_declare_data.graduation_design_release_id</code>.
     */
    public final TableField<GraduationDesignDeclareDataRecord, String> GRADUATION_DESIGN_RELEASE_ID = createField("graduation_design_release_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * Create a <code>isy.graduation_design_declare_data</code> table reference
     */
    public GraduationDesignDeclareData() {
        this(DSL.name("graduation_design_declare_data"), null);
    }

    /**
     * Create an aliased <code>isy.graduation_design_declare_data</code> table reference
     */
    public GraduationDesignDeclareData(String alias) {
        this(DSL.name(alias), GRADUATION_DESIGN_DECLARE_DATA);
    }

    /**
     * Create an aliased <code>isy.graduation_design_declare_data</code> table reference
     */
    public GraduationDesignDeclareData(Name alias) {
        this(alias, GRADUATION_DESIGN_DECLARE_DATA);
    }

    private GraduationDesignDeclareData(Name alias, Table<GraduationDesignDeclareDataRecord> aliased) {
        this(alias, aliased, null);
    }

    private GraduationDesignDeclareData(Name alias, Table<GraduationDesignDeclareDataRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.GRADUATION_DESIGN_DECLARE_DATA_GRADUATION_DESIGN_RELEASE_ID, Indexes.GRADUATION_DESIGN_DECLARE_DATA_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<GraduationDesignDeclareDataRecord> getPrimaryKey() {
        return Keys.KEY_GRADUATION_DESIGN_DECLARE_DATA_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GraduationDesignDeclareDataRecord>> getKeys() {
        return Arrays.<UniqueKey<GraduationDesignDeclareDataRecord>>asList(Keys.KEY_GRADUATION_DESIGN_DECLARE_DATA_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<GraduationDesignDeclareDataRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GraduationDesignDeclareDataRecord, ?>>asList(Keys.GRADUATION_DESIGN_DECLARE_DATA_IBFK_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDeclareData as(String alias) {
        return new GraduationDesignDeclareData(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDeclareData as(Name alias) {
        return new GraduationDesignDeclareData(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignDeclareData rename(String name) {
        return new GraduationDesignDeclareData(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignDeclareData rename(Name name) {
        return new GraduationDesignDeclareData(name, null);
    }
}
