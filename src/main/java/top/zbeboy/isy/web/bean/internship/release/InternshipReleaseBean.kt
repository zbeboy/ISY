package top.zbeboy.isy.web.bean.internship.release

import top.zbeboy.isy.domain.tables.pojos.InternshipRelease
import top.zbeboy.isy.domain.tables.pojos.Science

/**
 * Created by zbeboy 2017-12-18 .
 **/
class InternshipReleaseBean: InternshipRelease() {
    var realName: String? = null
    var departmentName: String? = null
    var schoolName: String? = null
    var collegeName: String? = null
    var internshipTypeName: String? = null
    var sciences: List<Science>? = null
    var schoolId: Int? = 0
    var collegeId: Int? = 0
    var teacherDistributionStartTimeStr: String? = null
    var teacherDistributionEndTimeStr: String? = null
    var startTimeStr: String? = null
    var endTimeStr: String? = null
    var releaseTimeStr: String? = null

    // 实习审核 统计总数
    var waitTotalData: Int? = 0
    var passTotalData: Int? = 0
    var failTotalData: Int? = 0
    var basicApplyTotalData: Int? = 0
    var companyApplyTotalData: Int? = 0
    var basicFillTotalData: Int? = 0
    var companyFillTotalData: Int? = 0

    // 实习统计 统计总数
    var submittedTotalData: Int? = 0
    var unsubmittedTotalData: Int? = 0
}