package top.zbeboy.isy.web.vo.data.nation

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-12 .
 **/
open class NationVo {
    var nationId: Int? = null
    @NotNull
    @Size(max = 30)
    var nationName: String? = null
}