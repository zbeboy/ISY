package top.zbeboy.isy.web.bean.graduate.design.replan

import top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember

/**
 * Created by zbeboy 2018-02-06 .
 **/
class DefenseGroupMemberBean : DefenseGroupMember() {
    var studentNumber: String? = null
    var studentName: String? = null
    var studentMobile: String? = null
    var subject: String? = null
    var staffName: String? = null
    var studentId: Int = 0
}