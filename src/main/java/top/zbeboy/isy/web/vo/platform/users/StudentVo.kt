package top.zbeboy.isy.web.vo.platform.users

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-11-19 .
 **/
open class StudentVo {
    @NotNull
    @Pattern(regexp = "^[\\d]{13,}$")
    val studentNumber: String? = null
    val birthday: String? = null
    val sex: String? = null
    val idCard: String? = null
    val familyResidence: String? = null
    val politicalLandscapeId: Int? = null
    val nationId: Int? = null
    val dormitoryNumber: String? = null
    val parentName: String? = null
    val parentContactPhone: String? = null
    val placeOrigin: String? = null
    @NotNull
    val username: String? = null
    @NotNull
    @Size(max = 30)
    val realName: String? = null
    val avatar: String? = null
}