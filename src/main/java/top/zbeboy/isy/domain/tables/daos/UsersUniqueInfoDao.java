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

import top.zbeboy.isy.domain.tables.UsersUniqueInfo;
import top.zbeboy.isy.domain.tables.records.UsersUniqueInfoRecord;


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
public class UsersUniqueInfoDao extends DAOImpl<UsersUniqueInfoRecord, top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo, String> {

    /**
     * Create a new UsersUniqueInfoDao without any configuration
     */
    public UsersUniqueInfoDao() {
        super(UsersUniqueInfo.USERS_UNIQUE_INFO, top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo.class);
    }

    /**
     * Create a new UsersUniqueInfoDao with an attached configuration
     */
    @Autowired
    public UsersUniqueInfoDao(Configuration configuration) {
        super(UsersUniqueInfo.USERS_UNIQUE_INFO, top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo object) {
        return object.getUsername();
    }

    /**
     * Fetch records that have <code>username IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo> fetchByUsername(String... values) {
        return fetch(UsersUniqueInfo.USERS_UNIQUE_INFO.USERNAME, values);
    }

    /**
     * Fetch a unique record that has <code>username = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo fetchOneByUsername(String value) {
        return fetchOne(UsersUniqueInfo.USERS_UNIQUE_INFO.USERNAME, value);
    }

    /**
     * Fetch records that have <code>id_card IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo> fetchByIdCard(String... values) {
        return fetch(UsersUniqueInfo.USERS_UNIQUE_INFO.ID_CARD, values);
    }

    /**
     * Fetch a unique record that has <code>id_card = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo fetchOneByIdCard(String value) {
        return fetchOne(UsersUniqueInfo.USERS_UNIQUE_INFO.ID_CARD, value);
    }
}