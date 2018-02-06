package top.zbeboy.isy.web.vo.graduate.design.replan

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-02-06 .
 **/
open class GraduationDesignReplanAddVo {
    @NotNull
    @Size(max = 64)
    var graduationDesignReleaseId: String? = null
    @NotNull
    var paperTime: String? = null
    @NotNull
    var defenseTime: String? = null
    var dayDefenseStartTime: Array<String>? = null
    var dayDefenseEndTime: Array<String>? = null
    @NotNull
    var intervalTime: Int = 0
    var defenseNote: String? = null
}