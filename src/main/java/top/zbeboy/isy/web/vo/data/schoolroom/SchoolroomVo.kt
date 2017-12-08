package top.zbeboy.isy.web.vo.data.schoolroom

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-08 .
 **/
open class SchoolroomVo {
    var schoolroomId: Int? = null
    @NotNull
    @Size(max = 10)
    var buildingCode: String? = null
    var schoolroomIsDel: Byte? = null
    @NotNull
    @Min(1)
    var buildingId: Int? = null
}