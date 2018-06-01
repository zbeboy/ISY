package top.zbeboy.isy.web.vo.data.school

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-01 .
 **/
open class SchoolVo {
    var schoolId: Int? = null
    @NotNull
    @Size(max = 200)
    var schoolName: String? = null
    var schoolIsDel: Byte? = null
}