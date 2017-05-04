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
import top.zbeboy.isy.domain.tables.records.ScoreTypeRecord;


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
public class ScoreType extends TableImpl<ScoreTypeRecord> {

    private static final long serialVersionUID = 1145193941;

    /**
     * The reference instance of <code>isy.score_type</code>
     */
    public static final ScoreType SCORE_TYPE = new ScoreType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ScoreTypeRecord> getRecordType() {
        return ScoreTypeRecord.class;
    }

    /**
     * The column <code>isy.score_type.score_type_id</code>.
     */
    public final TableField<ScoreTypeRecord, Integer> SCORE_TYPE_ID = createField("score_type_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.score_type.score_type_name</code>.
     */
    public final TableField<ScoreTypeRecord, String> SCORE_TYPE_NAME = createField("score_type_name", org.jooq.impl.SQLDataType.VARCHAR.length(20).nullable(false), this, "");

    /**
     * Create a <code>isy.score_type</code> table reference
     */
    public ScoreType() {
        this("score_type", null);
    }

    /**
     * Create an aliased <code>isy.score_type</code> table reference
     */
    public ScoreType(String alias) {
        this(alias, SCORE_TYPE);
    }

    private ScoreType(String alias, Table<ScoreTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private ScoreType(String alias, Table<ScoreTypeRecord> aliased, Field<?>[] parameters) {
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
    public Identity<ScoreTypeRecord, Integer> getIdentity() {
        return Keys.IDENTITY_SCORE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ScoreTypeRecord> getPrimaryKey() {
        return Keys.KEY_SCORE_TYPE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ScoreTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<ScoreTypeRecord>>asList(Keys.KEY_SCORE_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreType as(String alias) {
        return new ScoreType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ScoreType rename(String name) {
        return new ScoreType(name, null);
    }
}
