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

import top.zbeboy.isy.domain.tables.UsersKey;
import top.zbeboy.isy.domain.tables.records.UsersKeyRecord;


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
public class UsersKeyDao extends DAOImpl<UsersKeyRecord, top.zbeboy.isy.domain.tables.pojos.UsersKey, String> {

    /**
     * Create a new UsersKeyDao without any configuration
     */
    public UsersKeyDao() {
        super(UsersKey.USERS_KEY, top.zbeboy.isy.domain.tables.pojos.UsersKey.class);
    }

    /**
     * Create a new UsersKeyDao with an attached configuration
     */
    @Autowired
    public UsersKeyDao(Configuration configuration) {
        super(UsersKey.USERS_KEY, top.zbeboy.isy.domain.tables.pojos.UsersKey.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.UsersKey object) {
        return object.getUsername();
    }

    /**
     * Fetch records that have <code>username IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.UsersKey> fetchByUsername(String... values) {
        return fetch(UsersKey.USERS_KEY.USERNAME, values);
    }

    /**
     * Fetch a unique record that has <code>username = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.UsersKey fetchOneByUsername(String value) {
        return fetchOne(UsersKey.USERS_KEY.USERNAME, value);
    }

    /**
     * Fetch records that have <code>user_key IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.UsersKey> fetchByUserKey(String... values) {
        return fetch(UsersKey.USERS_KEY.USER_KEY, values);
    }

    /**
     * Fetch a unique record that has <code>user_key = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.UsersKey fetchOneByUserKey(String value) {
        return fetchOne(UsersKey.USERS_KEY.USER_KEY, value);
    }
}
