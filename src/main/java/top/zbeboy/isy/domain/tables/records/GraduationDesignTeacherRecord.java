/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.GraduationDesignTeacher;


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
public class GraduationDesignTeacherRecord extends UpdatableRecordImpl<GraduationDesignTeacherRecord> implements Record5<String, String, Integer, Integer, String> {

    private static final long serialVersionUID = -1899147636;

    /**
     * Setter for <code>isy.graduation_design_teacher.graduation_design_teacher_id</code>.
     */
    public void setGraduationDesignTeacherId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.graduation_design_teacher.graduation_design_teacher_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationDesignTeacherId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.graduation_design_teacher.graduation_design_release_id</code>.
     */
    public void setGraduationDesignReleaseId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.graduation_design_teacher.graduation_design_release_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationDesignReleaseId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.graduation_design_teacher.staff_id</code>.
     */
    public void setStaffId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.graduation_design_teacher.staff_id</code>.
     */
    @NotNull
    public Integer getStaffId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>isy.graduation_design_teacher.student_count</code>.
     */
    public void setStudentCount(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.graduation_design_teacher.student_count</code>.
     */
    @NotNull
    public Integer getStudentCount() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>isy.graduation_design_teacher.username</code>.
     */
    public void setUsername(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.graduation_design_teacher.username</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getUsername() {
        return (String) get(4);
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
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, String, Integer, Integer, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, String, Integer, Integer, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.STAFF_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.STUDENT_COUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.USERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getGraduationDesignTeacherId();
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
    public Integer value3() {
        return getStaffId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getStudentCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignTeacherRecord value1(String value) {
        setGraduationDesignTeacherId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignTeacherRecord value2(String value) {
        setGraduationDesignReleaseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignTeacherRecord value3(Integer value) {
        setStaffId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignTeacherRecord value4(Integer value) {
        setStudentCount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignTeacherRecord value5(String value) {
        setUsername(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignTeacherRecord values(String value1, String value2, Integer value3, Integer value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GraduationDesignTeacherRecord
     */
    public GraduationDesignTeacherRecord() {
        super(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER);
    }

    /**
     * Create a detached, initialised GraduationDesignTeacherRecord
     */
    public GraduationDesignTeacherRecord(String graduationDesignTeacherId, String graduationDesignReleaseId, Integer staffId, Integer studentCount, String username) {
        super(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER);

        set(0, graduationDesignTeacherId);
        set(1, graduationDesignReleaseId);
        set(2, staffId);
        set(3, studentCount);
        set(4, username);
    }
}
