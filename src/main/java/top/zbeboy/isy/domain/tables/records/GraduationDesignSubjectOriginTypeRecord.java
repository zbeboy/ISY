/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.GraduationDesignSubjectOriginType;


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
public class GraduationDesignSubjectOriginTypeRecord extends UpdatableRecordImpl<GraduationDesignSubjectOriginTypeRecord> implements Record2<Integer, String> {

    private static final long serialVersionUID = -625747938;

    /**
     * Setter for <code>isy.graduation_design_subject_origin_type.origin_type_id</code>.
     */
    public void setOriginTypeId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.graduation_design_subject_origin_type.origin_type_id</code>.
     */
    public Integer getOriginTypeId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>isy.graduation_design_subject_origin_type.origin_type_name</code>.
     */
    public void setOriginTypeName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.graduation_design_subject_origin_type.origin_type_name</code>.
     */
    @NotNull
    @Size(max = 30)
    public String getOriginTypeName() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Integer, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return GraduationDesignSubjectOriginType.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return GraduationDesignSubjectOriginType.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getOriginTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getOriginTypeName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getOriginTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getOriginTypeName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignSubjectOriginTypeRecord value1(Integer value) {
        setOriginTypeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignSubjectOriginTypeRecord value2(String value) {
        setOriginTypeName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignSubjectOriginTypeRecord values(Integer value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GraduationDesignSubjectOriginTypeRecord
     */
    public GraduationDesignSubjectOriginTypeRecord() {
        super(GraduationDesignSubjectOriginType.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE);
    }

    /**
     * Create a detached, initialised GraduationDesignSubjectOriginTypeRecord
     */
    public GraduationDesignSubjectOriginTypeRecord(Integer originTypeId, String originTypeName) {
        super(GraduationDesignSubjectOriginType.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE);

        set(0, originTypeId);
        set(1, originTypeName);
    }
}
