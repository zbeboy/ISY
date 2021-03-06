/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


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
public class UsersKey implements Serializable {

    private static final long serialVersionUID = 1120978512;

    private String username;
    private String userKey;

    public UsersKey() {}

    public UsersKey(UsersKey value) {
        this.username = value.username;
        this.userKey = value.userKey;
    }

    public UsersKey(
        String username,
        String userKey
    ) {
        this.username = username;
        this.userKey = userKey;
    }

    @NotNull
    @Size(max = 64)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NotNull
    @Size(max = 64)
    public String getUserKey() {
        return this.userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("UsersKey (");

        sb.append(username);
        sb.append(", ").append(userKey);

        sb.append(")");
        return sb.toString();
    }
}
