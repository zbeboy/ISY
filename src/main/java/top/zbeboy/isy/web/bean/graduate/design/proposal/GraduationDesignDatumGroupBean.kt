package top.zbeboy.isy.web.bean.graduate.design.proposal

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup

/**
 * Created by zbeboy 2018-01-29 .
 **/
class GraduationDesignDatumGroupBean : GraduationDesignDatumGroup() {
    var size: String? = null
    var originalFileName: String? = null
    var newName: String? = null
    var relativePath: String? = null
    var ext: String? = null
    var uploadTimeStr: String? = null
    var graduationDesignReleaseId: String? = null
}