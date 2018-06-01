package top.zbeboy.isy.web.vo.graduate.design.proposal

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-01-29 .
 **/
open class GraduationDesignProposalAddVo {
    @Size(max = 10)
    var version: String? = null
    @NotNull
    var graduationDesignDatumTypeId: Int? = null
    @NotNull
    @Size(max = 64)
    var graduationDesignReleaseId: String? = null
    var graduationDesignDatumId: String? = null
}