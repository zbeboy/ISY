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

import top.zbeboy.isy.domain.tables.InternshipType;
import top.zbeboy.isy.domain.tables.records.InternshipTypeRecord;


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
@Repository
public class InternshipTypeDao extends DAOImpl<InternshipTypeRecord, top.zbeboy.isy.domain.tables.pojos.InternshipType, Integer> {

    /**
     * Create a new InternshipTypeDao without any configuration
     */
    public InternshipTypeDao() {
        super(InternshipType.INTERNSHIP_TYPE, top.zbeboy.isy.domain.tables.pojos.InternshipType.class);
    }

    /**
     * Create a new InternshipTypeDao with an attached configuration
     */
    @Autowired
    public InternshipTypeDao(Configuration configuration) {
        super(InternshipType.INTERNSHIP_TYPE, top.zbeboy.isy.domain.tables.pojos.InternshipType.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(top.zbeboy.isy.domain.tables.pojos.InternshipType object) {
        return object.getInternshipTypeId();
    }

    /**
     * Fetch records that have <code>internship_type_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipType> fetchByInternshipTypeId(Integer... values) {
        return fetch(InternshipType.INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>internship_type_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.InternshipType fetchOneByInternshipTypeId(Integer value) {
        return fetchOne(InternshipType.INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID, value);
    }

    /**
     * Fetch records that have <code>internship_type_name IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipType> fetchByInternshipTypeName(String... values) {
        return fetch(InternshipType.INTERNSHIP_TYPE.INTERNSHIP_TYPE_NAME, values);
    }
}
