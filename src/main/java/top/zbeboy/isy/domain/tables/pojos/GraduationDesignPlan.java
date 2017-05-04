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
public class GraduationDesignPlan implements Serializable {

    private static final long serialVersionUID = 2108183708;

    private String graduationDesignPlanId;
    private String scheduling;
    private String supervisionTime;
    private String guideLocation;
    private String guideContent;
    private String note;
    private String graduationDesignTeacherId;

    public GraduationDesignPlan() {}

    public GraduationDesignPlan(GraduationDesignPlan value) {
        this.graduationDesignPlanId = value.graduationDesignPlanId;
        this.scheduling = value.scheduling;
        this.supervisionTime = value.supervisionTime;
        this.guideLocation = value.guideLocation;
        this.guideContent = value.guideContent;
        this.note = value.note;
        this.graduationDesignTeacherId = value.graduationDesignTeacherId;
    }

    public GraduationDesignPlan(
        String graduationDesignPlanId,
        String scheduling,
        String supervisionTime,
        String guideLocation,
        String guideContent,
        String note,
        String graduationDesignTeacherId
    ) {
        this.graduationDesignPlanId = graduationDesignPlanId;
        this.scheduling = scheduling;
        this.supervisionTime = supervisionTime;
        this.guideLocation = guideLocation;
        this.guideContent = guideContent;
        this.note = note;
        this.graduationDesignTeacherId = graduationDesignTeacherId;
    }

    @NotNull
    @Size(max = 64)
    public String getGraduationDesignPlanId() {
        return this.graduationDesignPlanId;
    }

    public void setGraduationDesignPlanId(String graduationDesignPlanId) {
        this.graduationDesignPlanId = graduationDesignPlanId;
    }

    @NotNull
    @Size(max = 100)
    public String getScheduling() {
        return this.scheduling;
    }

    public void setScheduling(String scheduling) {
        this.scheduling = scheduling;
    }

    @NotNull
    @Size(max = 100)
    public String getSupervisionTime() {
        return this.supervisionTime;
    }

    public void setSupervisionTime(String supervisionTime) {
        this.supervisionTime = supervisionTime;
    }

    @NotNull
    @Size(max = 100)
    public String getGuideLocation() {
        return this.guideLocation;
    }

    public void setGuideLocation(String guideLocation) {
        this.guideLocation = guideLocation;
    }

    @NotNull
    @Size(max = 150)
    public String getGuideContent() {
        return this.guideContent;
    }

    public void setGuideContent(String guideContent) {
        this.guideContent = guideContent;
    }

    @NotNull
    @Size(max = 100)
    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @NotNull
    @Size(max = 64)
    public String getGraduationDesignTeacherId() {
        return this.graduationDesignTeacherId;
    }

    public void setGraduationDesignTeacherId(String graduationDesignTeacherId) {
        this.graduationDesignTeacherId = graduationDesignTeacherId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GraduationDesignPlan (");

        sb.append(graduationDesignPlanId);
        sb.append(", ").append(scheduling);
        sb.append(", ").append(supervisionTime);
        sb.append(", ").append(guideLocation);
        sb.append(", ").append(guideContent);
        sb.append(", ").append(note);
        sb.append(", ").append(graduationDesignTeacherId);

        sb.append(")");
        return sb.toString();
    }
}
