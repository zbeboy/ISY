package top.zbeboy.isy.web.vo.register.staff

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-11-19 .
 **/
open class StaffVo {
    @NotNull
    @Min(1)
    var school: Int? = null
    var schoolName: String? = null

    @NotNull
    @Min(1)
    var college: Int? = null
    var collegeName: String? = null

    @NotNull
    @Min(1)
    var department: Int? = null
    var departmentName: String? = null

    @NotNull
    @Pattern(regexp = "^[\\d]{8,}$")
    var staffNumber: String? = null

    @NotNull
    @Pattern(regexp = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$")
    var email: String? = null

    @NotNull
    @Pattern(regexp = "^1[0-9]{10}")
    var mobile: String? = null

    @NotNull
    @Size(max = 30)
    var realName: String? = null

    @NotNull
    var phoneVerifyCode: String? = null

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]\\w{5,17}$")
    var password: String? = null

    @NotNull
    var confirmPassword: String? = null
}