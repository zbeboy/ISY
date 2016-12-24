package top.zbeboy.isy.web.bean.system.alert;

import top.zbeboy.isy.domain.tables.pojos.SystemAlert;

/**
 * Created by lenovo on 2016-12-24.
 */
public class SystemAlertBean extends SystemAlert {
    private String icon;
    private String alertDateStr;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAlertDateStr() {
        return alertDateStr;
    }

    public void setAlertDateStr(String alertDateStr) {
        this.alertDateStr = alertDateStr;
    }
}
