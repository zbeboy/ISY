/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
import top.zbeboy.isy.domain.tables.records.OrganizeRecord;


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
public class Organize extends TableImpl<OrganizeRecord> {

    private static final long serialVersionUID = 843679690;

    /**
     * The reference instance of <code>isy.organize</code>
     */
    public static final Organize ORGANIZE = new Organize();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrganizeRecord> getRecordType() {
        return OrganizeRecord.class;
    }

    /**
     * The column <code>isy.organize.organize_id</code>.
     */
    public final TableField<OrganizeRecord, Integer> ORGANIZE_ID = createField("organize_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>isy.organize.organize_name</code>.
     */
    public final TableField<OrganizeRecord, String> ORGANIZE_NAME = createField("organize_name", org.jooq.impl.SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>isy.organize.organize_is_del</code>.
     */
    public final TableField<OrganizeRecord, Byte> ORGANIZE_IS_DEL = createField("organize_is_del", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>isy.organize.science_id</code>.
     */
    public final TableField<OrganizeRecord, Integer> SCIENCE_ID = createField("science_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.organize.grade</code>.
     */
    public final TableField<OrganizeRecord, String> GRADE = createField("grade", org.jooq.impl.SQLDataType.VARCHAR(5).nullable(false), this, "");

    /**
     * Create a <code>isy.organize</code> table reference
     */
    public Organize() {
        this(DSL.name("organize"), null);
    }

    /**
     * Create an aliased <code>isy.organize</code> table reference
     */
    public Organize(String alias) {
        this(DSL.name(alias), ORGANIZE);
    }

    /**
     * Create an aliased <code>isy.organize</code> table reference
     */
    public Organize(Name alias) {
        this(alias, ORGANIZE);
    }

    private Organize(Name alias, Table<OrganizeRecord> aliased) {
        this(alias, aliased, null);
    }

    private Organize(Name alias, Table<OrganizeRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.ORGANIZE_PRIMARY, Indexes.ORGANIZE_SCIENCE_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<OrganizeRecord, Integer> getIdentity() {
        return Keys.IDENTITY_ORGANIZE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<OrganizeRecord> getPrimaryKey() {
        return Keys.KEY_ORGANIZE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<OrganizeRecord>> getKeys() {
        return Arrays.<UniqueKey<OrganizeRecord>>asList(Keys.KEY_ORGANIZE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<OrganizeRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<OrganizeRecord, ?>>asList(Keys.ORGANIZE_IBFK_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Organize as(String alias) {
        return new Organize(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Organize as(Name alias) {
        return new Organize(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Organize rename(String name) {
        return new Organize(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Organize rename(Name name) {
        return new Organize(name, null);
    }
}
