package top.zbeboy.isy.web.bean.graduate.design.teacher

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher

/**
 * Created by zbeboy 2018-01-17 .
 **/
class GraduationDesignTeacherBean : GraduationDesignTeacher() {
    var realName: String? = null
    var staffNumber: String? = null
    var staffUsername: String? = null
    var staffMobile: String? = null
    var residueCount: Int = 0
    // 用于选中
    var selected: Boolean = false
    var defenseGroupId: String? = null
    var defenseGroupName: String? = null
    var leaderId: String? = null
    var note: String? = null
}