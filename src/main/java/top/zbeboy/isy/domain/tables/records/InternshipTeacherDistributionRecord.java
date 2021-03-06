/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.InternshipTeacherDistribution;


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
public class InternshipTeacherDistributionRecord extends UpdatableRecordImpl<InternshipTeacherDistributionRecord> implements Record6<Integer, Integer, String, String, String, String> {

    private static final long serialVersionUID = -110548396;

    /**
     * Setter for <code>isy.internship_teacher_distribution.staff_id</code>.
     */
    public void setStaffId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.internship_teacher_distribution.staff_id</code>.
     */
    @NotNull
    public Integer getStaffId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>isy.internship_teacher_distribution.student_id</code>.
     */
    public void setStudentId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.internship_teacher_distribution.student_id</code>.
     */
    @NotNull
    public Integer getStudentId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>isy.internship_teacher_distribution.internship_release_id</code>.
     */
    public void setInternshipReleaseId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.internship_teacher_distribution.internship_release_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getInternshipReleaseId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>isy.internship_teacher_distribution.username</code>.
     */
    public void setUsername(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.internship_teacher_distribution.username</code>.
     */
    @NotNull
    @Size(max = 200)
    public String getUsername() {
        return (String) get(3);
    }

    /**
     * Setter for <code>isy.internship_teacher_distribution.student_real_name</code>.
     */
    public void setStudentRealName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.internship_teacher_distribution.student_real_name</code>.
     */
    @NotNull
    @Size(max = 30)
    public String getStudentRealName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>isy.internship_teacher_distribution.assigner</code>.
     */
    public void setAssigner(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.internship_teacher_distribution.assigner</code>.
     */
    @NotNull
    @Size(max = 30)
    public String getAssigner() {
        return (String) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record3<Integer, Integer, String> key() {
        return (Record3) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Integer, String, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Integer, String, String, String, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.USERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_REAL_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.ASSIGNER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getStaffId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getInternshipReleaseId();
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
    public String component5() {
        return getStudentRealName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getAssigner();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getStaffId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getInternshipReleaseId();
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
    public String value5() {
        return getStudentRealName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getAssigner();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipTeacherDistributionRecord value1(Integer value) {
        setStaffId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipTeacherDistributionRecord value2(Integer value) {
        setStudentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipTeacherDistributionRecord value3(String value) {
        setInternshipReleaseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipTeacherDistributionRecord value4(String value) {
        setUsername(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipTeacherDistributionRecord value5(String value) {
        setStudentRealName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipTeacherDistributionRecord value6(String value) {
        setAssigner(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipTeacherDistributionRecord values(Integer value1, Integer value2, String value3, String value4, String value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached InternshipTeacherDistributionRecord
     */
    public InternshipTeacherDistributionRecord() {
        super(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION);
    }

    /**
     * Create a detached, initialised InternshipTeacherDistributionRecord
     */
    public InternshipTeacherDistributionRecord(Integer staffId, Integer studentId, String internshipReleaseId, String username, String studentRealName, String assigner) {
        super(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION);

        set(0, staffId);
        set(1, studentId);
        set(2, internshipReleaseId);
        set(3, username);
        set(4, studentRealName);
        set(5, assigner);
    }
}
