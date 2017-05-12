package top.zbeboy.isy.web.bean.system.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.SystemSms;

/**
 * Created by lenovo on 2016-09-17.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class SystemSmsBean extends SystemSms {
    private String sendTimeNew;
}
