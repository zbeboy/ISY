package top.zbeboy.isy.web.bean.graduate.design.pharmtech

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTutor

/**
 * Created by zbeboy 2017-12-20 .
 **/
class GraduationDesignTutorBean : GraduationDesignTutor() {
    var realName: String? = null
    var mobile: String? = null
    var username: String? = null
    var organizeName: String? = null
    var studentNumber: String? = null
    var studentName: String? = null
    var staffName: String? = null
    var graduationDesignReleaseId: String? = null
    var staffId: Int = 0
}