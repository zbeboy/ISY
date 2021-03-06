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
public class Building implements Serializable {

    private static final long serialVersionUID = 867457199;

    private Integer buildingId;
    private String  buildingName;
    private Byte    buildingIsDel;
    private Integer collegeId;

    public Building() {}

    public Building(Building value) {
        this.buildingId = value.buildingId;
        this.buildingName = value.buildingName;
        this.buildingIsDel = value.buildingIsDel;
        this.collegeId = value.collegeId;
    }

    public Building(
        Integer buildingId,
        String  buildingName,
        Byte    buildingIsDel,
        Integer collegeId
    ) {
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.buildingIsDel = buildingIsDel;
        this.collegeId = collegeId;
    }

    public Integer getBuildingId() {
        return this.buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    @NotNull
    @Size(max = 30)
    public String getBuildingName() {
        return this.buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Byte getBuildingIsDel() {
        return this.buildingIsDel;
    }

    public void setBuildingIsDel(Byte buildingIsDel) {
        this.buildingIsDel = buildingIsDel;
    }

    @NotNull
    public Integer getCollegeId() {
        return this.collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Building (");

        sb.append(buildingId);
        sb.append(", ").append(buildingName);
        sb.append(", ").append(buildingIsDel);
        sb.append(", ").append(collegeId);

        sb.append(")");
        return sb.toString();
    }
}
