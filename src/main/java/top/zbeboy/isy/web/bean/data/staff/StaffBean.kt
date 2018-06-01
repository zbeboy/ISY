package top.zbeboy.isy.web.bean.data.staff

import top.zbeboy.isy.domain.tables.pojos.Staff
import java.sql.Date
import java.sql.Timestamp

/**
 * Created by zbeboy 2017-11-19 .
 **/
class StaffBean : Staff() {
    var schoolId: Int? = null
    var schoolName: String? = null
    var schoolIsDel: Byte? = null

    var collegeId: Int? = null
    var collegeName: String? = null
    var collegeIsDel: Byte? = null

    var departmentName: String? = null
    var departmentIsDel: Byte? = null

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

    var academicTitleName: String? = null

    var idCard: String? = null

    // 用于checkbox 选中使用
    var checked: Boolean = false
}