package top.zbeboy.isy.web.bean.system.message;

import top.zbeboy.isy.domain.tables.pojos.SystemMessage;

/**
 * Created by zbeboy on 2016/12/27.
 */
public class SystemMessageBean extends SystemMessage {
    private String messageDateStr;

    /*
    一般用于发件人姓名
     */
    private String realName;

    public String getMessageDateStr() {
        return messageDateStr;
    }

    public void setMessageDateStr(String messageDateStr) {
        this.messageDateStr = messageDateStr;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
