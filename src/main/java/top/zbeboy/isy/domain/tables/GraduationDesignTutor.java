/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.GraduationDesignTutorRecord;


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
public class GraduationDesignTutor extends TableImpl<GraduationDesignTutorRecord> {

    private static final long serialVersionUID = -1837774068;

    /**
     * The reference instance of <code>isy.graduation_design_tutor</code>
     */
    public static final GraduationDesignTutor GRADUATION_DESIGN_TUTOR = new GraduationDesignTutor();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GraduationDesignTutorRecord> getRecordType() {
        return GraduationDesignTutorRecord.class;
    }

    /**
     * The column <code>isy.graduation_design_tutor.graduation_design_teacher_id</code>.
     */
    public final TableField<GraduationDesignTutorRecord, String> GRADUATION_DESIGN_TEACHER_ID = createField("graduation_design_teacher_id", org.jooq.impl.SQLDataType.VARCHAR.length(64).nullable(false), this, "");

    /**
     * The column <code>isy.graduation_design_tutor.student_id</code>.
     */
    public final TableField<GraduationDesignTutorRecord, Integer> STUDENT_ID = createField("student_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>isy.graduation_design_tutor</code> table reference
     */
    public GraduationDesignTutor() {
        this("graduation_design_tutor", null);
    }

    /**
     * Create an aliased <code>isy.graduation_design_tutor</code> table reference
     */
    public GraduationDesignTutor(String alias) {
        this(alias, GRADUATION_DESIGN_TUTOR);
    }

    private GraduationDesignTutor(String alias, Table<GraduationDesignTutorRecord> aliased) {
        this(alias, aliased, null);
    }

    private GraduationDesignTutor(String alias, Table<GraduationDesignTutorRecord> aliased, Field<?>[] parameters) {
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
    public List<ForeignKey<GraduationDesignTutorRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GraduationDesignTutorRecord, ?>>asList(Keys.GRADUATION_DESIGN_TUTOR_IBFK_1, Keys.GRADUATION_DESIGN_TUTOR_IBFK_2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraduationDesignTutor as(String alias) {
        return new GraduationDesignTutor(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GraduationDesignTutor rename(String name) {
        return new GraduationDesignTutor(name, null);
    }
}
