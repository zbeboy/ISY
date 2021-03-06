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
public class CollegeRole implements Serializable {

    private static final long serialVersionUID = 1366990964;

    private String  roleId;
    private Integer collegeId;
    private Byte    allowAgent;

    public CollegeRole() {}

    public CollegeRole(CollegeRole value) {
        this.roleId = value.roleId;
        this.collegeId = value.collegeId;
        this.allowAgent = value.allowAgent;
    }

    public CollegeRole(
        String  roleId,
        Integer collegeId,
        Byte    allowAgent
    ) {
        this.roleId = roleId;
        this.collegeId = collegeId;
        this.allowAgent = allowAgent;
    }

    @NotNull
    @Size(max = 64)
    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @NotNull
    public Integer getCollegeId() {
        return this.collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    public Byte getAllowAgent() {
        return this.allowAgent;
    }

    public void setAllowAgent(Byte allowAgent) {
        this.allowAgent = allowAgent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CollegeRole (");

        sb.append(roleId);
        sb.append(", ").append(collegeId);
        sb.append(", ").append(allowAgent);

        sb.append(")");
        return sb.toString();
    }
}
