package top.zbeboy.isy.web.vo.data.academic

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-12 .
 **/
open class AcademicTitleVo {
    var academicTitleId: Int? = null
    @NotNull
    @Size(max = 30)
    var academicTitleName: String? = null
}