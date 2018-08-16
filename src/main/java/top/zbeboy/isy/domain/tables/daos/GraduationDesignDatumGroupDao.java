/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.daos;


import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import top.zbeboy.isy.domain.tables.GraduationDesignDatumGroup;
import top.zbeboy.isy.domain.tables.records.GraduationDesignDatumGroupRecord;


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
@Repository
public class GraduationDesignDatumGroupDao extends DAOImpl<GraduationDesignDatumGroupRecord, top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup, String> {

    /**
     * Create a new GraduationDesignDatumGroupDao without any configuration
     */
    public GraduationDesignDatumGroupDao() {
        super(GraduationDesignDatumGroup.GRADUATION_DESIGN_DATUM_GROUP, top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup.class);
    }

    /**
     * Create a new GraduationDesignDatumGroupDao with an attached configuration
     */
    @Autowired
    public GraduationDesignDatumGroupDao(Configuration configuration) {
        super(GraduationDesignDatumGroup.GRADUATION_DESIGN_DATUM_GROUP, top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup object) {
        return object.getGraduationDesignDatumGroupId();
    }

    /**
     * Fetch records that have <code>graduation_design_datum_group_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup> fetchByGraduationDesignDatumGroupId(String... values) {
        return fetch(GraduationDesignDatumGroup.GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_DATUM_GROUP_ID, values);
    }

    /**
     * Fetch a unique record that has <code>graduation_design_datum_group_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup fetchOneByGraduationDesignDatumGroupId(String value) {
        return fetchOne(GraduationDesignDatumGroup.GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_DATUM_GROUP_ID, value);
    }

    /**
     * Fetch records that have <code>file_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup> fetchByFileId(String... values) {
        return fetch(GraduationDesignDatumGroup.GRADUATION_DESIGN_DATUM_GROUP.FILE_ID, values);
    }

    /**
     * Fetch records that have <code>graduation_design_teacher_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup> fetchByGraduationDesignTeacherId(String... values) {
        return fetch(GraduationDesignDatumGroup.GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID, values);
    }

    /**
     * Fetch records that have <code>upload_time IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup> fetchByUploadTime(Timestamp... values) {
        return fetch(GraduationDesignDatumGroup.GRADUATION_DESIGN_DATUM_GROUP.UPLOAD_TIME, values);
    }
}