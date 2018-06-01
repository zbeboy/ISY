package top.zbeboy.isy.web.vo.graduate.design.release

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-01-14 .
 **/
open class GraduationDesignReleaseUpdateVo {
    @NotNull
    @Size(max = 64)
    var graduationDesignReleaseId: String? = null
    @NotNull
    @Size(max = 100)
    var graduationDesignTitle: String? = null
    @NotNull
    var startTime: String? = null
    @NotNull
    var endTime: String? = null
    @NotNull
    var fillTeacherTime: String? = null
    var graduationDesignIsDel: Byte? = null
    var files: String? = null
}