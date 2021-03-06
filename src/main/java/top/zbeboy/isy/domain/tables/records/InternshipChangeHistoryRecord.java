/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.InternshipChangeHistory;


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
public class InternshipChangeHistoryRecord extends UpdatableRecordImpl<InternshipChangeHistoryRecord> implements Record8<String, String, Timestamp, Timestamp, Integer, String, Timestamp, Integer> {

    private static final long serialVersionUID = 919845451;

    /**
     * Setter for <code>isy.internship_change_history.internship_change_history_id</code>.
     */
    public void setInternshipChangeHistoryId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.internship_change_history.internship_change_history_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getInternshipChangeHistoryId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.internship_change_history.reason</code>.
     */
    public void setReason(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.internship_change_history.reason</code>.
     */
    @Size(max = 500)
    public String getReason() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.internship_change_history.change_fill_start_time</code>.
     */
    public void setChangeFillStartTime(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.internship_change_history.change_fill_start_time</code>.
     */
    public Timestamp getChangeFillStartTime() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>isy.internship_change_history.change_fill_end_time</code>.
     */
    public void setChangeFillEndTime(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.internship_change_history.change_fill_end_time</code>.
     */
    public Timestamp getChangeFillEndTime() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>isy.internship_change_history.student_id</code>.
     */
    public void setStudentId(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.internship_change_history.student_id</code>.
     */
    @NotNull
    public Integer getStudentId() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>isy.internship_change_history.internship_release_id</code>.
     */
    public void setInternshipReleaseId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.internship_change_history.internship_release_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getInternshipReleaseId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>isy.internship_change_history.apply_time</code>.
     */
    public void setApplyTime(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>isy.internship_change_history.apply_time</code>.
     */
    @NotNull
    public Timestamp getApplyTime() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>isy.internship_change_history.state</code>.
     */
    public void setState(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>isy.internship_change_history.state</code>.
     */
    @NotNull
    public Integer getState() {
        return (Integer) get(7);
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
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<String, String, Timestamp, Timestamp, Integer, String, Timestamp, Integer> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<String, String, Timestamp, Timestamp, Integer, String, Timestamp, Integer> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_CHANGE_HISTORY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY.REASON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field3() {
        return InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY.CHANGE_FILL_START_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY.CHANGE_FILL_END_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY.STUDENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_RELEASE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY.APPLY_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY.STATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getInternshipChangeHistoryId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getReason();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component3() {
        return getChangeFillStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component4() {
        return getChangeFillEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getInternshipReleaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
        return getApplyTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component8() {
        return getState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getInternshipChangeHistoryId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getReason();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value3() {
        return getChangeFillStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getChangeFillEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getInternshipReleaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getApplyTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipChangeHistoryRecord value1(String value) {
        setInternshipChangeHistoryId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipChangeHistoryRecord value2(String value) {
        setReason(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipChangeHistoryRecord value3(Timestamp value) {
        setChangeFillStartTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipChangeHistoryRecord value4(Timestamp value) {
        setChangeFillEndTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipChangeHistoryRecord value5(Integer value) {
        setStudentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipChangeHistoryRecord value6(String value) {
        setInternshipReleaseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipChangeHistoryRecord value7(Timestamp value) {
        setApplyTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipChangeHistoryRecord value8(Integer value) {
        setState(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipChangeHistoryRecord values(String value1, String value2, Timestamp value3, Timestamp value4, Integer value5, String value6, Timestamp value7, Integer value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached InternshipChangeHistoryRecord
     */
    public InternshipChangeHistoryRecord() {
        super(InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY);
    }

    /**
     * Create a detached, initialised InternshipChangeHistoryRecord
     */
    public InternshipChangeHistoryRecord(String internshipChangeHistoryId, String reason, Timestamp changeFillStartTime, Timestamp changeFillEndTime, Integer studentId, String internshipReleaseId, Timestamp applyTime, Integer state) {
        super(InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY);

        set(0, internshipChangeHistoryId);
        set(1, reason);
        set(2, changeFillStartTime);
        set(3, changeFillEndTime);
        set(4, studentId);
        set(5, internshipReleaseId);
        set(6, applyTime);
        set(7, state);
    }
}
