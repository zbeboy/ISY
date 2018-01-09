package top.zbeboy.isy.web.bean.data.student

import top.zbeboy.isy.domain.tables.pojos.Student
import java.sql.Date
import java.sql.Timestamp

/**
 * Created by zbeboy 2017-11-19 .
 **/
class StudentBean : Student() {
    var schoolId: Int? = null
    var schoolName: String? = null
    var schoolIsDel: Byte? = null

    var collegeId: Int? = null
    var collegeName: String? = null
    var collegeAddress: String? = null
    var collegeIsDel: Byte? = null

    var departmentId: Int? = null
    var departmentName: String? = null
    var departmentIsDel: Byte? = null

    var scienceId: Int? = null
    var scienceName: String? = null
    var scienceIsDel: Byte? = null

    var organizeName: String? = null
    var organizeIsDel: Byte? = null
    var grade: String? = null

    var password: String? = null
    var enabled: Byte? = null
    var usersTypeId: Int? = null
    var realName: String? = null
    var mobile: String? = null
    var avatar: String? = null
    var verifyMailbox: Byte? = null
    var mailboxVerifyCode: String? = null
    var passwordResetKey: String? = null
    var mailboxVerifyValid: Timestamp? = null
    var passwordResetKeyValid: Timestamp? = null
    var langKey: String? = null
    var joinDate: Date? = null

    var nationName: String? = null

    var politicalLandscapeName: String? = null

    var roleName: String? = null

    var idCard: String? = null

    // 楼号
    var ridgepole: String? = null
    // 宿舍号
    var dorm: String? = null
}