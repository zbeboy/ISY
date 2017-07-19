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

import top.zbeboy.isy.domain.tables.DefenseGroup;


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
public class DefenseGroupRecord extends UpdatableRecordImpl<DefenseGroupRecord> implements Record8<String, String, Integer, String, String, Integer, String, Timestamp> {

    private static final long serialVersionUID = 184695313;

    /**
     * Setter for <code>isy.defense_group.defense_group_id</code>.
     */
    public void setDefenseGroupId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.defense_group.defense_group_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getDefenseGroupId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.defense_group.defense_group_name</code>.
     */
    public void setDefenseGroupName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.defense_group.defense_group_name</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getDefenseGroupName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.defense_group.schoolroom_id</code>.
     */
    public void setSchoolroomId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.defense_group.schoolroom_id</code>.
     */
    @NotNull
    public Integer getSchoolroomId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>isy.defense_group.note</code>.
     */
    public void setNote(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.defense_group.note</code>.
     */
    @Size(max = 100)
    public String getNote() {
        return (String) get(3);
    }

    /**
     * Setter for <code>isy.defense_group.leader_id</code>.
     */
    public void setLeaderId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.defense_group.leader_id</code>.
     */
    @Size(max = 64)
    public String getLeaderId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>isy.defense_group.secretary_id</code>.
     */
    public void setSecretaryId(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.defense_group.secretary_id</code>.
     */
    public Integer getSecretaryId() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>isy.defense_group.defense_arrangement_id</code>.
     */
    public void setDefenseArrangementId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>isy.defense_group.defense_arrangement_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getDefenseArrangementId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>isy.defense_group.create_time</code>.
     */
    public void setCreateTime(Timestamp value) {
        set(7, value);
    }

    /**
     * Getter for <code>isy.defense_group.create_time</code>.
     */
    @NotNull
    public Timestamp getCreateTime() {
        return (Timestamp) get(7);
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
    public Row8<String, String, Integer, String, String, Integer, String, Timestamp> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<String, String, Integer, String, String, Integer, String, Timestamp> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return DefenseGroup.DEFENSE_GROUP.DEFENSE_GROUP_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return DefenseGroup.DEFENSE_GROUP.DEFENSE_GROUP_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return DefenseGroup.DEFENSE_GROUP.SCHOOLROOM_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return DefenseGroup.DEFENSE_GROUP.NOTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return DefenseGroup.DEFENSE_GROUP.LEADER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field6() {
        return DefenseGroup.DEFENSE_GROUP.SECRETARY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return DefenseGroup.DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field8() {
        return DefenseGroup.DEFENSE_GROUP.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getDefenseGroupId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getDefenseGroupName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getSchoolroomId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getNote();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getLeaderId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value6() {
        return getSecretaryId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getDefenseArrangementId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value8() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseGroupRecord value1(String value) {
        setDefenseGroupId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseGroupRecord value2(String value) {
        setDefenseGroupName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseGroupRecord value3(Integer value) {
        setSchoolroomId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseGroupRecord value4(String value) {
        setNote(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseGroupRecord value5(String value) {
        setLeaderId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseGroupRecord value6(Integer value) {
        setSecretaryId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseGroupRecord value7(String value) {
        setDefenseArrangementId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseGroupRecord value8(Timestamp value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefenseGroupRecord values(String value1, String value2, Integer value3, String value4, String value5, Integer value6, String value7, Timestamp value8) {
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
     * Create a detached DefenseGroupRecord
     */
    public DefenseGroupRecord() {
        super(DefenseGroup.DEFENSE_GROUP);
    }

    /**
     * Create a detached, initialised DefenseGroupRecord
     */
    public DefenseGroupRecord(String defenseGroupId, String defenseGroupName, Integer schoolroomId, String note, String leaderId, Integer secretaryId, String defenseArrangementId, Timestamp createTime) {
        super(DefenseGroup.DEFENSE_GROUP);

        set(0, defenseGroupId);
        set(1, defenseGroupName);
        set(2, schoolroomId);
        set(3, note);
        set(4, leaderId);
        set(5, secretaryId);
        set(6, defenseArrangementId);
        set(7, createTime);
    }
}
