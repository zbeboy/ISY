package top.zbeboy.isy.web.vo.graduate.design.replan

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-02-06 .
 **/
open class DefenseGroupMemberAddVo {
    @NotNull
    @Size(max = 64)
    var graduationDesignTeacherId: String? = null
    @NotNull
    @Size(max = 64)
    var defenseGroupId: String? = null
    @Size(max = 100)
    var note: String? = null
    @NotNull
    @Size(max = 64)
    var graduationDesignReleaseId: String? = null
}