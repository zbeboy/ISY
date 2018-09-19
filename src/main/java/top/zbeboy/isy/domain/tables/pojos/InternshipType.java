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
public class InternshipType implements Serializable {

    private static final long serialVersionUID = -1456854258;

    private Integer internshipTypeId;
    private String  internshipTypeName;

    public InternshipType() {}

    public InternshipType(InternshipType value) {
        this.internshipTypeId = value.internshipTypeId;
        this.internshipTypeName = value.internshipTypeName;
    }

    public InternshipType(
        Integer internshipTypeId,
        String  internshipTypeName
    ) {
        this.internshipTypeId = internshipTypeId;
        this.internshipTypeName = internshipTypeName;
    }

    public Integer getInternshipTypeId() {
        return this.internshipTypeId;
    }

    public void setInternshipTypeId(Integer internshipTypeId) {
        this.internshipTypeId = internshipTypeId;
    }

    @NotNull
    @Size(max = 100)
    public String getInternshipTypeName() {
        return this.internshipTypeName;
    }

    public void setInternshipTypeName(String internshipTypeName) {
        this.internshipTypeName = internshipTypeName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("InternshipType (");

        sb.append(internshipTypeId);
        sb.append(", ").append(internshipTypeName);

        sb.append(")");
        return sb.toString();
    }
}
