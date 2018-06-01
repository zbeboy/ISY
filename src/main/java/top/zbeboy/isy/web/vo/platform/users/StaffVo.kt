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
    var staffNumber: String? = null
    var birthday: String? = null
    var sex: String? = null
    var idCard: String? = null
    var familyResidence: String? = null
    var politicalLandscapeId: Int? = null
    var nationId: Int? = null
    var academicTitleId: Int? = null
    var post: String? = null
    var departmentId: Int? = null
    @NotNull
    var username: String? = null
    @NotNull
    @Size(max = 30)
    var realName: String? = null
    var avatar: String? = null
}