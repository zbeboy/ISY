package top.zbeboy.isy.web.vo.data.organize

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-03 .
 **/
open class OrganizeVo {
    var organizeId: Int? = null
    @NotNull
    @Size(max = 200)
    var organizeName: String? = null
    var organizeIsDel: Byte? = null
    @NotNull
    @Min(1)
    var scienceId: Int? = null
    @NotNull
    @Size(max = 5)
    var grade: String? = null
}