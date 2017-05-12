package top.zbeboy.isy.web.bean.system.application;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.Application;

/**
 * Created by lenovo on 2016-10-02.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ApplicationBean extends Application{
    private String applicationTypeName;
    private String applicationPidName;
}
