package top.zbeboy.isy.web.bean.graduate.design.replan

import top.zbeboy.isy.domain.tables.pojos.DefenseGroup

/**
 * Created by zbeboy 2018-02-06 .
 **/
class DefenseGroupBean : DefenseGroup() {
    var buildingId: Int? = null
    var buildingName: String? = null
    var buildingCode: String? = null
    var staffName: String? = null
    var studentName: String? = null
    // 压缩组员信息
    var memberName: List<String>? = null
}