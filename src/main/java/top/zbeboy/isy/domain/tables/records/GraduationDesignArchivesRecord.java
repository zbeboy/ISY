/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.TableRecordImpl;

import top.zbeboy.isy.domain.tables.GraduationDesignArchives;


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
public class GraduationDesignArchivesRecord extends TableRecordImpl<GraduationDesignArchivesRecord> implements Record4<String, Byte, String, String> {

    private static final long serialVersionUID = -166322749;

    /**
     * Setter for <code>isy.graduation_design_archives.graduation_design_presubject_id</code>.
     */
    public void setGraduationDesignPresubjectId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.graduation_design_archives.graduation_design_presubject_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationDesignPresubjectId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.graduation_design_archives.is_excellent</code>.
     */
    public void setIsExcellent(Byte value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.graduation_design_archives.is_excellent</code>.
     */
    public Byte getIsExcellent() {
        return (Byte) get(1);
    }

    /**
     * Setter for <code>isy.graduation_design_archives.archive_number</code>.
     */
    public void setArchiveNumber(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.graduation_design_archives.archive_number</code>.
     */
    @NotNull
    @Size(max = 100)
    public String getArchiveNumber() {
        return (String) get(2);
    }

    /**
     * Setter for <code>isy.graduation_design_archives.note</code>.
     */
    public void setNote(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.graduation_design_archives.note</code>.
     */
    @Size(max = 100)
    public String getNote() {
        return (String) get(3);
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<String, Byte, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<String, Byte, String, String> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return GraduationDesignArchives.GRADUATION_DESIGN_ARCHIVES.GRADUATION_DESIGN_PRESUBJECT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field2() {
        return GraduationDesignArchives.GRADUATION_DESIGN_ARCHIVES.IS_EXCELLENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return GraduationDesignArchives.GRADUATION_DESIGN_ARCHIVES.ARCHIVE_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return GraduationDesignArchives.GRADUATION_DESIGN_ARCHIVES.NOTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getGraduationDesignPresubjectId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value2() {
        return getIsExcellent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getArchiveNumber();
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
    public GraduationDesignArchivesRecord value1(String value) {
        setGraduationDesignPresubjectId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignArchivesRecord value2(Byte value) {
        setIsExcellent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignArchivesRecord value3(String value) {
        setArchiveNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignArchivesRecord value4(String value) {
        setNote(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignArchivesRecord values(String value1, Byte value2, String value3, String value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GraduationDesignArchivesRecord
     */
    public GraduationDesignArchivesRecord() {
        super(GraduationDesignArchives.GRADUATION_DESIGN_ARCHIVES);
    }

    /**
     * Create a detached, initialised GraduationDesignArchivesRecord
     */
    public GraduationDesignArchivesRecord(String graduationDesignPresubjectId, Byte isExcellent, String archiveNumber, String note) {
        super(GraduationDesignArchives.GRADUATION_DESIGN_ARCHIVES);

        set(0, graduationDesignPresubjectId);
        set(1, isExcellent);
        set(2, archiveNumber);
        set(3, note);
    }
}