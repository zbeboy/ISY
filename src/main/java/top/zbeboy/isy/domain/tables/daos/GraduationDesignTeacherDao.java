/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.daos;


import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import top.zbeboy.isy.domain.tables.GraduationDesignTeacher;
import top.zbeboy.isy.domain.tables.records.GraduationDesignTeacherRecord;


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
@Repository
public class GraduationDesignTeacherDao extends DAOImpl<GraduationDesignTeacherRecord, top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher, String> {

    /**
     * Create a new GraduationDesignTeacherDao without any configuration
     */
    public GraduationDesignTeacherDao() {
        super(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER, top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher.class);
    }

    /**
     * Create a new GraduationDesignTeacherDao with an attached configuration
     */
    @Autowired
    public GraduationDesignTeacherDao(Configuration configuration) {
        super(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER, top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher object) {
        return object.getGraduationDesignTeacherId();
    }

    /**
     * Fetch records that have <code>graduation_design_teacher_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher> fetchByGraduationDesignTeacherId(String... values) {
        return fetch(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID, values);
    }

    /**
     * Fetch a unique record that has <code>graduation_design_teacher_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher fetchOneByGraduationDesignTeacherId(String value) {
        return fetchOne(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID, value);
    }

    /**
     * Fetch records that have <code>graduation_design_release_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher> fetchByGraduationDesignReleaseId(String... values) {
        return fetch(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID, values);
    }

    /**
     * Fetch records that have <code>staff_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher> fetchByStaffId(Integer... values) {
        return fetch(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.STAFF_ID, values);
    }

    /**
     * Fetch records that have <code>student_count IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher> fetchByStudentCount(Integer... values) {
        return fetch(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.STUDENT_COUNT, values);
    }

    /**
     * Fetch records that have <code>residue IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher> fetchByResidue(Integer... values) {
        return fetch(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.RESIDUE, values);
    }

    /**
     * Fetch records that have <code>username IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher> fetchByUsername(String... values) {
        return fetch(GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER.USERNAME, values);
    }
}
