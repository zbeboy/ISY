/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.pojos;


import java.io.Serializable;
import java.sql.Date;

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
public class DefenseOrder implements Serializable {

    private static final long serialVersionUID = -1030151827;

    private String defenseOrderId;
    private String studentNumber;
    private String studentName;
    private String subject;
    private Date   defenseDate;
    private String defenseTime;
    private String staffName;
    private String graduationDesignTutorId;

    public DefenseOrder() {}

    public DefenseOrder(DefenseOrder value) {
        this.defenseOrderId = value.defenseOrderId;
        this.studentNumber = value.studentNumber;
        this.studentName = value.studentName;
        this.subject = value.subject;
        this.defenseDate = value.defenseDate;
        this.defenseTime = value.defenseTime;
        this.staffName = value.staffName;
        this.graduationDesignTutorId = value.graduationDesignTutorId;
    }

    public DefenseOrder(
        String defenseOrderId,
        String studentNumber,
        String studentName,
        String subject,
        Date   defenseDate,
        String defenseTime,
        String staffName,
        String graduationDesignTutorId
    ) {
        this.defenseOrderId = defenseOrderId;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.subject = subject;
        this.defenseDate = defenseDate;
        this.defenseTime = defenseTime;
        this.staffName = staffName;
        this.graduationDesignTutorId = graduationDesignTutorId;
    }

    @NotNull
    @Size(max = 64)
    public String getDefenseOrderId() {
        return this.defenseOrderId;
    }

    public void setDefenseOrderId(String defenseOrderId) {
        this.defenseOrderId = defenseOrderId;
    }

    @NotNull
    @Size(max = 20)
    public String getStudentNumber() {
        return this.studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    @NotNull
    @Size(max = 30)
    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @NotNull
    @Size(max = 100)
    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @NotNull
    public Date getDefenseDate() {
        return this.defenseDate;
    }

    public void setDefenseDate(Date defenseDate) {
        this.defenseDate = defenseDate;
    }

    @NotNull
    @Size(max = 20)
    public String getDefenseTime() {
        return this.defenseTime;
    }

    public void setDefenseTime(String defenseTime) {
        this.defenseTime = defenseTime;
    }

    @NotNull
    @Size(max = 30)
    public String getStaffName() {
        return this.staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    @NotNull
    @Size(max = 64)
    public String getGraduationDesignTutorId() {
        return this.graduationDesignTutorId;
    }

    public void setGraduationDesignTutorId(String graduationDesignTutorId) {
        this.graduationDesignTutorId = graduationDesignTutorId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DefenseOrder (");

        sb.append(defenseOrderId);
        sb.append(", ").append(studentNumber);
        sb.append(", ").append(studentName);
        sb.append(", ").append(subject);
        sb.append(", ").append(defenseDate);
        sb.append(", ").append(defenseTime);
        sb.append(", ").append(staffName);
        sb.append(", ").append(graduationDesignTutorId);

        sb.append(")");
        return sb.toString();
    }
}
