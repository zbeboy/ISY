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
    var applicationName: String? = null
    var applicationSort: Int? = null
    @NotNull
    var applicationPid: String? = null
    @NotNull
    @Size(max = 300)
    var applicationUrl: String? = null
    @NotNull
    @Size(max = 100)
    var applicationCode: String? = null
    @NotNull
    @Size(max = 100)
    var applicationEnName: String? = null
    @Size(max = 20)
    var icon: String? = null
    @Size(max = 300)
    var applicationDataUrlStartWith: String? = null
}