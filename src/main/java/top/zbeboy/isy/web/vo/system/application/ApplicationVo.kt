package top.zbeboy.isy.web.vo.system.application

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-11-17 .
 **/
open class ApplicationVo {
    val applicationId: String? = null
    @NotNull
    @Size(max = 30)
    val applicationName: String? = null
    val applicationSort: Int? = null
    @NotNull
    val applicationPid: String? = null
    @NotNull
    @Size(max = 300)
    val applicationUrl: String? = null
    @NotNull
    @Size(max = 100)
    val applicationCode: String? = null
    @NotNull
    @Size(max = 100)
    val applicationEnName: String? = null
    @Size(max = 20)
    val icon: String? = null
    @Size(max = 300)
    val applicationDataUrlStartWith: String? = null
}