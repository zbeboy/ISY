/**
 * This class is generated by jOOQ
 */
package top.zbeboy.isy.domain.tables.records;


import java.sql.Date;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.InternshipJournal;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class InternshipJournalRecord extends UpdatableRecordImpl<InternshipJournalRecord> implements Record13<String, String, String, String, String, String, String, String, Date, Timestamp, Integer, String, String> {

    private static final long serialVersionUID = -1534533974;

    /**
     * Setter for <code>isy.internship_journal.internship_journal_id</code>.
     */
    public void setInternshipJournalId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.internship_journal.internship_journal_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getInternshipJournalId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.internship_journal.student_name</code>.
     */
    public void setStudentName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.internship_journal.student_name</code>.
     */
    @NotNull
    @Size(max = 10)
    public String getStudentName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.internship_journal.student_number</code>.
     */
    public void setStudentNumber(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.internship_journal.student_number</code>.
     */
    @NotNull
    @Size(max = 20)
    public String getStudentNumber() {
        return (String) get(2);
    }

    /**
     * Setter for <code>isy.internship_journal.organize</code>.
     */
    public void setOrganize(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.internship_journal.organize</code>.
     */
    @NotNull
    @Size(max = 10)
    public String getOrganize() {
        return (String) get(3);
    }

    /**
     * Setter for <code>isy.internship_journal.school_guidance_teacher</code>.
     */
    public void setSchoolGuidanceTeacher(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.internship_journal.school_guidance_teacher</code>.
     */
    @NotNull
    @Size(max = 10)
    public String getSchoolGuidanceTeacher() {
        return (String) get(4);
    }

    /**
     * Setter for <code>isy.internship_journal.graduation_practice_company_name</code>.
     */
    public void setGraduationPracticeCompanyName(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.internship_journal.graduation_practice_company_name</code>.
     */
    @NotNull
    @Size(max = 200)
    public String getGraduationPracticeCompanyName() {
        return (String) get(5);
    }

    /**
     * Setter for <code>isy.internship_journal.internship_journal_content</code>.
     */
    public void setInternshipJournalContent(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>isy.internship_journal.internship_journal_content</code>.
     */
    @NotNull
    @Size(max = 65535)
    public String getInternshipJournalContent() {
        return (String) get(6);
    }

    /**
     * Setter for <code>isy.internship_journal.internship_journal_html</code>.
     */
    public void setInternshipJournalHtml(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>isy.internship_journal.internship_journal_html</code>.
     */
    @NotNull
    @Size(max = 65535)
    public String getInternshipJournalHtml() {
        return (String) get(7);
    }

    /**
     * Setter for <code>isy.internship_journal.internship_journal_date</code>.
     */
    public void setInternshipJournalDate(Date value) {
        set(8, value);
    }

    /**
     * Getter for <code>isy.internship_journal.internship_journal_date</code>.
     */
    @NotNull
    public Date getInternshipJournalDate() {
        return (Date) get(8);
    }

    /**
     * Setter for <code>isy.internship_journal.create_date</code>.
     */
    public void setCreateDate(Timestamp value) {
        set(9, value);
    }

    /**
     * Getter for <code>isy.internship_journal.create_date</code>.
     */
    @NotNull
    public Timestamp getCreateDate() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>isy.internship_journal.student_id</code>.
     */
    public void setStudentId(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>isy.internship_journal.student_id</code>.
     */
    @NotNull
    public Integer getStudentId() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>isy.internship_journal.internship_release_id</code>.
     */
    public void setInternshipReleaseId(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>isy.internship_journal.internship_release_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getInternshipReleaseId() {
        return (String) get(11);
    }

    /**
     * Setter for <code>isy.internship_journal.internship_journal_word</code>.
     */
    public void setInternshipJournalWord(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>isy.internship_journal.internship_journal_word</code>.
     */
    @NotNull
    @Size(max = 500)
    public String getInternshipJournalWord() {
        return (String) get(12);
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
    // Record13 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<String, String, String, String, String, String, String, String, Date, Timestamp, Integer, String, String> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<String, String, String, String, String, String, String, String, Date, Timestamp, Integer, String, String> valuesRow() {
        return (Row13) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return InternshipJournal.INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return InternshipJournal.INTERNSHIP_JOURNAL.STUDENT_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return InternshipJournal.INTERNSHIP_JOURNAL.STUDENT_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return InternshipJournal.INTERNSHIP_JOURNAL.ORGANIZE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return InternshipJournal.INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return InternshipJournal.INTERNSHIP_JOURNAL.GRADUATION_PRACTICE_COMPANY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return InternshipJournal.INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_CONTENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return InternshipJournal.INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_HTML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field9() {
        return InternshipJournal.INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field10() {
        return InternshipJournal.INTERNSHIP_JOURNAL.CREATE_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field11() {
        return InternshipJournal.INTERNSHIP_JOURNAL.STUDENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return InternshipJournal.INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return InternshipJournal.INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_WORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getInternshipJournalId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getStudentName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getStudentNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getOrganize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getSchoolGuidanceTeacher();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getGraduationPracticeCompanyName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getInternshipJournalContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getInternshipJournalHtml();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value9() {
        return getInternshipJournalDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value10() {
        return getCreateDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value11() {
        return getStudentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getInternshipReleaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getInternshipJournalWord();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value1(String value) {
        setInternshipJournalId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value2(String value) {
        setStudentName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value3(String value) {
        setStudentNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value4(String value) {
        setOrganize(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value5(String value) {
        setSchoolGuidanceTeacher(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value6(String value) {
        setGraduationPracticeCompanyName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value7(String value) {
        setInternshipJournalContent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value8(String value) {
        setInternshipJournalHtml(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value9(Date value) {
        setInternshipJournalDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value10(Timestamp value) {
        setCreateDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value11(Integer value) {
        setStudentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value12(String value) {
        setInternshipReleaseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord value13(String value) {
        setInternshipJournalWord(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournalRecord values(String value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, Date value9, Timestamp value10, Integer value11, String value12, String value13) {
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
     * Create a detached InternshipJournalRecord
     */
    public InternshipJournalRecord() {
        super(InternshipJournal.INTERNSHIP_JOURNAL);
    }

    /**
     * Create a detached, initialised InternshipJournalRecord
     */
    public InternshipJournalRecord(String internshipJournalId, String studentName, String studentNumber, String organize, String schoolGuidanceTeacher, String graduationPracticeCompanyName, String internshipJournalContent, String internshipJournalHtml, Date internshipJournalDate, Timestamp createDate, Integer studentId, String internshipReleaseId, String internshipJournalWord) {
        super(InternshipJournal.INTERNSHIP_JOURNAL);

        set(0, internshipJournalId);
        set(1, studentName);
        set(2, studentNumber);
        set(3, organize);
        set(4, schoolGuidanceTeacher);
        set(5, graduationPracticeCompanyName);
        set(6, internshipJournalContent);
        set(7, internshipJournalHtml);
        set(8, internshipJournalDate);
        set(9, createDate);
        set(10, studentId);
        set(11, internshipReleaseId);
        set(12, internshipJournalWord);
    }
}
