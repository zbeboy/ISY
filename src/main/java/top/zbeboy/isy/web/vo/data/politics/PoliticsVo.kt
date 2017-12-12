package top.zbeboy.isy.web.vo.data.politics

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-12 .
 **/
open class PoliticsVo {
    var politicalLandscapeId: Int? = null
    @NotNull
    @Size(max = 30)
    var politicalLandscapeName: String? = null
}