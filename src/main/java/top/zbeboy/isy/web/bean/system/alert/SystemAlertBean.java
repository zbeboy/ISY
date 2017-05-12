package top.zbeboy.isy.web.bean.system.alert;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.SystemAlert;

/**
 * Created by lenovo on 2016-12-24.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class SystemAlertBean extends SystemAlert {
    private String icon;
    private String alertDateStr;
}
