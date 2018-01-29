package top.zbeboy.isy.web.bean.graduate.design.proposal

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatum

/**
 * Created by zbeboy 2018-01-29 .
 **/
class GraduationDesignDatumBean : GraduationDesignDatum() {
    var size: String? = null
    var originalFileName: String? = null
    var newName: String? = null
    var relativePath: String? = null
    var ext: String? = null
    var updateTimeStr: String? = null
    var graduationDesignReleaseId: String? = null
    var graduationDesignDatumTypeName: String? = null
    var realName: String? = null
    var studentNumber: String? = null
    var staffId: Int = 0
    var studentId: Int = 0
    var organizeName: String? = null
}