package top.zbeboy.isy.web.bean.system.log;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.SystemLog;

/**
 * Created by lenovo on 2016-09-15.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class SystemLogBean extends SystemLog {
    private String operatingTimeNew;
}
