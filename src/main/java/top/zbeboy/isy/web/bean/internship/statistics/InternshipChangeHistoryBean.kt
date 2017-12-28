package top.zbeboy.isy.web.bean.internship.statistics

import top.zbeboy.isy.domain.tables.pojos.InternshipChangeHistory

/**
 * Created by zbeboy 2017-12-28 .
 **/
class InternshipChangeHistoryBean : InternshipChangeHistory() {
    var internshipTitle: String? = null
    var realName: String? = null
    var studentNumber: String? = null
    var organizeName: String? = null
    var applyTimeStr: String? = null
    var changeFillStartTimeStr: String? = null
    var changeFillEndTimeStr: String? = null
}