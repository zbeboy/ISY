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
import top.zbeboy.isy.domain.tables.records.GraduationDesignReleaseFileRecord;


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
public class GraduationDesignReleaseFile extends TableImpl<GraduationDesignReleaseFileRecord> {

    private static final long serialVersionUID = 1351949439;

    /**
     * The reference instance of <code>isy.graduation_design_release_file</code>
     */
    public static final GraduationDesignReleaseFile GRADUATION_DESIGN_RELEASE_FILE = new GraduationDesignReleaseFile();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GraduationDesignReleaseFileRecord> getRecordType() {
        return GraduationDesignReleaseFileRecord.class;
    }

    /**
     * The column <code>isy.graduation_design_release_file.graduation_design_release_id</code>.
     */
    public final TableField<GraduationDesignReleaseFileRecord, String> GRADUATION_DESIGN_RELEASE_ID = createField("graduation_design_release_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_release_file.file_id</code>.
     */
    public final TableField<GraduationDesignReleaseFileRecord, String> FILE_ID = createField("file_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * Create a <code>isy.graduation_design_release_file</code> table reference
     */
    public GraduationDesignReleaseFile() {
        this(DSL.name("graduation_design_release_file"), null);
    }

    /**
     * Create an aliased <code>isy.graduation_design_release_file</code> table reference
     */
    public GraduationDesignReleaseFile(String alias) {
        this(DSL.name(alias), GRADUATION_DESIGN_RELEASE_FILE);
    }

    /**
     * Create an aliased <code>isy.graduation_design_release_file</code> table reference
     */
    public GraduationDesignReleaseFile(Name alias) {
        this(alias, GRADUATION_DESIGN_RELEASE_FILE);
    }

    private GraduationDesignReleaseFile(Name alias, Table<GraduationDesignReleaseFileRecord> aliased) {
        this(alias, aliased, null);
    }

    private GraduationDesignReleaseFile(Name alias, Table<GraduationDesignReleaseFileRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.GRADUATION_DESIGN_RELEASE_FILE_FILE_ID, Indexes.GRADUATION_DESIGN_RELEASE_FILE_GRADUATION_DESIGN_RELEASE_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<GraduationDesignReleaseFileRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GraduationDesignReleaseFileRecord, ?>>asList(Keys.GRADUATION_DESIGN_RELEASE_FILE_IBFK_1, Keys.GRADUATION_DESIGN_RELEASE_FILE_IBFK_2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseFile as(String alias) {
        return new GraduationDesignReleaseFile(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseFile as(Name alias) {
        return new GraduationDesignReleaseFile(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignReleaseFile rename(String name) {
        return new GraduationDesignReleaseFile(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignReleaseFile rename(Name name) {
        return new GraduationDesignReleaseFile(name, null);
    }
}
