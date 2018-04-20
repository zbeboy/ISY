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

import top.zbeboy.isy.domain.tables.GraduationDesignSubjectType;
import top.zbeboy.isy.domain.tables.records.GraduationDesignSubjectTypeRecord;


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
public class GraduationDesignSubjectTypeDao extends DAOImpl<GraduationDesignSubjectTypeRecord, top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType, Integer> {

    /**
     * Create a new GraduationDesignSubjectTypeDao without any configuration
     */
    public GraduationDesignSubjectTypeDao() {
        super(GraduationDesignSubjectType.GRADUATION_DESIGN_SUBJECT_TYPE, top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType.class);
    }

    /**
     * Create a new GraduationDesignSubjectTypeDao with an attached configuration
     */
    @Autowired
    public GraduationDesignSubjectTypeDao(Configuration configuration) {
        super(GraduationDesignSubjectType.GRADUATION_DESIGN_SUBJECT_TYPE, top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType object) {
        return object.getSubjectTypeId();
    }

    /**
     * Fetch records that have <code>subject_type_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType> fetchBySubjectTypeId(Integer... values) {
        return fetch(GraduationDesignSubjectType.GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>subject_type_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType fetchOneBySubjectTypeId(Integer value) {
        return fetchOne(GraduationDesignSubjectType.GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID, value);
    }

    /**
     * Fetch records that have <code>subject_type_name IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType> fetchBySubjectTypeName(String... values) {
        return fetch(GraduationDesignSubjectType.GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME, values);
    }
}
