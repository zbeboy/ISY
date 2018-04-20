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
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GraduationDesignTutor implements Serializable {

    private static final long serialVersionUID = -1867583245;

    private String  graduationDesignTutorId;
    private String  graduationDesignTeacherId;
    private Integer studentId;

    public GraduationDesignTutor() {}

    public GraduationDesignTutor(GraduationDesignTutor value) {
        this.graduationDesignTutorId = value.graduationDesignTutorId;
        this.graduationDesignTeacherId = value.graduationDesignTeacherId;
        this.studentId = value.studentId;
    }

    public GraduationDesignTutor(
        String  graduationDesignTutorId,
        String  graduationDesignTeacherId,
        Integer studentId
    ) {
        this.graduationDesignTutorId = graduationDesignTutorId;
        this.graduationDesignTeacherId = graduationDesignTeacherId;
        this.studentId = studentId;
    }

    @NotNull
    @Size(max = 64)
    public String getGraduationDesignTutorId() {
        return this.graduationDesignTutorId;
    }

    public void setGraduationDesignTutorId(String graduationDesignTutorId) {
        this.graduationDesignTutorId = graduationDesignTutorId;
    }

    @NotNull
    @Size(max = 64)
    public String getGraduationDesignTeacherId() {
        return this.graduationDesignTeacherId;
    }

    public void setGraduationDesignTeacherId(String graduationDesignTeacherId) {
        this.graduationDesignTeacherId = graduationDesignTeacherId;
    }

    @NotNull
    public Integer getStudentId() {
        return this.studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GraduationDesignTutor (");

        sb.append(graduationDesignTutorId);
        sb.append(", ").append(graduationDesignTeacherId);
        sb.append(", ").append(studentId);

        sb.append(")");
        return sb.toString();
    }
}
