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
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SystemAlert implements Serializable {

    private static final long serialVersionUID = -866138826;

    private String    systemAlertId;
    private String    alertContent;
    private Timestamp alertDate;
    private String    linkId;
    private Byte      isSee;
    private String    username;
    private Integer   systemAlertTypeId;

    public SystemAlert() {}

    public SystemAlert(SystemAlert value) {
        this.systemAlertId = value.systemAlertId;
        this.alertContent = value.alertContent;
        this.alertDate = value.alertDate;
        this.linkId = value.linkId;
        this.isSee = value.isSee;
        this.username = value.username;
        this.systemAlertTypeId = value.systemAlertTypeId;
    }

    public SystemAlert(
        String    systemAlertId,
        String    alertContent,
        Timestamp alertDate,
        String    linkId,
        Byte      isSee,
        String    username,
        Integer   systemAlertTypeId
    ) {
        this.systemAlertId = systemAlertId;
        this.alertContent = alertContent;
        this.alertDate = alertDate;
        this.linkId = linkId;
        this.isSee = isSee;
        this.username = username;
        this.systemAlertTypeId = systemAlertTypeId;
    }

    @NotNull
    @Size(max = 64)
    public String getSystemAlertId() {
        return this.systemAlertId;
    }

    public void setSystemAlertId(String systemAlertId) {
        this.systemAlertId = systemAlertId;
    }

    @NotNull
    @Size(max = 10)
    public String getAlertContent() {
        return this.alertContent;
    }

    public void setAlertContent(String alertContent) {
        this.alertContent = alertContent;
    }

    @NotNull
    public Timestamp getAlertDate() {
        return this.alertDate;
    }

    public void setAlertDate(Timestamp alertDate) {
        this.alertDate = alertDate;
    }

    @Size(max = 64)
    public String getLinkId() {
        return this.linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Byte getIsSee() {
        return this.isSee;
    }

    public void setIsSee(Byte isSee) {
        this.isSee = isSee;
    }

    @NotNull
    @Size(max = 64)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NotNull
    public Integer getSystemAlertTypeId() {
        return this.systemAlertTypeId;
    }

    public void setSystemAlertTypeId(Integer systemAlertTypeId) {
        this.systemAlertTypeId = systemAlertTypeId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SystemAlert (");

        sb.append(systemAlertId);
        sb.append(", ").append(alertContent);
        sb.append(", ").append(alertDate);
        sb.append(", ").append(linkId);
        sb.append(", ").append(isSee);
        sb.append(", ").append(username);
        sb.append(", ").append(systemAlertTypeId);

        sb.append(")");
        return sb.toString();
    }
}
