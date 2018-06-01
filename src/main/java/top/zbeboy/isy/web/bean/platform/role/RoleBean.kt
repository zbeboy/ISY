package top.zbeboy.isy.web.bean.platform.role

import top.zbeboy.isy.domain.tables.pojos.Role

/**
 * Created by zbeboy 2017-11-16 .
 **/
class RoleBean : Role() {
    var collegeId: Int? = null
    var collegeName: String? = null
    var schoolId: Int? = null
    var schoolName: String? = null
    var applicationId: Int? = null
    var allowAgent: Byte? = null
}