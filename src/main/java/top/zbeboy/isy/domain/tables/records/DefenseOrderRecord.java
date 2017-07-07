/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import java.sql.Date;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.DefenseOrder;


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
public class DefenseOrderRecord extends UpdatableRecordImpl<DefenseOrderRecord> implements Record9<String, String, String, String, Date, String, String, Integer, String> {

    private static final long serialVersionUID = -914474162;

    /**
     * Setter for <code>isy.defense_order.defense_order_id</code>.
     */
    public void setDefenseOrderId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.defense_order.defense_order_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getDefenseOrderId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.defense_order.student_number</code>.
     */
    public void setStudentNumber(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.defense_order.student_number</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getStudentNumber() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.defense_order.student_name</code>.
     */
    public void setStudentName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.defense_order.student_name</code>.
     */
    @NotNull
    @Size(max = 30)
    public String getStudentName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>isy.defense_order.subject</code>.
     */
    public void setSubject(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.defense_order.subject</code>.
     */
    @NotNull
    @Size(max = 100)
    public String getSubject() {
        return (String) get(3);
    }

    /**
     * Setter for <code>isy.defense_order.defense_date</code>.
     */
    public void setDefenseDate(Date value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.defense_order.defense_date</code>.
     */
    @NotNull
    public Date getDefenseDate() {
        return (Date) get(4);
    }

    /**
     * Setter for <code>isy.defense_order.defense_time</code>.
     */
    public void setDefenseTime(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.defense_order.defense_time</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getDefenseTime() {
        return (String) get(5);
    }

    /**
     * Setter for <code>isy.defense_order.staff_name</code>.
     */
    public void setStaffName(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>isy.defense_order.staff_name</code>.
     */
    @NotNull
    @Size(max = 30)
    public String getStaffName() {
        return (String) get(6);
    }

    /**
     * Setter for <code>isy.defense_order.score_type_id</code>.
     */
    public void setScoreTypeId(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>isy.defense_order.score_type_id</code>.
     */
    @NotNull
    public Integer getScoreTypeId() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>isy.defense_order.graduation_design_tutor_id</code>.
     */
    public void setGraduationDesignTutorId(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>isy.defense_order.graduation_design_tutor_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationDesignTutorId() {
        return (String) get(8);
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
    // Record9 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row9<String, String, String, String, Date, String, String, Integer, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row9<String, String, String, String, Date, String, String, Integer, String> valuesRow() {
        return (Row9) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return DefenseOrder.DEFENSE_ORDER.DEFENSE_ORDER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return DefenseOrder.DEFENSE_ORDER.STUDENT_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return DefenseOrder.DEFENSE_ORDER.STUDENT_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return DefenseOrder.DEFENSE_ORDER.SUBJECT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field5() {
        return DefenseOrder.DEFENSE_ORDER.DEFENSE_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return DefenseOrder.DEFENSE_ORDER.DEFENSE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return DefenseOrder.DEFENSE_ORDER.STAFF_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return DefenseOrder.DEFENSE_ORDER.SCORE_TYPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return DefenseOrder.DEFENSE_ORDER.GRADUATION_DESIGN_TUTOR_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getDefenseOrderId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getStudentNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getStudentName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getSubject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value5() {
        return getDefenseDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getDefenseTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getStaffName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getScoreTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getGraduationDesignTutorId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrderRecord value1(String value) {
        setDefenseOrderId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrderRecord value2(String value) {
        setStudentNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrderRecord value3(String value) {
        setStudentName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrderRecord value4(String value) {
        setSubject(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrderRecord value5(Date value) {
        setDefenseDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrderRecord value6(String value) {
        setDefenseTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrderRecord value7(String value) {
        setStaffName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrderRecord value8(Integer value) {
        setScoreTypeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrderRecord value9(String value) {
        setGraduationDesignTutorId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseOrderRecord values(String value1, String value2, String value3, String value4, Date value5, String value6, String value7, Integer value8, String value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DefenseOrderRecord
     */
    public DefenseOrderRecord() {
        super(DefenseOrder.DEFENSE_ORDER);
    }

    /**
     * Create a detached, initialised DefenseOrderRecord
     */
    public DefenseOrderRecord(String defenseOrderId, String studentNumber, String studentName, String subject, Date defenseDate, String defenseTime, String staffName, Integer scoreTypeId, String graduationDesignTutorId) {
        super(DefenseOrder.DEFENSE_ORDER);

        set(0, defenseOrderId);
        set(1, studentNumber);
        set(2, studentName);
        set(3, subject);
        set(4, defenseDate);
        set(5, defenseTime);
        set(6, staffName);
        set(7, scoreTypeId);
        set(8, graduationDesignTutorId);
    }
}
