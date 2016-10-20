package top.zbeboy.isy.web.bean.system.application;

import top.zbeboy.isy.domain.tables.pojos.Application;

/**
 * Created by lenovo on 2016-10-02.
 */
public class ApplicationBean extends Application{
    private String applicationTypeName;
    private String applicationPidName;

    public String getApplicationTypeName() {
        return applicationTypeName;
    }

    public void setApplicationTypeName(String applicationTypeName) {
        this.applicationTypeName = applicationTypeName;
    }

    public String getApplicationPidName() {
        return applicationPidName;
    }

    public void setApplicationPidName(String applicationPidName) {
        this.applicationPidName = applicationPidName;
    }
}
