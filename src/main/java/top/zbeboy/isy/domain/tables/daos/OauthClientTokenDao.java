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

import top.zbeboy.isy.domain.tables.OauthClientToken;
import top.zbeboy.isy.domain.tables.records.OauthClientTokenRecord;


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
public class OauthClientTokenDao extends DAOImpl<OauthClientTokenRecord, top.zbeboy.isy.domain.tables.pojos.OauthClientToken, String> {

    /**
     * Create a new OauthClientTokenDao without any configuration
     */
    public OauthClientTokenDao() {
        super(OauthClientToken.OAUTH_CLIENT_TOKEN, top.zbeboy.isy.domain.tables.pojos.OauthClientToken.class);
    }

    /**
     * Create a new OauthClientTokenDao with an attached configuration
     */
    @Autowired
    public OauthClientTokenDao(Configuration configuration) {
        super(OauthClientToken.OAUTH_CLIENT_TOKEN, top.zbeboy.isy.domain.tables.pojos.OauthClientToken.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.OauthClientToken object) {
        return object.getAuthenticationId();
    }

    /**
     * Fetch records that have <code>token_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.OauthClientToken> fetchByTokenId(String... values) {
        return fetch(OauthClientToken.OAUTH_CLIENT_TOKEN.TOKEN_ID, values);
    }

    /**
     * Fetch records that have <code>token IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.OauthClientToken> fetchByToken(byte[]... values) {
        return fetch(OauthClientToken.OAUTH_CLIENT_TOKEN.TOKEN, values);
    }

    /**
     * Fetch records that have <code>authentication_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.OauthClientToken> fetchByAuthenticationId(String... values) {
        return fetch(OauthClientToken.OAUTH_CLIENT_TOKEN.AUTHENTICATION_ID, values);
    }

    /**
     * Fetch a unique record that has <code>authentication_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.OauthClientToken fetchOneByAuthenticationId(String value) {
        return fetchOne(OauthClientToken.OAUTH_CLIENT_TOKEN.AUTHENTICATION_ID, value);
    }

    /**
     * Fetch records that have <code>user_name IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.OauthClientToken> fetchByUserName(String... values) {
        return fetch(OauthClientToken.OAUTH_CLIENT_TOKEN.USER_NAME, values);
    }

    /**
     * Fetch records that have <code>client_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.OauthClientToken> fetchByClientId(String... values) {
        return fetch(OauthClientToken.OAUTH_CLIENT_TOKEN.CLIENT_ID, values);
    }
}
