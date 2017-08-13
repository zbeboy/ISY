/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.GraduationDesignSubjectTypeRecord;


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
public class GraduationDesignSubjectType extends TableImpl<GraduationDesignSubjectTypeRecord> {

    private static final long serialVersionUID = -1643912731;

    /**
     * The reference instance of <code>isy.graduation_design_subject_type</code>
     */
    public static final GraduationDesignSubjectType GRADUATION_DESIGN_SUBJECT_TYPE = new GraduationDesignSubjectType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GraduationDesignSubjectTypeRecord> getRecordType() {
        return GraduationDesignSubjectTypeRecord.class;
    }

    /**
     * The column <code>isy.graduation_design_subject_type.subject_type_id</code>.
     */
    public final TableField<GraduationDesignSubjectTypeRecord, Integer> SUBJECT_TYPE_ID = createField("subject_type_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_subject_type.subject_type_name</code>.
     */
    public final TableField<GraduationDesignSubjectTypeRecord, String> SUBJECT_TYPE_NAME = createField("subject_type_name", org.jooq.impl.SQLDataType.VARCHAR.length(30).nullable(false), this, "");

    /**
     * Create a <code>isy.graduation_design_subject_type</code> table reference
     */
    public GraduationDesignSubjectType() {
        this("graduation_design_subject_type", null);
    }

    /**
     * Create an aliased <code>isy.graduation_design_subject_type</code> table reference
     */
    public GraduationDesignSubjectType(String alias) {
        this(alias, GRADUATION_DESIGN_SUBJECT_TYPE);
    }

    private GraduationDesignSubjectType(String alias, Table<GraduationDesignSubjectTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private GraduationDesignSubjectType(String alias, Table<GraduationDesignSubjectTypeRecord> aliased, Field<?>[] parameters) {
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
    public Identity<GraduationDesignSubjectTypeRecord, Integer> getIdentity() {
        return Keys.IDENTITY_GRADUATION_DESIGN_SUBJECT_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<GraduationDesignSubjectTypeRecord> getPrimaryKey() {
        return Keys.KEY_GRADUATION_DESIGN_SUBJECT_TYPE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GraduationDesignSubjectTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<GraduationDesignSubjectTypeRecord>>asList(Keys.KEY_GRADUATION_DESIGN_SUBJECT_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignSubjectType as(String alias) {
        return new GraduationDesignSubjectType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignSubjectType rename(String name) {
        return new GraduationDesignSubjectType(name, null);
    }
}