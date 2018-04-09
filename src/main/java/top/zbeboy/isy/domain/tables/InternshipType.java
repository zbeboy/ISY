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
import top.zbeboy.isy.domain.tables.records.InternshipTypeRecord;


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
public class InternshipType extends TableImpl<InternshipTypeRecord> {

    private static final long serialVersionUID = -354441655;

    /**
     * The reference instance of <code>isy.internship_type</code>
     */
    public static final InternshipType INTERNSHIP_TYPE = new InternshipType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<InternshipTypeRecord> getRecordType() {
        return InternshipTypeRecord.class;
    }

    /**
     * The column <code>isy.internship_type.internship_type_id</code>.
     */
    public final TableField<InternshipTypeRecord, Integer> INTERNSHIP_TYPE_ID = createField("internship_type_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>isy.internship_type.internship_type_name</code>.
     */
    public final TableField<InternshipTypeRecord, String> INTERNSHIP_TYPE_NAME = createField("internship_type_name", org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * Create a <code>isy.internship_type</code> table reference
     */
    public InternshipType() {
        this(DSL.name("internship_type"), null);
    }

    /**
     * Create an aliased <code>isy.internship_type</code> table reference
     */
    public InternshipType(String alias) {
        this(DSL.name(alias), INTERNSHIP_TYPE);
    }

    /**
     * Create an aliased <code>isy.internship_type</code> table reference
     */
    public InternshipType(Name alias) {
        this(alias, INTERNSHIP_TYPE);
    }

    private InternshipType(Name alias, Table<InternshipTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private InternshipType(Name alias, Table<InternshipTypeRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.INTERNSHIP_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<InternshipTypeRecord, Integer> getIdentity() {
        return Keys.IDENTITY_INTERNSHIP_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<InternshipTypeRecord> getPrimaryKey() {
        return Keys.KEY_INTERNSHIP_TYPE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<InternshipTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<InternshipTypeRecord>>asList(Keys.KEY_INTERNSHIP_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipType as(String alias) {
        return new InternshipType(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipType as(Name alias) {
        return new InternshipType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public InternshipType rename(String name) {
        return new InternshipType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public InternshipType rename(Name name) {
        return new InternshipType(name, null);
    }
}
