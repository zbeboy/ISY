package top.zbeboy.isy.web.bean.system.mailbox;

import top.zbeboy.isy.domain.tables.pojos.SystemMailbox;

/**
 * Created by lenovo on 2016-09-17.
 */
public class SystemMailboxBean extends SystemMailbox {
    private String sendTimeNew;

    public String getSendTimeNew() {
        return sendTimeNew;
    }

    public void setSendTimeNew(String sendTimeNew) {
        this.sendTimeNew = sendTimeNew;
    }
}
