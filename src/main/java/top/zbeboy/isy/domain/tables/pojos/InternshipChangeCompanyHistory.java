/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.pojos;


import java.io.Serializable;
import java.sql.Timestamp;

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
public class InternshipChangeCompanyHistory implements Serializable {

    private static final long serialVersionUID = 1379390091;

    private String    internshipChangeCompanyHistoryId;
    private Integer   studentId;
    private String    internshipReleaseId;
    private String    companyName;
    private String    companyAddress;
    private String    companyContacts;
    private String    companyTel;
    private Timestamp changeTime;

    public InternshipChangeCompanyHistory() {}

    public InternshipChangeCompanyHistory(InternshipChangeCompanyHistory value) {
        this.internshipChangeCompanyHistoryId = value.internshipChangeCompanyHistoryId;
        this.studentId = value.studentId;
        this.internshipReleaseId = value.internshipReleaseId;
        this.companyName = value.companyName;
        this.companyAddress = value.companyAddress;
        this.companyContacts = value.companyContacts;
        this.companyTel = value.companyTel;
        this.changeTime = value.changeTime;
    }

    public InternshipChangeCompanyHistory(
        String    internshipChangeCompanyHistoryId,
        Integer   studentId,
        String    internshipReleaseId,
        String    companyName,
        String    companyAddress,
        String    companyContacts,
        String    companyTel,
        Timestamp changeTime
    ) {
        this.internshipChangeCompanyHistoryId = internshipChangeCompanyHistoryId;
        this.studentId = studentId;
        this.internshipReleaseId = internshipReleaseId;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyContacts = companyContacts;
        this.companyTel = companyTel;
        this.changeTime = changeTime;
    }

    @NotNull
    @Size(max = 64)
    public String getInternshipChangeCompanyHistoryId() {
        return this.internshipChangeCompanyHistoryId;
    }

    public void setInternshipChangeCompanyHistoryId(String internshipChangeCompanyHistoryId) {
        this.internshipChangeCompanyHistoryId = internshipChangeCompanyHistoryId;
    }

    @NotNull
    public Integer getStudentId() {
        return this.studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    @NotNull
    @Size(max = 64)
    public String getInternshipReleaseId() {
        return this.internshipReleaseId;
    }

    public void setInternshipReleaseId(String internshipReleaseId) {
        this.internshipReleaseId = internshipReleaseId;
    }

    @Size(max = 200)
    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Size(max = 500)
    public String getCompanyAddress() {
        return this.companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    @Size(max = 10)
    public String getCompanyContacts() {
        return this.companyContacts;
    }

    public void setCompanyContacts(String companyContacts) {
        this.companyContacts = companyContacts;
    }

    @Size(max = 20)
    public String getCompanyTel() {
        return this.companyTel;
    }

    public void setCompanyTel(String companyTel) {
        this.companyTel = companyTel;
    }

    @NotNull
    public Timestamp getChangeTime() {
        return this.changeTime;
    }

    public void setChangeTime(Timestamp changeTime) {
        this.changeTime = changeTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("InternshipChangeCompanyHistory (");

        sb.append(internshipChangeCompanyHistoryId);
        sb.append(", ").append(studentId);
        sb.append(", ").append(internshipReleaseId);
        sb.append(", ").append(companyName);
        sb.append(", ").append(companyAddress);
        sb.append(", ").append(companyContacts);
        sb.append(", ").append(companyTel);
        sb.append(", ").append(changeTime);

        sb.append(")");
        return sb.toString();
    }
}
