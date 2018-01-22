package top.zbeboy.isy.web.vo.graduate.design.project

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-01-22 .
 **/
open class GraduationDesignProjectAddVo {
    @NotNull
    @Size(max = 64)
    var graduationDesignReleaseId: String? = null
    @NotNull
    @Size(max = 100)
    var scheduling: String? = null
    @NotNull
    @Size(max = 100)
    var supervisionTime: String? = null
    @NotNull
    @Size(max = 150)
    var guideContent: String? = null
    @Size(max = 100)
    var note: String? = null
    @NotNull
    var schoolroomId: Int? = null
}