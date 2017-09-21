/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.GraduationPracticeCompanyRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GraduationPracticeCompany extends TableImpl<GraduationPracticeCompanyRecord> {

    private static final long serialVersionUID = 1877028918;

    /**
     * The reference instance of <code>isy.graduation_practice_company</code>
     */
    public static final GraduationPracticeCompany GRADUATION_PRACTICE_COMPANY = new GraduationPracticeCompany();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GraduationPracticeCompanyRecord> getRecordType() {
        return GraduationPracticeCompanyRecord.class;
    }

    /**
     * The column <code>isy.graduation_practice_company.graduation_practice_company_id</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> GRADUATION_PRACTICE_COMPANY_ID = createField("graduation_practice_company_id", org.jooq.impl.SQLDataType.VARCHAR.length(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.student_name</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> STUDENT_NAME = createField("student_name", org.jooq.impl.SQLDataType.VARCHAR.length(15).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.college_class</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> COLLEGE_CLASS = createField("college_class", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.student_sex</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> STUDENT_SEX = createField("student_sex", org.jooq.impl.SQLDataType.VARCHAR.length(2).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.student_number</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> STUDENT_NUMBER = createField("student_number", org.jooq.impl.SQLDataType.VARCHAR.length(20).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.phone_number</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> PHONE_NUMBER = createField("phone_number", org.jooq.impl.SQLDataType.VARCHAR.length(15).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.qq_mailbox</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> QQ_MAILBOX = createField("qq_mailbox", org.jooq.impl.SQLDataType.VARCHAR.length(100).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.parental_contact</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> PARENTAL_CONTACT = createField("parental_contact", org.jooq.impl.SQLDataType.VARCHAR.length(20).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.headmaster</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> HEADMASTER = createField("headmaster", org.jooq.impl.SQLDataType.VARCHAR.length(10).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.headmaster_contact</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> HEADMASTER_CONTACT = createField("headmaster_contact", org.jooq.impl.SQLDataType.VARCHAR.length(20).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.graduation_practice_company_name</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> GRADUATION_PRACTICE_COMPANY_NAME = createField("graduation_practice_company_name", org.jooq.impl.SQLDataType.VARCHAR.length(200).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.graduation_practice_company_address</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> GRADUATION_PRACTICE_COMPANY_ADDRESS = createField("graduation_practice_company_address", org.jooq.impl.SQLDataType.VARCHAR.length(500).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.graduation_practice_company_contacts</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> GRADUATION_PRACTICE_COMPANY_CONTACTS = createField("graduation_practice_company_contacts", org.jooq.impl.SQLDataType.VARCHAR.length(10).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.graduation_practice_company_tel</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> GRADUATION_PRACTICE_COMPANY_TEL = createField("graduation_practice_company_tel", org.jooq.impl.SQLDataType.VARCHAR.length(20).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.school_guidance_teacher</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> SCHOOL_GUIDANCE_TEACHER = createField("school_guidance_teacher", org.jooq.impl.SQLDataType.VARCHAR.length(10).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.school_guidance_teacher_tel</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> SCHOOL_GUIDANCE_TEACHER_TEL = createField("school_guidance_teacher_tel", org.jooq.impl.SQLDataType.VARCHAR.length(20).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.start_time</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, Date> START_TIME = createField("start_time", org.jooq.impl.SQLDataType.DATE.nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.end_time</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, Date> END_TIME = createField("end_time", org.jooq.impl.SQLDataType.DATE.nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.commitment_book</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, Byte> COMMITMENT_BOOK = createField("commitment_book", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>isy.graduation_practice_company.safety_responsibility_book</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, Byte> SAFETY_RESPONSIBILITY_BOOK = createField("safety_responsibility_book", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>isy.graduation_practice_company.practice_agreement</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, Byte> PRACTICE_AGREEMENT = createField("practice_agreement", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>isy.graduation_practice_company.internship_application</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, Byte> INTERNSHIP_APPLICATION = createField("internship_application", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>isy.graduation_practice_company.practice_receiving</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, Byte> PRACTICE_RECEIVING = createField("practice_receiving", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>isy.graduation_practice_company.security_education_agreement</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, Byte> SECURITY_EDUCATION_AGREEMENT = createField("security_education_agreement", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>isy.graduation_practice_company.parental_consent</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, Byte> PARENTAL_CONSENT = createField("parental_consent", org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * The column <code>isy.graduation_practice_company.student_id</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, Integer> STUDENT_ID = createField("student_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.graduation_practice_company.internship_release_id</code>.
     */
    public final TableField<GraduationPracticeCompanyRecord, String> INTERNSHIP_RELEASE_ID = createField("internship_release_id", org.jooq.impl.SQLDataType.VARCHAR.length(64).nullable(false), this, "");

    /**
     * Create a <code>isy.graduation_practice_company</code> table reference
     */
    public GraduationPracticeCompany() {
        this("graduation_practice_company", null);
    }

    /**
     * Create an aliased <code>isy.graduation_practice_company</code> table reference
     */
    public GraduationPracticeCompany(String alias) {
        this(alias, GRADUATION_PRACTICE_COMPANY);
    }

    private GraduationPracticeCompany(String alias, Table<GraduationPracticeCompanyRecord> aliased) {
        this(alias, aliased, null);
    }

    private GraduationPracticeCompany(String alias, Table<GraduationPracticeCompanyRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Isy.ISY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<GraduationPracticeCompanyRecord> getPrimaryKey() {
        return Keys.KEY_GRADUATION_PRACTICE_COMPANY_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GraduationPracticeCompanyRecord>> getKeys() {
        return Arrays.<UniqueKey<GraduationPracticeCompanyRecord>>asList(Keys.KEY_GRADUATION_PRACTICE_COMPANY_PRIMARY, Keys.KEY_GRADUATION_PRACTICE_COMPANY_STUDENT_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<GraduationPracticeCompanyRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GraduationPracticeCompanyRecord, ?>>asList(Keys.GRADUATION_PRACTICE_COMPANY_IBFK_1, Keys.GRADUATION_PRACTICE_COMPANY_IBFK_2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationPracticeCompany as(String alias) {
        return new GraduationPracticeCompany(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationPracticeCompany rename(String name) {
        return new GraduationPracticeCompany(name, null);
    }
}
