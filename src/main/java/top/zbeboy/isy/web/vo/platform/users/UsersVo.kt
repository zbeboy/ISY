package top.zbeboy.isy.web.vo.platform.users

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-11-19 .
 **/
open class UsersVo {
    @NotNull
    @Size(max = 200)
    var username: String? = null
    @NotNull
    @Size(max = 30)
    var realName: String? = null
    var avatar: String? = null
}