package top.zbeboy.isy.web.bean.internship.apply

import top.zbeboy.isy.domain.tables.pojos.InternshipApply
import top.zbeboy.isy.domain.tables.pojos.Science
import java.sql.Timestamp

/**
 * Created by zbeboy 2017-12-23 .
 **/
class InternshipApplyBean : InternshipApply() {
    var internshipTitle: String? = null
    var releaseTime: Timestamp? = null
    var username: String? = null
    var allowGrade: String? = null
    var teacherDistributionStartTime: Timestamp? = null
    var teacherDistributionEndTime: Timestamp? = null
    var startTime: Timestamp? = null
    var endTime: Timestamp? = null
    var internshipReleaseIsDel: Byte? = null
    var departmentId: Int? = null
    var internshipTypeId: Int? = null
    var sciences: List<Science>? = null
    var teacherDistributionStartTimeStr: String? = null
    var teacherDistributionEndTimeStr: String? = null
    var startTimeStr: String? = null
    var endTimeStr: String? = null
    var releaseTimeStr: String? = null
    var publisher: String? = null
    var departmentName: String? = null
    var schoolName: String? = null
    var collegeName: String? = null
    var internshipTypeName: String? = null
    var schoolId: Int = 0
    var collegeId: Int = 0
    var fileId: String? = null
    var originalFileName: String? = null
    var ext: String? = null

    // 用于实习显示指导教师用
    var schoolGuidanceTeacher: String? = null
    var schoolGuidanceTeacherTel: String? = null
}