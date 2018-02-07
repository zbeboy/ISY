package top.zbeboy.isy.web.vo.graduate.design.reorder

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-02-07 .
 **/
open class DefenseOrderVo {
    @NotNull
    @Size(max = 64)
    var defenseOrderId: String? = null
    @NotNull
    @Size(max = 64)
    var defenseGroupId: String? = null
    @NotNull
    @Size(max = 64)
    var graduationDesignReleaseId: String? = null
    var defenseStatus: Int = 0
    var grade: Double = 0.toDouble()
    var scoreTypeId: Int = 0
    var defenseQuestion: String? = null
}