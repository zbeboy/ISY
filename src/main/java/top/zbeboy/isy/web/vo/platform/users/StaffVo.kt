package top.zbeboy.isy.web.vo.platform.users

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-11-19 .
 **/
open class StaffVo {
    @NotNull
    @Pattern(regexp = "^[\\d]{8,}$")
    val staffNumber: String? = null
    val birthday: String? = null
    val sex: String? = null
    val idCard: String? = null
    val familyResidence: String? = null
    val politicalLandscapeId: Int? = null
    val nationId: Int? = null
    val academicTitleId: Int? = null
    val post: String? = null
    val departmentId: Int? = null
    @NotNull
    val username: String? = null
    @NotNull
    @Size(max = 30)
    val realName: String? = null
    val avatar: String? = null
}