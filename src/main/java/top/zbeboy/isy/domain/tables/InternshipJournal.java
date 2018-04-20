/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Indexes;
import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.InternshipJournalRecord;


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
public class InternshipJournal extends TableImpl<InternshipJournalRecord> {

    private static final long serialVersionUID = -1028692089;

    /**
     * The reference instance of <code>isy.internship_journal</code>
     */
    public static final InternshipJournal INTERNSHIP_JOURNAL = new InternshipJournal();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<InternshipJournalRecord> getRecordType() {
        return InternshipJournalRecord.class;
    }

    /**
     * The column <code>isy.internship_journal.internship_journal_id</code>.
     */
    public final TableField<InternshipJournalRecord, String> INTERNSHIP_JOURNAL_ID = createField("internship_journal_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.student_name</code>.
     */
    public final TableField<InternshipJournalRecord, String> STUDENT_NAME = createField("student_name", org.jooq.impl.SQLDataType.VARCHAR(30).nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.student_number</code>.
     */
    public final TableField<InternshipJournalRecord, String> STUDENT_NUMBER = createField("student_number", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.organize</code>.
     */
    public final TableField<InternshipJournalRecord, String> ORGANIZE = createField("organize", org.jooq.impl.SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.school_guidance_teacher</code>.
     */
    public final TableField<InternshipJournalRecord, String> SCHOOL_GUIDANCE_TEACHER = createField("school_guidance_teacher", org.jooq.impl.SQLDataType.VARCHAR(30).nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.graduation_practice_company_name</code>.
     */
    public final TableField<InternshipJournalRecord, String> GRADUATION_PRACTICE_COMPANY_NAME = createField("graduation_practice_company_name", org.jooq.impl.SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.internship_journal_content</code>.
     */
    public final TableField<InternshipJournalRecord, String> INTERNSHIP_JOURNAL_CONTENT = createField("internship_journal_content", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.internship_journal_html</code>.
     */
    public final TableField<InternshipJournalRecord, String> INTERNSHIP_JOURNAL_HTML = createField("internship_journal_html", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.internship_journal_date</code>.
     */
    public final TableField<InternshipJournalRecord, Date> INTERNSHIP_JOURNAL_DATE = createField("internship_journal_date", org.jooq.impl.SQLDataType.DATE.nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.create_date</code>.
     */
    public final TableField<InternshipJournalRecord, Timestamp> CREATE_DATE = createField("create_date", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.student_id</code>.
     */
    public final TableField<InternshipJournalRecord, Integer> STUDENT_ID = createField("student_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.internship_release_id</code>.
     */
    public final TableField<InternshipJournalRecord, String> INTERNSHIP_RELEASE_ID = createField("internship_release_id", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.staff_id</code>.
     */
    public final TableField<InternshipJournalRecord, Integer> STAFF_ID = createField("staff_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.internship_journal_word</code>.
     */
    public final TableField<InternshipJournalRecord, String> INTERNSHIP_JOURNAL_WORD = createField("internship_journal_word", org.jooq.impl.SQLDataType.VARCHAR(500).nullable(false), this, "");

    /**
     * The column <code>isy.internship_journal.is_see_staff</code>.
     */
    public final TableField<InternshipJournalRecord, Byte> IS_SEE_STAFF = createField("is_see_staff", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * Create a <code>isy.internship_journal</code> table reference
     */
    public InternshipJournal() {
        this(DSL.name("internship_journal"), null);
    }

    /**
     * Create an aliased <code>isy.internship_journal</code> table reference
     */
    public InternshipJournal(String alias) {
        this(DSL.name(alias), INTERNSHIP_JOURNAL);
    }

    /**
     * Create an aliased <code>isy.internship_journal</code> table reference
     */
    public InternshipJournal(Name alias) {
        this(alias, INTERNSHIP_JOURNAL);
    }

    private InternshipJournal(Name alias, Table<InternshipJournalRecord> aliased) {
        this(alias, aliased, null);
    }

    private InternshipJournal(Name alias, Table<InternshipJournalRecord> aliased, Field<?>[] parameters) {
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
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.INTERNSHIP_JOURNAL_INTERNSHIP_RELEASE_ID, Indexes.INTERNSHIP_JOURNAL_PRIMARY, Indexes.INTERNSHIP_JOURNAL_STAFF_ID, Indexes.INTERNSHIP_JOURNAL_STUDENT_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<InternshipJournalRecord> getPrimaryKey() {
        return Keys.KEY_INTERNSHIP_JOURNAL_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<InternshipJournalRecord>> getKeys() {
        return Arrays.<UniqueKey<InternshipJournalRecord>>asList(Keys.KEY_INTERNSHIP_JOURNAL_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<InternshipJournalRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<InternshipJournalRecord, ?>>asList(Keys.INTERNSHIP_JOURNAL_IBFK_1, Keys.INTERNSHIP_JOURNAL_IBFK_2, Keys.INTERNSHIP_JOURNAL_IBFK_3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournal as(String alias) {
        return new InternshipJournal(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternshipJournal as(Name alias) {
        return new InternshipJournal(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public InternshipJournal rename(String name) {
        return new InternshipJournal(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public InternshipJournal rename(Name name) {
        return new InternshipJournal(name, null);
    }
}
