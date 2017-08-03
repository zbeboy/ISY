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
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.GraduateArchivesRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GraduateArchives extends TableImpl<GraduateArchivesRecord> {

    private static final long serialVersionUID = 633957103;

    /**
     * The reference instance of <code>isy.graduate_archives</code>
     */
    public static final GraduateArchives GRADUATE_ARCHIVES = new GraduateArchives();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GraduateArchivesRecord> getRecordType() {
        return GraduateArchivesRecord.class;
    }

    /**
     * The column <code>isy.graduate_archives.graduation_design_presubject_id</code>.
     */
    public final TableField<GraduateArchivesRecord, String> GRADUATION_DESIGN_PRESUBJECT_ID = createField("graduation_design_presubject_id", org.jooq.impl.SQLDataType.VARCHAR.length(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduate_archives.is_excellent</code>.
     */
    public final TableField<GraduateArchivesRecord, Byte> IS_EXCELLENT = createField("is_excellent", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>isy.graduate_archives.archive_number</code>.
     */
    public final TableField<GraduateArchivesRecord, String> ARCHIVE_NUMBER = createField("archive_number", org.jooq.impl.SQLDataType.VARCHAR.length(100).nullable(false), this, "");

    /**
     * The column <code>isy.graduate_archives.note</code>.
     */
    public final TableField<GraduateArchivesRecord, String> NOTE = createField("note", org.jooq.impl.SQLDataType.VARCHAR.length(100), this, "");

    /**
     * Create a <code>isy.graduate_archives</code> table reference
     */
    public GraduateArchives() {
        this("graduate_archives", null);
    }

    /**
     * Create an aliased <code>isy.graduate_archives</code> table reference
     */
    public GraduateArchives(String alias) {
        this(alias, GRADUATE_ARCHIVES);
    }

    private GraduateArchives(String alias, Table<GraduateArchivesRecord> aliased) {
        this(alias, aliased, null);
    }

    private GraduateArchives(String alias, Table<GraduateArchivesRecord> aliased, Field<?>[] parameters) {
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
    public List<UniqueKey<GraduateArchivesRecord>> getKeys() {
        return Arrays.<UniqueKey<GraduateArchivesRecord>>asList(Keys.KEY_GRADUATE_ARCHIVES_GRADUATION_DESIGN_PRESUBJECT_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<GraduateArchivesRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GraduateArchivesRecord, ?>>asList(Keys.GRADUATE_ARCHIVES_IBFK_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateArchives as(String alias) {
        return new GraduateArchives(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduateArchives rename(String name) {
        return new GraduateArchives(name, null);
    }
}
