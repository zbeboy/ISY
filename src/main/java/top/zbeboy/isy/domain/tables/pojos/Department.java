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
        "jOOQ version:3.9.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Department implements Serializable {

    private static final long serialVersionUID = 310945227;

    private Integer departmentId;
    private String  departmentName;
    private Byte    departmentIsDel;
    private Integer collegeId;

    public Department() {}

    public Department(Department value) {
        this.departmentId = value.departmentId;
        this.departmentName = value.departmentName;
        this.departmentIsDel = value.departmentIsDel;
        this.collegeId = value.collegeId;
    }

    public Department(
        Integer departmentId,
        String  departmentName,
        Byte    departmentIsDel,
        Integer collegeId
    ) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentIsDel = departmentIsDel;
        this.collegeId = collegeId;
    }

    @NotNull
    public Integer getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    @NotNull
    @Size(max = 200)
    public String getDepartmentName() {
        return this.departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Byte getDepartmentIsDel() {
        return this.departmentIsDel;
    }

    public void setDepartmentIsDel(Byte departmentIsDel) {
        this.departmentIsDel = departmentIsDel;
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
        StringBuilder sb = new StringBuilder("Department (");

        sb.append(departmentId);
        sb.append(", ").append(departmentName);
        sb.append(", ").append(departmentIsDel);
        sb.append(", ").append(collegeId);

        sb.append(")");
        return sb.toString();
    }
}
