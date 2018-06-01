package top.zbeboy.isy.web.vo.register.reset

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

/**
 * Created by zbeboy 2017-11-29 .
 **/
open class ResetVo {

    @NotNull
    @Pattern(regexp = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$")
    var email: String? = null

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]\\w{5,17}$")
    var password: String? = null

    @NotNull
    var confirmPassword: String? = null
}