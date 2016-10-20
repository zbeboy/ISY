package top.zbeboy.isy.web.bean.system.log;

import top.zbeboy.isy.domain.tables.pojos.SystemLog;

/**
 * Created by lenovo on 2016-09-15.
 */
public class SystemLogBean extends SystemLog {
    private String operatingTimeNew;

    public String getOperatingTimeNew() {
        return operatingTimeNew;
    }

    public void setOperatingTimeNew(String operatingTimeNew) {
        this.operatingTimeNew = operatingTimeNew;
    }
}
