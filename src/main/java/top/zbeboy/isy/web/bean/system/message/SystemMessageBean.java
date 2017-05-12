package top.zbeboy.isy.web.bean.system.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.SystemMessage;

/**
 * Created by zbeboy on 2016/12/27.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class SystemMessageBean extends SystemMessage {
    private String messageDateStr;

    /*
    一般用于发件人姓名
     */
    private String realName;
}
