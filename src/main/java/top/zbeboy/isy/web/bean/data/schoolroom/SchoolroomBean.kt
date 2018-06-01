package top.zbeboy.isy.web.bean.data.schoolroom

import top.zbeboy.isy.domain.tables.pojos.Schoolroom

/**
 * Created by zbeboy 2017-12-08 .
 **/
class SchoolroomBean: Schoolroom() {
    var schoolName: String? = null
    var collegeName: String? = null
    var buildingName: String? = null
    var schoolId: Int = 0
    var collegeId: Int = 0
}