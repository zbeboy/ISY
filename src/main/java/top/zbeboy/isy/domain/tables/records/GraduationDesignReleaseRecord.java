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
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.GraduationDesignRelease;


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
public class GraduationDesignReleaseRecord extends UpdatableRecordImpl<GraduationDesignReleaseRecord> implements Record15<String, String, Timestamp, String, Timestamp, Timestamp, Timestamp, Timestamp, Byte, String, Byte, Byte, Integer, Integer, String> {

    private static final long serialVersionUID = 2117513957;

    /**
     * Setter for <code>isy.graduation_design_release.graduation_design_release_id</code>.
     */
    public void setGraduationDesignReleaseId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.graduation_design_release_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationDesignReleaseId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.graduation_design_release.graduation_design_title</code>.
     */
    public void setGraduationDesignTitle(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.graduation_design_title</code>.
     */
    @NotNull
    @Size(max = 100)
    public String getGraduationDesignTitle() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.graduation_design_release.release_time</code>.
     */
    public void setReleaseTime(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.release_time</code>.
     */
    public Timestamp getReleaseTime() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>isy.graduation_design_release.username</code>.
     */
    public void setUsername(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.username</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getUsername() {
        return (String) get(3);
    }

    /**
     * Setter for <code>isy.graduation_design_release.start_time</code>.
     */
    public void setStartTime(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.start_time</code>.
     */
    @NotNull
    public Timestamp getStartTime() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>isy.graduation_design_release.end_time</code>.
     */
    public void setEndTime(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.end_time</code>.
     */
    @NotNull
    public Timestamp getEndTime() {
        return (Timestamp) get(5);
    }

    /**
     * Setter for <code>isy.graduation_design_release.fill_teacher_start_time</code>.
     */
    public void setFillTeacherStartTime(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.fill_teacher_start_time</code>.
     */
    @NotNull
    public Timestamp getFillTeacherStartTime() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>isy.graduation_design_release.fill_teacher_end_time</code>.
     */
    public void setFillTeacherEndTime(Timestamp value) {
        set(7, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.fill_teacher_end_time</code>.
     */
    @NotNull
    public Timestamp getFillTeacherEndTime() {
        return (Timestamp) get(7);
    }

    /**
     * Setter for <code>isy.graduation_design_release.graduation_design_is_del</code>.
     */
    public void setGraduationDesignIsDel(Byte value) {
        set(8, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.graduation_design_is_del</code>.
     */
    @NotNull
    public Byte getGraduationDesignIsDel() {
        return (Byte) get(8);
    }

    /**
     * Setter for <code>isy.graduation_design_release.allow_grade</code>.
     */
    public void setAllowGrade(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.allow_grade</code>.
     */
    @NotNull
    @Size(max = 5)
    public String getAllowGrade() {
        return (String) get(9);
    }

    /**
     * Setter for <code>isy.graduation_design_release.is_ok_teacher</code>.
     */
    public void setIsOkTeacher(Byte value) {
        set(10, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.is_ok_teacher</code>.
     */
    public Byte getIsOkTeacher() {
        return (Byte) get(10);
    }

    /**
     * Setter for <code>isy.graduation_design_release.is_ok_teacher_adjust</code>.
     */
    public void setIsOkTeacherAdjust(Byte value) {
        set(11, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.is_ok_teacher_adjust</code>.
     */
    public Byte getIsOkTeacherAdjust() {
        return (Byte) get(11);
    }

    /**
     * Setter for <code>isy.graduation_design_release.department_id</code>.
     */
    public void setDepartmentId(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.department_id</code>.
     */
    @NotNull
    public Integer getDepartmentId() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>isy.graduation_design_release.science_id</code>.
     */
    public void setScienceId(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.science_id</code>.
     */
    @NotNull
    public Integer getScienceId() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>isy.graduation_design_release.publisher</code>.
     */
    public void setPublisher(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>isy.graduation_design_release.publisher</code>.
     */
    @NotNull
    @Size(max = 30)
    public String getPublisher() {
        return (String) get(14);
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
    // Record15 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row15<String, String, Timestamp, String, Timestamp, Timestamp, Timestamp, Timestamp, Byte, String, Byte, Byte, Integer, Integer, String> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row15<String, String, Timestamp, String, Timestamp, Timestamp, Timestamp, Timestamp, Byte, String, Byte, Byte, Integer, Integer, String> valuesRow() {
        return (Row15) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_RELEASE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_TITLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field3() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.RELEASE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.USERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.START_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.END_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.FILL_TEACHER_START_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field8() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.FILL_TEACHER_END_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field9() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.GRADUATION_DESIGN_IS_DEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.ALLOW_GRADE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field11() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.IS_OK_TEACHER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field12() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.IS_OK_TEACHER_ADJUST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field13() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.DEPARTMENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field14() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.SCIENCE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return GraduationDesignRelease.GRADUATION_DESIGN_RELEASE.PUBLISHER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getGraduationDesignReleaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getGraduationDesignTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component3() {
        return getReleaseTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component5() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component6() {
        return getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
        return getFillTeacherStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component8() {
        return getFillTeacherEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component9() {
        return getGraduationDesignIsDel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component10() {
        return getAllowGrade();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component11() {
        return getIsOkTeacher();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component12() {
        return getIsOkTeacherAdjust();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component13() {
        return getDepartmentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component14() {
        return getScienceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component15() {
        return getPublisher();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getGraduationDesignReleaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getGraduationDesignTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value3() {
        return getReleaseTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value5() {
        return getStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value6() {
        return getEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getFillTeacherStartTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value8() {
        return getFillTeacherEndTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value9() {
        return getGraduationDesignIsDel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getAllowGrade();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value11() {
        return getIsOkTeacher();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value12() {
        return getIsOkTeacherAdjust();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value13() {
        return getDepartmentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value14() {
        return getScienceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getPublisher();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value1(String value) {
        setGraduationDesignReleaseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value2(String value) {
        setGraduationDesignTitle(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value3(Timestamp value) {
        setReleaseTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value4(String value) {
        setUsername(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value5(Timestamp value) {
        setStartTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value6(Timestamp value) {
        setEndTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value7(Timestamp value) {
        setFillTeacherStartTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value8(Timestamp value) {
        setFillTeacherEndTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value9(Byte value) {
        setGraduationDesignIsDel(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value10(String value) {
        setAllowGrade(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value11(Byte value) {
        setIsOkTeacher(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value12(Byte value) {
        setIsOkTeacherAdjust(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value13(Integer value) {
        setDepartmentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value14(Integer value) {
        setScienceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord value15(String value) {
        setPublisher(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignReleaseRecord values(String value1, String value2, Timestamp value3, String value4, Timestamp value5, Timestamp value6, Timestamp value7, Timestamp value8, Byte value9, String value10, Byte value11, Byte value12, Integer value13, Integer value14, String value15) {
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
        value14(value14);
        value15(value15);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GraduationDesignReleaseRecord
     */
    public GraduationDesignReleaseRecord() {
        super(GraduationDesignRelease.GRADUATION_DESIGN_RELEASE);
    }

    /**
     * Create a detached, initialised GraduationDesignReleaseRecord
     */
    public GraduationDesignReleaseRecord(String graduationDesignReleaseId, String graduationDesignTitle, Timestamp releaseTime, String username, Timestamp startTime, Timestamp endTime, Timestamp fillTeacherStartTime, Timestamp fillTeacherEndTime, Byte graduationDesignIsDel, String allowGrade, Byte isOkTeacher, Byte isOkTeacherAdjust, Integer departmentId, Integer scienceId, String publisher) {
        super(GraduationDesignRelease.GRADUATION_DESIGN_RELEASE);

        set(0, graduationDesignReleaseId);
        set(1, graduationDesignTitle);
        set(2, releaseTime);
        set(3, username);
        set(4, startTime);
        set(5, endTime);
        set(6, fillTeacherStartTime);
        set(7, fillTeacherEndTime);
        set(8, graduationDesignIsDel);
        set(9, allowGrade);
        set(10, isOkTeacher);
        set(11, isOkTeacherAdjust);
        set(12, departmentId);
        set(13, scienceId);
        set(14, publisher);
    }
}
