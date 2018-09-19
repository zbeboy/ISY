/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
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
import top.zbeboy.isy.domain.tables.records.GraduationDesignDatumTypeRecord;


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
public class GraduationDesignDatumType extends TableImpl<GraduationDesignDatumTypeRecord> {

    private static final long serialVersionUID = 594012312;

    /**
     * The reference instance of <code>isy.graduation_design_datum_type</code>
     */
    public static final GraduationDesignDatumType GRADUATION_DESIGN_DATUM_TYPE = new GraduationDesignDatumType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GraduationDesignDatumTypeRecord> getRecordType() {
        return GraduationDesignDatumTypeRecord.class;
    }

    /**
     * The column <code>isy.graduation_design_datum_type.graduation_design_datum_type_id</code>.
     */
    public final TableField<GraduationDesignDatumTypeRecord, Integer> GRADUATION_DESIGN_DATUM_TYPE_ID = createField("graduation_design_datum_type_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>isy.graduation_design_datum_type.graduation_design_datum_type_name</code>.
     */
    public final TableField<GraduationDesignDatumTypeRecord, String> GRADUATION_DESIGN_DATUM_TYPE_NAME = createField("graduation_design_datum_type_name", org.jooq.impl.SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * Create a <code>isy.graduation_design_datum_type</code> table reference
     */
    public GraduationDesignDatumType() {
        this(DSL.name("graduation_design_datum_type"), null);
    }

    /**
     * Create an aliased <code>isy.graduation_design_datum_type</code> table reference
     */
    public GraduationDesignDatumType(String alias) {
        this(DSL.name(alias), GRADUATION_DESIGN_DATUM_TYPE);
    }

    /**
     * Create an aliased <code>isy.graduation_design_datum_type</code> table reference
     */
    public GraduationDesignDatumType(Name alias) {
        this(alias, GRADUATION_DESIGN_DATUM_TYPE);
    }

    private GraduationDesignDatumType(Name alias, Table<GraduationDesignDatumTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private GraduationDesignDatumType(Name alias, Table<GraduationDesignDatumTypeRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.GRADUATION_DESIGN_DATUM_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<GraduationDesignDatumTypeRecord, Integer> getIdentity() {
        return Keys.IDENTITY_GRADUATION_DESIGN_DATUM_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<GraduationDesignDatumTypeRecord> getPrimaryKey() {
        return Keys.KEY_GRADUATION_DESIGN_DATUM_TYPE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GraduationDesignDatumTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<GraduationDesignDatumTypeRecord>>asList(Keys.KEY_GRADUATION_DESIGN_DATUM_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumType as(String alias) {
        return new GraduationDesignDatumType(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumType as(Name alias) {
        return new GraduationDesignDatumType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignDatumType rename(String name) {
        return new GraduationDesignDatumType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignDatumType rename(Name name) {
        return new GraduationDesignDatumType(name, null);
    }
}
