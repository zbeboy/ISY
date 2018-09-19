/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.sql.Date;
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
import top.zbeboy.isy.domain.tables.records.DefenseOrderRecord;


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
public class DefenseOrder extends TableImpl<DefenseOrderRecord> {

    private static final long serialVersionUID = 495819211;

    /**
     * The reference instance of <code>isy.defense_order</code>
     */
    public static final DefenseOrder DEFENSE_ORDER = new DefenseOrder();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DefenseOrderRecord> getRecordType() {
        return DefenseOrderRecord.class;
    }

    /**
     * The column <code>isy.defense_order.defense_order_id</code>.
     */
    public final TableField<DefenseOrderRecord, String> DEFENSE_ORDER_ID = createField("defense_order_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.defense_order.student_number</code>.
     */
    public final TableField<DefenseOrderRecord, String> STUDENT_NUMBER = createField("student_number", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>isy.defense_order.student_name</code>.
     */
    public final TableField<DefenseOrderRecord, String> STUDENT_NAME = createField("student_name", org.jooq.impl.SQLDataType.VARCHAR(30).nullable(false), this, "");

    /**
     * The column <code>isy.defense_order.student_mobile</code>.
     */
    public final TableField<DefenseOrderRecord, String> STUDENT_MOBILE = createField("student_mobile", org.jooq.impl.SQLDataType.VARCHAR(15).nullable(false), this, "");

    /**
     * The column <code>isy.defense_order.subject</code>.
     */
    public final TableField<DefenseOrderRecord, String> SUBJECT = createField("subject", org.jooq.impl.SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>isy.defense_order.defense_date</code>.
     */
    public final TableField<DefenseOrderRecord, Date> DEFENSE_DATE = createField("defense_date", org.jooq.impl.SQLDataType.DATE.nullable(false), this, "");

    /**
     * The column <code>isy.defense_order.defense_time</code>.
     */
    public final TableField<DefenseOrderRecord, String> DEFENSE_TIME = createField("defense_time", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>isy.defense_order.staff_name</code>.
     */
    public final TableField<DefenseOrderRecord, String> STAFF_NAME = createField("staff_name", org.jooq.impl.SQLDataType.VARCHAR(30).nullable(false), this, "");

    /**
     * The column <code>isy.defense_order.score_type_id</code>.
     */
    public final TableField<DefenseOrderRecord, Integer> SCORE_TYPE_ID = createField("score_type_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>isy.defense_order.sort_num</code>.
     */
    public final TableField<DefenseOrderRecord, Integer> SORT_NUM = createField("sort_num", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.defense_order.defense_status</code>.
     */
    public final TableField<DefenseOrderRecord, Integer> DEFENSE_STATUS = createField("defense_status", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>isy.defense_order.defense_question</code>.
     */
    public final TableField<DefenseOrderRecord, String> DEFENSE_QUESTION = createField("defense_question", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>isy.defense_order.student_id</code>.
     */
    public final TableField<DefenseOrderRecord, Integer> STUDENT_ID = createField("student_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.defense_order.defense_group_id</code>.
     */
    public final TableField<DefenseOrderRecord, String> DEFENSE_GROUP_ID = createField("defense_group_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * Create a <code>isy.defense_order</code> table reference
     */
    public DefenseOrder() {
        this(DSL.name("defense_order"), null);
    }

    /**
     * Create an aliased <code>isy.defense_order</code> table reference
     */
    public DefenseOrder(String alias) {
        this(DSL.name(alias), DEFENSE_ORDER);
    }

    /**
     * Create an aliased <code>isy.defense_order</code> table reference
     */
    public DefenseOrder(Name alias) {
        this(alias, DEFENSE_ORDER);
    }

    private DefenseOrder(Name alias, Table<DefenseOrderRecord> aliased) {
        this(alias, aliased, null);
    }

    private DefenseOrder(Name alias, Table<DefenseOrderRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.DEFENSE_ORDER_DEFENSE_GROUP_ID, Indexes.DEFENSE_ORDER_PRIMARY, Indexes.DEFENSE_ORDER_STUDENT_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DefenseOrderRecord> getPrimaryKey() {
        return Keys.KEY_DEFENSE_ORDER_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DefenseOrderRecord>> getKeys() {
        return Arrays.<UniqueKey<DefenseOrderRecord>>asList(Keys.KEY_DEFENSE_ORDER_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<DefenseOrderRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DefenseOrderRecord, ?>>asList(Keys.DEFENSE_ORDER_IBFK_1, Keys.DEFENSE_ORDER_IBFK_2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrder as(String alias) {
        return new DefenseOrder(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrder as(Name alias) {
        return new DefenseOrder(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DefenseOrder rename(String name) {
        return new DefenseOrder(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DefenseOrder rename(Name name) {
        return new DefenseOrder(name, null);
    }
}
