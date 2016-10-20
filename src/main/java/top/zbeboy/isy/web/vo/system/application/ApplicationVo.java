package top.zbeboy.isy.web.vo.system.application;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-10-04.
 */
public class ApplicationVo {
    private Integer applicationId;
    @NotNull
    @Size(max = 30)
    private String applicationName;
    private Integer applicationSort;
    @NotNull
    private Integer applicationPid;
    @NotNull
    @Size(max = 300)
    private String applicationUrl;
    @NotNull
    @Size(max = 100)
    private String applicationCode;
    @NotNull
    @Size(max = 100)
    private String applicationEnName;
    @Size(max = 20)
    private String icon;
    @Size(max = 300)
    private String applicationDataUrlStartWith;
    @NotNull
    private Integer applicationTypeId;

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Integer getApplicationSort() {
        return applicationSort;
    }

    public void setApplicationSort(Integer applicationSort) {
        this.applicationSort = applicationSort;
    }

    public Integer getApplicationPid() {
        return applicationPid;
    }

    public void setApplicationPid(Integer applicationPid) {
        this.applicationPid = applicationPid;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public String getApplicationEnName() {
        return applicationEnName;
    }

    public void setApplicationEnName(String applicationEnName) {
        this.applicationEnName = applicationEnName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getApplicationDataUrlStartWith() {
        return applicationDataUrlStartWith;
    }

    public void setApplicationDataUrlStartWith(String applicationDataUrlStartWith) {
        this.applicationDataUrlStartWith = applicationDataUrlStartWith;
    }

    public Integer getApplicationTypeId() {
        return applicationTypeId;
    }

    public void setApplicationTypeId(Integer applicationTypeId) {
        this.applicationTypeId = applicationTypeId;
    }
}
