/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.GraduateBill;


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
public class GraduateBillRecord extends UpdatableRecordImpl<GraduateBillRecord> implements Record3<String, String, String> {

    private static final long serialVersionUID = 1393552574;

    /**
     * Setter for <code>isy.graduate_bill.graduate_bill_id</code>.
     */
    public void setGraduateBillId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.graduate_bill.graduate_bill_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduateBillId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.graduate_bill.graduation_design_release_id</code>.
     */
    public void setGraduationDesignReleaseId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.graduate_bill.graduation_design_release_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationDesignReleaseId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.graduate_bill.graduation_design_presubject_id</code>.
     */
    public void setGraduationDesignPresubjectId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.graduate_bill.graduation_design_presubject_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationDesignPresubjectId() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return GraduateBill.GRADUATE_BILL.GRADUATE_BILL_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return GraduateBill.GRADUATE_BILL.GRADUATION_DESIGN_RELEASE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return GraduateBill.GRADUATE_BILL.GRADUATION_DESIGN_PRESUBJECT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getGraduateBillId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getGraduationDesignReleaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getGraduationDesignPresubjectId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateBillRecord value1(String value) {
        setGraduateBillId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateBillRecord value2(String value) {
        setGraduationDesignReleaseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateBillRecord value3(String value) {
        setGraduationDesignPresubjectId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateBillRecord values(String value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GraduateBillRecord
     */
    public GraduateBillRecord() {
        super(GraduateBill.GRADUATE_BILL);
    }

    /**
     * Create a detached, initialised GraduateBillRecord
     */
    public GraduateBillRecord(String graduateBillId, String graduationDesignReleaseId, String graduationDesignPresubjectId) {
        super(GraduateBill.GRADUATE_BILL);

        set(0, graduateBillId);
        set(1, graduationDesignReleaseId);
        set(2, graduationDesignPresubjectId);
    }
}
