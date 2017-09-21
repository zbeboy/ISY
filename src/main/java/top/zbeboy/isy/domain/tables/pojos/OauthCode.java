/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.validation.constraints.Size;


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
public class OauthCode implements Serializable {

    private static final long serialVersionUID = 1519857134;

    private String code;
    private byte[] authentication;

    public OauthCode() {}

    public OauthCode(OauthCode value) {
        this.code = value.code;
        this.authentication = value.authentication;
    }

    public OauthCode(
        String code,
        byte[] authentication
    ) {
        this.code = code;
        this.authentication = authentication;
    }

    @Size(max = 256)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getAuthentication() {
        return this.authentication;
    }

    public void setAuthentication(byte... authentication) {
        this.authentication = authentication;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("OauthCode (");

        sb.append(code);
        sb.append(", ").append("[binary...]");

        sb.append(")");
        return sb.toString();
    }
}
