package top.zbeboy.isy.web.vo.data.college

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-01 .
 **/
open class CollegeVo {
    var collegeId: Int? = null
    @NotNull
    @Size(max = 200)
    var collegeName: String? = null
    var collegeIsDel: Byte? = null
    @NotNull
    @Min(1)
    var schoolId: Int? = null
    @NotNull
    @Size(max = 500)
    var collegeAddress: String? = null
    @NotNull
    @Size(max = 20)
    var collegeCode: String? = null
}