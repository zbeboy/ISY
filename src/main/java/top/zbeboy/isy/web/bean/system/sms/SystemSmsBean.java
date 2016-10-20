package top.zbeboy.isy.web.bean.system.sms;

import top.zbeboy.isy.domain.tables.pojos.SystemSms;

/**
 * Created by lenovo on 2016-09-17.
 */
public class SystemSmsBean extends SystemSms {
    private String sendTimeNew;

    public String getSendTimeNew() {
        return sendTimeNew;
    }

    public void setSendTimeNew(String sendTimeNew) {
        this.sendTimeNew = sendTimeNew;
    }
}
