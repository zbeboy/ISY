package top.zbeboy.isy.web.vo.internship.release

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-18 .
 **/
open class InternshipReleaseUpdateVo {
    @NotNull
    @Size(max = 64)
    var internshipReleaseId: String? = null
    @NotNull
    @Size(max = 100)
    var releaseTitle: String? = null
    @NotNull
    var teacherDistributionTime: String? = null
    @NotNull
    var time: String? = null
    var internshipReleaseIsDel: Byte? = null
    var files: String? = null
}