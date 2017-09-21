/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import javax.annotation.Generated;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.TableRecordImpl;

import top.zbeboy.isy.domain.tables.OauthRefreshToken;


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
public class OauthRefreshTokenRecord extends TableRecordImpl<OauthRefreshTokenRecord> implements Record3<String, byte[], byte[]> {

    private static final long serialVersionUID = 1976061049;

    /**
     * Setter for <code>isy.oauth_refresh_token.token_id</code>.
     */
    public void setTokenId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.oauth_refresh_token.token_id</code>.
     */
    @Size(max = 256)
    public String getTokenId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.oauth_refresh_token.token</code>.
     */
    public void setToken(byte... value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.oauth_refresh_token.token</code>.
     */
    public byte[] getToken() {
        return (byte[]) get(1);
    }

    /**
     * Setter for <code>isy.oauth_refresh_token.authentication</code>.
     */
    public void setAuthentication(byte... value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.oauth_refresh_token.authentication</code>.
     */
    public byte[] getAuthentication() {
        return (byte[]) get(2);
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, byte[], byte[]> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, byte[], byte[]> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return OauthRefreshToken.OAUTH_REFRESH_TOKEN.TOKEN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<byte[]> field2() {
        return OauthRefreshToken.OAUTH_REFRESH_TOKEN.TOKEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<byte[]> field3() {
        return OauthRefreshToken.OAUTH_REFRESH_TOKEN.AUTHENTICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getTokenId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] value2() {
        return getToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] value3() {
        return getAuthentication();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthRefreshTokenRecord value1(String value) {
        setTokenId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthRefreshTokenRecord value2(byte... value) {
        setToken(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthRefreshTokenRecord value3(byte... value) {
        setAuthentication(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthRefreshTokenRecord values(String value1, byte[] value2, byte[] value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OauthRefreshTokenRecord
     */
    public OauthRefreshTokenRecord() {
        super(OauthRefreshToken.OAUTH_REFRESH_TOKEN);
    }

    /**
     * Create a detached, initialised OauthRefreshTokenRecord
     */
    public OauthRefreshTokenRecord(String tokenId, byte[] token, byte[] authentication) {
        super(OauthRefreshToken.OAUTH_REFRESH_TOKEN);

        set(0, tokenId);
        set(1, token);
        set(2, authentication);
    }
}
