package top.zbeboy.isy.web.bean.system.mailbox;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.SystemMailbox;

/**
 * Created by lenovo on 2016-09-17.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class SystemMailboxBean extends SystemMailbox {
    private String sendTimeNew;
}
