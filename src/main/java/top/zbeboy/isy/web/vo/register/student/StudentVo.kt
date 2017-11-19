package top.zbeboy.isy.web.vo.register.student

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-11-19 .
 **/
open class StudentVo {
    @NotNull
    @Min(1)
    val school: Int? = null
    val schoolName: String? = null

    @NotNull
    @Min(1)
    val college: Int? = null
    val collegeName: String? = null

    @NotNull
    @Min(1)
    val department: Int? = null
    val departmentName: String? = null

    @NotNull
    @Min(1)
    val science: Int? = null
    val scienceName: String? = null

    @NotNull
    val grade: String? = null

    @NotNull
    @Min(1)
    val organize: Int? = null
    val organizeName: String? = null

    @NotNull
    @Pattern(regexp = "^[\\d]{13,}$")
    val studentNumber: String? = null

    @NotNull
    @Pattern(regexp = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$")
    val email: String? = null

    @NotNull
    @Pattern(regexp = "^1[0-9]{10}")
    val mobile: String? = null

    @NotNull
    @Size(max = 30)
    val realName: String? = null

    @NotNull
    val phoneVerifyCode: String? = null

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]\\w{5,17}$")
    val password: String? = null

    @NotNull
    val confirmPassword: String? = null
}