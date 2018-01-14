package top.zbeboy.isy.web.bean.graduate.design.release

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease

/**
 * Created by zbeboy 2018-01-14 .
 **/
class GraduationDesignReleaseBean: GraduationDesignRelease() {
    var realName: String? = null
    var departmentName: String? = null
    var schoolName: String? = null
    var collegeName: String? = null
    var scienceName: String? = null
    var schoolId: Int? = 0
    var collegeId: Int? = 0
    var fillTeacherStartTimeStr: String? = null
    var fillTeacherEndTimeStr: String? = null
    var startTimeStr: String? = null
    var endTimeStr: String? = null
    var releaseTimeStr: String? = null

    // 未填报学生数
    var studentNotFillCount: Int? = 0
    // 填报学生数
    var studentFillCount: Int? = 0
    // 同步时间
    var syncDate: String? = null
    var hasSyncDate: Boolean? = false
}