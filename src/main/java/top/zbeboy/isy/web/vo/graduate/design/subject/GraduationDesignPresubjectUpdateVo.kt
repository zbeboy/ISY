package top.zbeboy.isy.web.vo.graduate.design.subject

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-01-26 .
 **/
open class GraduationDesignPresubjectUpdateVo {
    @NotNull
    @Size(max = 64)
    var graduationDesignPresubjectId: String? = null
    @NotNull
    @Size(max = 64)
    var graduationDesignReleaseId: String? = null
    @NotNull
    @Size(max = 100)
    var presubjectTitle: String? = null
    @NotNull
    @Size(max = 65535)
    var presubjectPlan: String? = null
    @NotNull
    var publicLevel: Int? = null

    var staffId: Int = 0
}