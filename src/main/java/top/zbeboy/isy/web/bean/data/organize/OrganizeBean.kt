package top.zbeboy.isy.web.bean.data.organize

import top.zbeboy.isy.domain.tables.pojos.Organize

/**
 * Created by zbeboy 2017-12-03 .
 **/
class OrganizeBean: Organize() {
    var schoolName: String? = null
    var collegeName: String? = null
    var departmentName: String? = null
    var scienceName: String? = null
    var schoolId: Int = 0
    var collegeId: Int = 0
    var departmentId: Int = 0
}