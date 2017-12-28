package top.zbeboy.isy.web.bean.internship.statistics

import top.zbeboy.isy.domain.tables.pojos.InternshipChangeCompanyHistory

/**
 * Created by zbeboy 2017-12-28 .
 **/
class InternshipChangeCompanyHistoryBean : InternshipChangeCompanyHistory() {
    var internshipTitle: String? = null
    var realName: String? = null
    var studentNumber: String? = null
    var organizeName: String? = null
    var changeTimeStr: String? = null
}