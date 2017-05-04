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

import top.zbeboy.isy.domain.tables.GraduationDesignSubjectOriginType;
import top.zbeboy.isy.domain.tables.records.GraduationDesignSubjectOriginTypeRecord;


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
@Repository
public class GraduationDesignSubjectOriginTypeDao extends DAOImpl<GraduationDesignSubjectOriginTypeRecord, top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType, Integer> {

    /**
     * Create a new GraduationDesignSubjectOriginTypeDao without any configuration
     */
    public GraduationDesignSubjectOriginTypeDao() {
        super(GraduationDesignSubjectOriginType.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE, top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType.class);
    }

    /**
     * Create a new GraduationDesignSubjectOriginTypeDao with an attached configuration
     */
    @Autowired
    public GraduationDesignSubjectOriginTypeDao(Configuration configuration) {
        super(GraduationDesignSubjectOriginType.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE, top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType object) {
        return object.getOriginTypeId();
    }

    /**
     * Fetch records that have <code>origin_type_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType> fetchByOriginTypeId(Integer... values) {
        return fetch(GraduationDesignSubjectOriginType.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>origin_type_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType fetchOneByOriginTypeId(Integer value) {
        return fetchOne(GraduationDesignSubjectOriginType.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID, value);
    }

    /**
     * Fetch records that have <code>origin_type_name IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType> fetchByOriginTypeName(String... values) {
        return fetch(GraduationDesignSubjectOriginType.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME, values);
    }
}
