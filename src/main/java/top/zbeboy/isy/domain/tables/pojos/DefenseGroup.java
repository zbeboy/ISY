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
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DefenseGroup implements Serializable {

    private static final long serialVersionUID = -78895828;

    private String  defenseGroupId;
    private String  defenseGroupName;
    private Integer defenseGroupNumber;
    private Integer schoolroomId;
    private String  note;
    private Integer groupLeader;
    private String  defenseArrangementId;

    public DefenseGroup() {}

    public DefenseGroup(DefenseGroup value) {
        this.defenseGroupId = value.defenseGroupId;
        this.defenseGroupName = value.defenseGroupName;
        this.defenseGroupNumber = value.defenseGroupNumber;
        this.schoolroomId = value.schoolroomId;
        this.note = value.note;
        this.groupLeader = value.groupLeader;
        this.defenseArrangementId = value.defenseArrangementId;
    }

    public DefenseGroup(
        String  defenseGroupId,
        String  defenseGroupName,
        Integer defenseGroupNumber,
        Integer schoolroomId,
        String  note,
        Integer groupLeader,
        String  defenseArrangementId
    ) {
        this.defenseGroupId = defenseGroupId;
        this.defenseGroupName = defenseGroupName;
        this.defenseGroupNumber = defenseGroupNumber;
        this.schoolroomId = schoolroomId;
        this.note = note;
        this.groupLeader = groupLeader;
        this.defenseArrangementId = defenseArrangementId;
    }

    @NotNull
    @Size(max = 64)
    public String getDefenseGroupId() {
        return this.defenseGroupId;
    }

    public void setDefenseGroupId(String defenseGroupId) {
        this.defenseGroupId = defenseGroupId;
    }

    @NotNull
    @Size(max = 20)
    public String getDefenseGroupName() {
        return this.defenseGroupName;
    }

    public void setDefenseGroupName(String defenseGroupName) {
        this.defenseGroupName = defenseGroupName;
    }

    @NotNull
    public Integer getDefenseGroupNumber() {
        return this.defenseGroupNumber;
    }

    public void setDefenseGroupNumber(Integer defenseGroupNumber) {
        this.defenseGroupNumber = defenseGroupNumber;
    }

    @NotNull
    public Integer getSchoolroomId() {
        return this.schoolroomId;
    }

    public void setSchoolroomId(Integer schoolroomId) {
        this.schoolroomId = schoolroomId;
    }

    @Size(max = 100)
    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @NotNull
    public Integer getGroupLeader() {
        return this.groupLeader;
    }

    public void setGroupLeader(Integer groupLeader) {
        this.groupLeader = groupLeader;
    }

    @NotNull
    @Size(max = 64)
    public String getDefenseArrangementId() {
        return this.defenseArrangementId;
    }

    public void setDefenseArrangementId(String defenseArrangementId) {
        this.defenseArrangementId = defenseArrangementId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DefenseGroup (");

        sb.append(defenseGroupId);
        sb.append(", ").append(defenseGroupName);
        sb.append(", ").append(defenseGroupNumber);
        sb.append(", ").append(schoolroomId);
        sb.append(", ").append(note);
        sb.append(", ").append(groupLeader);
        sb.append(", ").append(defenseArrangementId);

        sb.append(")");
        return sb.toString();
    }
}