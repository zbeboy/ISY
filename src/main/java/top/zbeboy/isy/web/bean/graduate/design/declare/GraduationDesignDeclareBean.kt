package top.zbeboy.isy.web.bean.graduate.design.declare

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclare

/**
 * Created by zbeboy 2018-01-26 .
 **/
class GraduationDesignDeclareBean : GraduationDesignDeclare() {
    var staffName: String? = null
    var academicTitleName: String? = null
    var studentName: String? = null
    var studentNumber: String? = null
    var organizeName: String? = null
    var subjectTypeName: String? = null
    var originTypeName: String? = null
    var presubjectTitle: String? = null
    var staffId: Int = 0
    var studentId: Int = 0
    var graduationDesignReleaseId: String? = null
    var publicLevel: Int = 0
    var scoreTypeName: String? = null
    var defenseOrderId: String? = null
}