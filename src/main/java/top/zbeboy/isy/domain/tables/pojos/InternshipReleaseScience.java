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
        "jOOQ version:3.9.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class InternshipReleaseScience implements Serializable {

    private static final long serialVersionUID = 1835577605;

    private String  internshipReleaseId;
    private Integer scienceId;

    public InternshipReleaseScience() {}

    public InternshipReleaseScience(InternshipReleaseScience value) {
        this.internshipReleaseId = value.internshipReleaseId;
        this.scienceId = value.scienceId;
    }

    public InternshipReleaseScience(
        String  internshipReleaseId,
        Integer scienceId
    ) {
        this.internshipReleaseId = internshipReleaseId;
        this.scienceId = scienceId;
    }

    @NotNull
    @Size(max = 64)
    public String getInternshipReleaseId() {
        return this.internshipReleaseId;
    }

    public void setInternshipReleaseId(String internshipReleaseId) {
        this.internshipReleaseId = internshipReleaseId;
    }

    @NotNull
    public Integer getScienceId() {
        return this.scienceId;
    }

    public void setScienceId(Integer scienceId) {
        this.scienceId = scienceId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("InternshipReleaseScience (");

        sb.append(internshipReleaseId);
        sb.append(", ").append(scienceId);

        sb.append(")");
        return sb.toString();
    }
}