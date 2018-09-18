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
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.GraduationDesignDatum;


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
public class GraduationDesignDatumRecord extends UpdatableRecordImpl<GraduationDesignDatumRecord> implements Record6<String, String, String, Integer, String, Timestamp> {

    private static final long serialVersionUID = 588235179;

    /**
     * Setter for <code>isy.graduation_design_datum.graduation_design_datum_id</code>.
     */
    public void setGraduationDesignDatumId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.graduation_design_datum.graduation_design_datum_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationDesignDatumId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.graduation_design_datum.version</code>.
     */
    public void setVersion(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.graduation_design_datum.version</code>.
     */
    @Size(max = 10)
    public String getVersion() {
        return (String) get(1);
    }

    /**
     * Setter for <code>isy.graduation_design_datum.file_id</code>.
     */
    public void setFileId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.graduation_design_datum.file_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getFileId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>isy.graduation_design_datum.graduation_design_datum_type_id</code>.
     */
    public void setGraduationDesignDatumTypeId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.graduation_design_datum.graduation_design_datum_type_id</code>.
     */
    @NotNull
    public Integer getGraduationDesignDatumTypeId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>isy.graduation_design_datum.graduation_design_tutor_id</code>.
     */
    public void setGraduationDesignTutorId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.graduation_design_datum.graduation_design_tutor_id</code>.
     */
    @NotNull
    @Size(max = 64)
    public String getGraduationDesignTutorId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>isy.graduation_design_datum.update_time</code>.
     */
    public void setUpdateTime(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>isy.graduation_design_datum.update_time</code>.
     */
    @NotNull
    public Timestamp getUpdateTime() {
        return (Timestamp) get(5);
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
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<String, String, String, Integer, String, Timestamp> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<String, String, String, Integer, String, Timestamp> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return GraduationDesignDatum.GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return GraduationDesignDatum.GRADUATION_DESIGN_DATUM.VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return GraduationDesignDatum.GRADUATION_DESIGN_DATUM.FILE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return GraduationDesignDatum.GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return GraduationDesignDatum.GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return GraduationDesignDatum.GRADUATION_DESIGN_DATUM.UPDATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getGraduationDesignDatumId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getFileId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getGraduationDesignDatumTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getGraduationDesignTutorId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component6() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getGraduationDesignDatumId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getFileId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getGraduationDesignDatumTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getGraduationDesignTutorId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value6() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumRecord value1(String value) {
        setGraduationDesignDatumId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumRecord value2(String value) {
        setVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumRecord value3(String value) {
        setFileId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumRecord value4(Integer value) {
        setGraduationDesignDatumTypeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumRecord value5(String value) {
        setGraduationDesignTutorId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumRecord value6(Timestamp value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignDatumRecord values(String value1, String value2, String value3, Integer value4, String value5, Timestamp value6) {
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
     * Create a detached GraduationDesignDatumRecord
     */
    public GraduationDesignDatumRecord() {
        super(GraduationDesignDatum.GRADUATION_DESIGN_DATUM);
    }

    /**
     * Create a detached, initialised GraduationDesignDatumRecord
     */
    public GraduationDesignDatumRecord(String graduationDesignDatumId, String version, String fileId, Integer graduationDesignDatumTypeId, String graduationDesignTutorId, Timestamp updateTime) {
        super(GraduationDesignDatum.GRADUATION_DESIGN_DATUM);

        set(0, graduationDesignDatumId);
        set(1, version);
        set(2, fileId);
        set(3, graduationDesignDatumTypeId);
        set(4, graduationDesignTutorId);
        set(5, updateTime);
    }
}
