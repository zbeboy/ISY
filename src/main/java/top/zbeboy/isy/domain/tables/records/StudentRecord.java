/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.Student;


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
public class StudentRecord extends UpdatableRecordImpl<StudentRecord> implements Record13<Integer, String, String, String, String, Integer, Integer, String, String, String, String, Integer, String> {

    private static final long serialVersionUID = 1861557730;

    /**
     * Setter for <code>isy.student.student_id</code>.
     */
    public void setStudentId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.student.student_id</code>.
     */
    public Integer getStudentId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>isy.student.student_number</code>.
     */
    public void setStudentNumber(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.student.student_number</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getStudentNumber() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.student.birthday</code>.
     */
    public void setBirthday(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.student.birthday</code>.
     */
    @Size(max = 48)
    public String getBirthday() {
        return (String) get(2);
    }

    /**
     * Setter for <code>isy.student.sex</code>.
     */
    public void setSex(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.student.sex</code>.
     */
    @Size(max = 24)
    public String getSex() {
        return (String) get(3);
    }

    /**
     * Setter for <code>isy.student.family_residence</code>.
     */
    public void setFamilyResidence(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.student.family_residence</code>.
     */
    @Size(max = 192)
    public String getFamilyResidence() {
        return (String) get(4);
    }

    /**
     * Setter for <code>isy.student.political_landscape_id</code>.
     */
    public void setPoliticalLandscapeId(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.student.political_landscape_id</code>.
     */
    public Integer getPoliticalLandscapeId() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>isy.student.nation_id</code>.
     */
    public void setNationId(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>isy.student.nation_id</code>.
     */
    public Integer getNationId() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>isy.student.dormitory_number</code>.
     */
    public void setDormitoryNumber(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>isy.student.dormitory_number</code>.
     */
    @Size(max = 24)
    public String getDormitoryNumber() {
        return (String) get(7);
    }

    /**
     * Setter for <code>isy.student.parent_name</code>.
     */
    public void setParentName(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>isy.student.parent_name</code>.
     */
    @Size(max = 48)
    public String getParentName() {
        return (String) get(8);
    }

    /**
     * Setter for <code>isy.student.parent_contact_phone</code>.
     */
    public void setParentContactPhone(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>isy.student.parent_contact_phone</code>.
     */
    @Size(max = 48)
    public String getParentContactPhone() {
        return (String) get(9);
    }

    /**
     * Setter for <code>isy.student.place_origin</code>.
     */
    public void setPlaceOrigin(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>isy.student.place_origin</code>.
     */
    @Size(max = 112)
    public String getPlaceOrigin() {
        return (String) get(10);
    }

    /**
     * Setter for <code>isy.student.organize_id</code>.
     */
    public void setOrganizeId(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>isy.student.organize_id</code>.
     */
    @NotNull
    public Integer getOrganizeId() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>isy.student.username</code>.
     */
    public void setUsername(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>isy.student.username</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getUsername() {
        return (String) get(12);
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
    // Record13 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<Integer, String, String, String, String, Integer, Integer, String, String, String, String, Integer, String> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<Integer, String, String, String, String, Integer, Integer, String, String, String, String, Integer, String> valuesRow() {
        return (Row13) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Student.STUDENT.STUDENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Student.STUDENT.STUDENT_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Student.STUDENT.BIRTHDAY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Student.STUDENT.SEX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Student.STUDENT.FAMILY_RESIDENCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field6() {
        return Student.STUDENT.POLITICAL_LANDSCAPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field7() {
        return Student.STUDENT.NATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Student.STUDENT.DORMITORY_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Student.STUDENT.PARENT_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Student.STUDENT.PARENT_CONTACT_PHONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Student.STUDENT.PLACE_ORIGIN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field12() {
        return Student.STUDENT.ORGANIZE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return Student.STUDENT.USERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getStudentNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getBirthday();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getSex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getFamilyResidence();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component6() {
        return getPoliticalLandscapeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component7() {
        return getNationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getDormitoryNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getParentName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component10() {
        return getParentContactPhone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component11() {
        return getPlaceOrigin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component12() {
        return getOrganizeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component13() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getStudentId();
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
        return getBirthday();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getSex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getFamilyResidence();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value6() {
        return getPoliticalLandscapeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value7() {
        return getNationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getDormitoryNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getParentName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getParentContactPhone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getPlaceOrigin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value12() {
        return getOrganizeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value1(Integer value) {
        setStudentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value2(String value) {
        setStudentNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value3(String value) {
        setBirthday(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value4(String value) {
        setSex(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value5(String value) {
        setFamilyResidence(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value6(Integer value) {
        setPoliticalLandscapeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value7(Integer value) {
        setNationId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value8(String value) {
        setDormitoryNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value9(String value) {
        setParentName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value10(String value) {
        setParentContactPhone(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value11(String value) {
        setPlaceOrigin(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value12(Integer value) {
        setOrganizeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord value13(String value) {
        setUsername(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentRecord values(Integer value1, String value2, String value3, String value4, String value5, Integer value6, Integer value7, String value8, String value9, String value10, String value11, Integer value12, String value13) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached StudentRecord
     */
    public StudentRecord() {
        super(Student.STUDENT);
    }

    /**
     * Create a detached, initialised StudentRecord
     */
    public StudentRecord(Integer studentId, String studentNumber, String birthday, String sex, String familyResidence, Integer politicalLandscapeId, Integer nationId, String dormitoryNumber, String parentName, String parentContactPhone, String placeOrigin, Integer organizeId, String username) {
        super(Student.STUDENT);

        set(0, studentId);
        set(1, studentNumber);
        set(2, birthday);
        set(3, sex);
        set(4, familyResidence);
        set(5, politicalLandscapeId);
        set(6, nationId);
        set(7, dormitoryNumber);
        set(8, parentName);
        set(9, parentContactPhone);
        set(10, placeOrigin);
        set(11, organizeId);
        set(12, username);
    }
}
