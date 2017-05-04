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

import top.zbeboy.isy.domain.tables.GraduateArchives;


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
public class GraduateArchivesRecord extends UpdatableRecordImpl<GraduateArchivesRecord> implements Record5<String, String, Byte, String, String> {

    private static final long serialVersionUID = -924990662;

    /**
     * Setter for <code>isy.graduate_archives.graduate_archives_id</code>.
     */
    public void setGraduateArchivesId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.graduate_archives.graduate_archives_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduateArchivesId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.graduate_archives.graduate_bill_id</code>.
     */
    public void setGraduateBillId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.graduate_archives.graduate_bill_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduateBillId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.graduate_archives.is_excellent</code>.
     */
    public void setIsExcellent(Byte value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.graduate_archives.is_excellent</code>.
     */
    public Byte getIsExcellent() {
        return (Byte) get(2);
    }

    /**
     * Setter for <code>isy.graduate_archives.archive_number</code>.
     */
    public void setArchiveNumber(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.graduate_archives.archive_number</code>.
     */
    @NotNull
    @Size(max = 100)
    public String getArchiveNumber() {
        return (String) get(3);
    }

    /**
     * Setter for <code>isy.graduate_archives.note</code>.
     */
    public void setNote(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.graduate_archives.note</code>.
     */
    @Size(max = 100)
    public String getNote() {
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
    public Row5<String, String, Byte, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, String, Byte, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return GraduateArchives.GRADUATE_ARCHIVES.GRADUATE_ARCHIVES_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return GraduateArchives.GRADUATE_ARCHIVES.GRADUATE_BILL_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field3() {
        return GraduateArchives.GRADUATE_ARCHIVES.IS_EXCELLENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return GraduateArchives.GRADUATE_ARCHIVES.ARCHIVE_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return GraduateArchives.GRADUATE_ARCHIVES.NOTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getGraduateArchivesId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getGraduateBillId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value3() {
        return getIsExcellent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getArchiveNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getNote();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateArchivesRecord value1(String value) {
        setGraduateArchivesId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateArchivesRecord value2(String value) {
        setGraduateBillId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateArchivesRecord value3(Byte value) {
        setIsExcellent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateArchivesRecord value4(String value) {
        setArchiveNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateArchivesRecord value5(String value) {
        setNote(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduateArchivesRecord values(String value1, String value2, Byte value3, String value4, String value5) {
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
     * Create a detached GraduateArchivesRecord
     */
    public GraduateArchivesRecord() {
        super(GraduateArchives.GRADUATE_ARCHIVES);
    }

    /**
     * Create a detached, initialised GraduateArchivesRecord
     */
    public GraduateArchivesRecord(String graduateArchivesId, String graduateBillId, Byte isExcellent, String archiveNumber, String note) {
        super(GraduateArchives.GRADUATE_ARCHIVES);

        set(0, graduateArchivesId);
        set(1, graduateBillId);
        set(2, isExcellent);
        set(3, archiveNumber);
        set(4, note);
    }
}
