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
public class Application implements Serializable {

    private static final long serialVersionUID = 1844416835;

    private String  applicationId;
    private String  applicationName;
    private Integer applicationSort;
    private String  applicationPid;
    private String  applicationUrl;
    private String  applicationCode;
    private String  applicationEnName;
    private String  icon;
    private String  applicationDataUrlStartWith;

    public Application() {}

    public Application(Application value) {
        this.applicationId = value.applicationId;
        this.applicationName = value.applicationName;
        this.applicationSort = value.applicationSort;
        this.applicationPid = value.applicationPid;
        this.applicationUrl = value.applicationUrl;
        this.applicationCode = value.applicationCode;
        this.applicationEnName = value.applicationEnName;
        this.icon = value.icon;
        this.applicationDataUrlStartWith = value.applicationDataUrlStartWith;
    }

    public Application(
        String  applicationId,
        String  applicationName,
        Integer applicationSort,
        String  applicationPid,
        String  applicationUrl,
        String  applicationCode,
        String  applicationEnName,
        String  icon,
        String  applicationDataUrlStartWith
    ) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.applicationSort = applicationSort;
        this.applicationPid = applicationPid;
        this.applicationUrl = applicationUrl;
        this.applicationCode = applicationCode;
        this.applicationEnName = applicationEnName;
        this.icon = icon;
        this.applicationDataUrlStartWith = applicationDataUrlStartWith;
    }

    @NotNull
    @Size(max = 64)
    public String getApplicationId() {
        return this.applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @NotNull
    @Size(max = 30)
    public String getApplicationName() {
        return this.applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Integer getApplicationSort() {
        return this.applicationSort;
    }

    public void setApplicationSort(Integer applicationSort) {
        this.applicationSort = applicationSort;
    }

    @NotNull
    @Size(max = 64)
    public String getApplicationPid() {
        return this.applicationPid;
    }

    public void setApplicationPid(String applicationPid) {
        this.applicationPid = applicationPid;
    }

    @NotNull
    @Size(max = 300)
    public String getApplicationUrl() {
        return this.applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    @NotNull
    @Size(max = 100)
    public String getApplicationCode() {
        return this.applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    @NotNull
    @Size(max = 100)
    public String getApplicationEnName() {
        return this.applicationEnName;
    }

    public void setApplicationEnName(String applicationEnName) {
        this.applicationEnName = applicationEnName;
    }

    @Size(max = 20)
    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Size(max = 300)
    public String getApplicationDataUrlStartWith() {
        return this.applicationDataUrlStartWith;
    }

    public void setApplicationDataUrlStartWith(String applicationDataUrlStartWith) {
        this.applicationDataUrlStartWith = applicationDataUrlStartWith;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Application (");

        sb.append(applicationId);
        sb.append(", ").append(applicationName);
        sb.append(", ").append(applicationSort);
        sb.append(", ").append(applicationPid);
        sb.append(", ").append(applicationUrl);
        sb.append(", ").append(applicationCode);
        sb.append(", ").append(applicationEnName);
        sb.append(", ").append(icon);
        sb.append(", ").append(applicationDataUrlStartWith);

        sb.append(")");
        return sb.toString();
    }
}
