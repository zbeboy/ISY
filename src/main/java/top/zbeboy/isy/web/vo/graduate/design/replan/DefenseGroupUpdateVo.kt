package top.zbeboy.isy.web.vo.graduate.design.replan

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-02-06 .
 **/
open class DefenseGroupUpdateVo {
    @NotNull
    @Size(max = 64)
    var defenseGroupId: String? = null
    @NotNull
    @Size(max = 20)
    var defenseGroupName: String? = null
    @NotNull
    var schoolroomId: Int = 0
    @Size(max = 100)
    var note: String? = null
    @NotNull
    @Size(max = 64)
    var graduationDesignReleaseId: String? = null
}