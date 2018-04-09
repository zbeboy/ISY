/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.records;


import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;

import top.zbeboy.isy.domain.tables.OauthClientToken;


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
public class OauthClientTokenRecord extends UpdatableRecordImpl<OauthClientTokenRecord> implements Record5<String, byte[], String, String, String> {

    private static final long serialVersionUID = 1128977815;

    /**
     * Setter for <code>isy.oauth_client_token.token_id</code>.
     */
    public void setTokenId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>isy.oauth_client_token.token_id</code>.
     */
    @Size(max = 256)
    public String getTokenId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>isy.oauth_client_token.token</code>.
     */
    public void setToken(byte... value) {
        set(1, value);
    }

    /**
     * Getter for <code>isy.oauth_client_token.token</code>.
     */
    public byte[] getToken() {
        return (byte[]) get(1);
    }

    /**
     * Setter for <code>isy.oauth_client_token.authentication_id</code>.
     */
    public void setAuthenticationId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>isy.oauth_client_token.authentication_id</code>.
     */
    @NotNull
    @Size(max = 256)
    public String getAuthenticationId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>isy.oauth_client_token.user_name</code>.
     */
    public void setUserName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>isy.oauth_client_token.user_name</code>.
     */
    @Size(max = 256)
    public String getUserName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>isy.oauth_client_token.client_id</code>.
     */
    public void setClientId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>isy.oauth_client_token.client_id</code>.
     */
    @Size(max = 256)
    public String getClientId() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, byte[], String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, byte[], String, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return OauthClientToken.OAUTH_CLIENT_TOKEN.TOKEN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<byte[]> field2() {
        return OauthClientToken.OAUTH_CLIENT_TOKEN.TOKEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return OauthClientToken.OAUTH_CLIENT_TOKEN.AUTHENTICATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return OauthClientToken.OAUTH_CLIENT_TOKEN.USER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return OauthClientToken.OAUTH_CLIENT_TOKEN.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getTokenId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] component2() {
        return getToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getAuthenticationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getUserName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getClientId();
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
    public String value3() {
        return getAuthenticationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getUserName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthClientTokenRecord value1(String value) {
        setTokenId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthClientTokenRecord value2(byte... value) {
        setToken(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthClientTokenRecord value3(String value) {
        setAuthenticationId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthClientTokenRecord value4(String value) {
        setUserName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthClientTokenRecord value5(String value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthClientTokenRecord values(String value1, byte[] value2, String value3, String value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OauthClientTokenRecord
     */
    public OauthClientTokenRecord() {
        super(OauthClientToken.OAUTH_CLIENT_TOKEN);
    }

    /**
     * Create a detached, initialised OauthClientTokenRecord
     */
    public OauthClientTokenRecord(String tokenId, byte[] token, String authenticationId, String userName, String clientId) {
        super(OauthClientToken.OAUTH_CLIENT_TOKEN);

        set(0, tokenId);
        set(1, token);
        set(2, authenticationId);
        set(3, userName);
        set(4, clientId);
    }
}
