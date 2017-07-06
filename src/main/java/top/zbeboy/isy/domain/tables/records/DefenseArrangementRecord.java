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

import top.zbeboy.isy.domain.tables.DefenseArrangement;


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
public class DefenseArrangementRecord extends UpdatableRecordImpl<DefenseArrangementRecord> implements Record9<String, Date, Date, Date, Date, String, String, String, String> {

    private static final long serialVersionUID = 1130137551;

    /**
     * Setter for <code>isy.defense_arrangement.defense_arrangement_id</code>.
     */
    public void setDefenseArrangementId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.defense_arrangement.defense_arrangement_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getDefenseArrangementId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.defense_arrangement.paper_start_date</code>.
     */
    public void setPaperStartDate(Date value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.defense_arrangement.paper_start_date</code>.
     */
    @NotNull
    public Date getPaperStartDate() {
        return (Date) get(1);
    }

    /**
     * Setter for <code>isy.defense_arrangement.paper_end_date</code>.
     */
    public void setPaperEndDate(Date value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.defense_arrangement.paper_end_date</code>.
     */
    @NotNull
    public Date getPaperEndDate() {
        return (Date) get(2);
    }

    /**
     * Setter for <code>isy.defense_arrangement.defense_start_date</code>.
     */
    public void setDefenseStartDate(Date value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.defense_arrangement.defense_start_date</code>.
     */
    @NotNull
    public Date getDefenseStartDate() {
        return (Date) get(3);
    }

    /**
     * Setter for <code>isy.defense_arrangement.defense_end_date</code>.
     */
    public void setDefenseEndDate(Date value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.defense_arrangement.defense_end_date</code>.
     */
    @NotNull
    public Date getDefenseEndDate() {
        return (Date) get(4);
    }

    /**
     * Setter for <code>isy.defense_arrangement.defense_start_time</code>.
     */
    public void setDefenseStartTime(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.defense_arrangement.defense_start_time</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getDefenseStartTime() {
        return (String) get(5);
    }

    /**
     * Setter for <code>isy.defense_arrangement.defense_end_time</code>.
     */
    public void setDefenseEndTime(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>isy.defense_arrangement.defense_end_time</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getDefenseEndTime() {
        return (String) get(6);
    }

    /**
     * Setter for <code>isy.defense_arrangement.defense_note</code>.
     */
    public void setDefenseNote(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>isy.defense_arrangement.defense_note</code>.
     */
    @Size(max = 100)
    public String getDefenseNote() {
        return (String) get(7);
    }

    /**
     * Setter for <code>isy.defense_arrangement.graduation_design_release_id</code>.
     */
    public void setGraduationDesignReleaseId(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>isy.defense_arrangement.graduation_design_release_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationDesignReleaseId() {
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
    public Row9<String, Date, Date, Date, Date, String, String, String, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row9<String, Date, Date, Date, Date, String, String, String, String> valuesRow() {
        return (Row9) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return DefenseArrangement.DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field2() {
        return DefenseArrangement.DEFENSE_ARRANGEMENT.PAPER_START_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field3() {
        return DefenseArrangement.DEFENSE_ARRANGEMENT.PAPER_END_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field4() {
        return DefenseArrangement.DEFENSE_ARRANGEMENT.DEFENSE_START_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field5() {
        return DefenseArrangement.DEFENSE_ARRANGEMENT.DEFENSE_END_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return DefenseArrangement.DEFENSE_ARRANGEMENT.DEFENSE_START_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return DefenseArrangement.DEFENSE_ARRANGEMENT.DEFENSE_END_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return DefenseArrangement.DEFENSE_ARRANGEMENT.DEFENSE_NOTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return DefenseArrangement.DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getDefenseArrangementId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value2() {
        return getPaperStartDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value3() {
        return getPaperEndDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value4() {
        return getDefenseStartDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value5() {
        return getDefenseEndDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getDefenseStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getDefenseEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getDefenseNote();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getGraduationDesignReleaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangementRecord value1(String value) {
        setDefenseArrangementId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangementRecord value2(Date value) {
        setPaperStartDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangementRecord value3(Date value) {
        setPaperEndDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangementRecord value4(Date value) {
        setDefenseStartDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangementRecord value5(Date value) {
        setDefenseEndDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangementRecord value6(String value) {
        setDefenseStartTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangementRecord value7(String value) {
        setDefenseEndTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangementRecord value8(String value) {
        setDefenseNote(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangementRecord value9(String value) {
        setGraduationDesignReleaseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseArrangementRecord values(String value1, Date value2, Date value3, Date value4, Date value5, String value6, String value7, String value8, String value9) {
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
     * Create a detached DefenseArrangementRecord
     */
    public DefenseArrangementRecord() {
        super(DefenseArrangement.DEFENSE_ARRANGEMENT);
    }

    /**
     * Create a detached, initialised DefenseArrangementRecord
     */
    public DefenseArrangementRecord(String defenseArrangementId, Date paperStartDate, Date paperEndDate, Date defenseStartDate, Date defenseEndDate, String defenseStartTime, String defenseEndTime, String defenseNote, String graduationDesignReleaseId) {
        super(DefenseArrangement.DEFENSE_ARRANGEMENT);

        set(0, defenseArrangementId);
        set(1, paperStartDate);
        set(2, paperEndDate);
        set(3, defenseStartDate);
        set(4, defenseEndDate);
        set(5, defenseStartTime);
        set(6, defenseEndTime);
        set(7, defenseNote);
        set(8, graduationDesignReleaseId);
    }
}
