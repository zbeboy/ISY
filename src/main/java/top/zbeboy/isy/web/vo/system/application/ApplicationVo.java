package top.zbeboy.isy.web.vo.system.application;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-10-04.
 */
@Data
public class ApplicationVo {
    private String applicationId;
    @NotNull
    @Size(max = 30)
    private String applicationName;
    private Integer applicationSort;
    @NotNull
    private String applicationPid;
    @NotNull
    @Size(max = 300)
    private String applicationUrl;
    @NotNull
    @Size(max = 100)
    private String applicationCode;
    @NotNull
    @Size(max = 100)
    private String applicationEnName;
    @Size(max = 20)
    private String icon;
    @Size(max = 300)
    private String applicationDataUrlStartWith;
}
