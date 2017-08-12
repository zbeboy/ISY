package top.zbeboy.isy.web.bean.system.mailbox;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.elastic.pojo.SystemMailboxElastic;

/**
 * Created by lenovo on 2016-09-17.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class SystemMailboxBean extends SystemMailboxElastic {
    private String sendTimeNew;
}
