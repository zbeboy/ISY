/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.GraduationDesignDatumRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GraduationDesignDatum extends TableImpl<GraduationDesignDatumRecord> {

    private static final long serialVersionUID = 1080002407;

    /**
     * The reference instance of <code>isy.graduation_design_datum</code>
     */
    public static final GraduationDesignDatum GRADUATION_DESIGN_DATUM = new GraduationDesignDatum();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GraduationDesignDatumRecord> getRecordType() {
        return GraduationDesignDatumRecord.class;
    }

    /**
     * The column <code>isy.graduation_design_datum.graduation_design_datum_id</code>.
     */
    public final TableField<GraduationDesignDatumRecord, String> GRADUATION_DESIGN_DATUM_ID = createField("graduation_design_datum_id", org.jooq.impl.SQLDataType.VARCHAR.length(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_datum.version</code>.
     */
    public final TableField<GraduationDesignDatumRecord, String> VERSION = createField("version", org.jooq.impl.SQLDataType.VARCHAR.length(10), this, "");

    /**
     * The column <code>isy.graduation_design_datum.file_id</code>.
     */
    public final TableField<GraduationDesignDatumRecord, String> FILE_ID = createField("file_id", org.jooq.impl.SQLDataType.VARCHAR.length(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_datum.graduation_design_datum_type_id</code>.
     */
    public final TableField<GraduationDesignDatumRecord, Integer> GRADUATION_DESIGN_DATUM_TYPE_ID = createField("graduation_design_datum_type_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_datum.graduation_design_release_id</code>.
     */
    public final TableField<GraduationDesignDatumRecord, String> GRADUATION_DESIGN_RELEASE_ID = createField("graduation_design_release_id", org.jooq.impl.SQLDataType.VARCHAR.length(64).nullable(false), this, "");

    /**
     * Create a <code>isy.graduation_design_datum</code> table reference
     */
    public GraduationDesignDatum() {
        this("graduation_design_datum", null);
    }

    /**
     * Create an aliased <code>isy.graduation_design_datum</code> table reference
     */
    public GraduationDesignDatum(String alias) {
        this(alias, GRADUATION_DESIGN_DATUM);
    }

    private GraduationDesignDatum(String alias, Table<GraduationDesignDatumRecord> aliased) {
        this(alias, aliased, null);
    }

    private GraduationDesignDatum(String alias, Table<GraduationDesignDatumRecord> aliased, Field<?>[] parameters) {
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
    public List<ForeignKey<GraduationDesignDatumRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GraduationDesignDatumRecord, ?>>asList(Keys.GRADUATION_DESIGN_DATUM_IBFK_2, Keys.GRADUATION_DESIGN_DATUM_IBFK_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatum as(String alias) {
        return new GraduationDesignDatum(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignDatum rename(String name) {
        return new GraduationDesignDatum(name, null);
    }
}
