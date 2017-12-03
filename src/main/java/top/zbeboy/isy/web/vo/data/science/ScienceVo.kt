package top.zbeboy.isy.web.vo.data.science

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-03 .
 **/
open class ScienceVo {
    var scienceId: Int? = null
    @NotNull
    @Size(max = 200)
    var scienceName: String? = null
    var scienceIsDel: Byte? = null
    @NotNull
    @Min(1)
    var departmentId: Int? = null
    @NotNull
    @Size(max = 20)
    var scienceCode: String? = null
}