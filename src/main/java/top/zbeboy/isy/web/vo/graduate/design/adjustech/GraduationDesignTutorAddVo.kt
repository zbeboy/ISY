package top.zbeboy.isy.web.vo.graduate.design.adjustech

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-01-20 .
 **/
open class GraduationDesignTutorAddVo {
    @NotNull
    @Size(max = 64)
    var graduationDesignReleaseId: String? = null
    @NotNull
    var studentId: String? = null
    @NotNull
    @Size(max = 64)
    var graduationDesignTeacherId: String? = null
}