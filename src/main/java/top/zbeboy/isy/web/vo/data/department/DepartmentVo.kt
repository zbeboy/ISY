package top.zbeboy.isy.web.vo.data.department

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-02 .
 **/
open class DepartmentVo {
    var departmentId: Int? = null
    @NotNull
    @Size(max = 200)
    var departmentName: String? = null
    var departmentIsDel: Byte? = null
    @NotNull
    @Min(1)
    var collegeId: Int? = null
}