package top.zbeboy.isy.web.vo.data.building

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-07 .
 **/
open class BuildingVo {
    var buildingId: Int? = null
    @NotNull
    @Size(max = 200)
    var buildingName: String? = null
    var buildingIsDel: Byte? = null
    @NotNull
    @Min(1)
    var collegeId: Int? = null
}