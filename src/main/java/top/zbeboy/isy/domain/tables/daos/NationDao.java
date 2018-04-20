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

import top.zbeboy.isy.domain.tables.Nation;
import top.zbeboy.isy.domain.tables.records.NationRecord;


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
public class NationDao extends DAOImpl<NationRecord, top.zbeboy.isy.domain.tables.pojos.Nation, Integer> {

    /**
     * Create a new NationDao without any configuration
     */
    public NationDao() {
        super(Nation.NATION, top.zbeboy.isy.domain.tables.pojos.Nation.class);
    }

    /**
     * Create a new NationDao with an attached configuration
     */
    @Autowired
    public NationDao(Configuration configuration) {
        super(Nation.NATION, top.zbeboy.isy.domain.tables.pojos.Nation.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(top.zbeboy.isy.domain.tables.pojos.Nation object) {
        return object.getNationId();
    }

    /**
     * Fetch records that have <code>nation_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.Nation> fetchByNationId(Integer... values) {
        return fetch(Nation.NATION.NATION_ID, values);
    }

    /**
     * Fetch a unique record that has <code>nation_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.Nation fetchOneByNationId(Integer value) {
        return fetchOne(Nation.NATION.NATION_ID, value);
    }

    /**
     * Fetch records that have <code>nation_name IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.Nation> fetchByNationName(String... values) {
        return fetch(Nation.NATION.NATION_NAME, values);
    }
}
