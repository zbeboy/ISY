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

import top.zbeboy.isy.domain.tables.InternshipType;


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
public class InternshipTypeRecord extends UpdatableRecordImpl<InternshipTypeRecord> implements Record2<Integer, String> {

    private static final long serialVersionUID = 1765678209;

    /**
     * Setter for <code>isy.internship_type.internship_type_id</code>.
     */
    public void setInternshipTypeId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.internship_type.internship_type_id</code>.
     */
    public Integer getInternshipTypeId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>isy.internship_type.internship_type_name</code>.
     */
    public void setInternshipTypeName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.internship_type.internship_type_name</code>.
     */
    @NotNull
    @Size(max = 100)
    public String getInternshipTypeName() {
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
        return InternshipType.INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return InternshipType.INTERNSHIP_TYPE.INTERNSHIP_TYPE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getInternshipTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getInternshipTypeName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getInternshipTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getInternshipTypeName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipTypeRecord value1(Integer value) {
        setInternshipTypeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipTypeRecord value2(String value) {
        setInternshipTypeName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipTypeRecord values(Integer value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached InternshipTypeRecord
     */
    public InternshipTypeRecord() {
        super(InternshipType.INTERNSHIP_TYPE);
    }

    /**
     * Create a detached, initialised InternshipTypeRecord
     */
    public InternshipTypeRecord(Integer internshipTypeId, String internshipTypeName) {
        super(InternshipType.INTERNSHIP_TYPE);

        set(0, internshipTypeId);
        set(1, internshipTypeName);
    }
}
