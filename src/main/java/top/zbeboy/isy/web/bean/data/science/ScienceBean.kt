package top.zbeboy.isy.web.bean.data.science

import top.zbeboy.isy.domain.tables.pojos.Science

/**
 * Created by zbeboy 2017-12-03 .
 **/
class ScienceBean: Science() {
    var schoolName: String? = null
    var collegeName: String? = null
    var departmentName: String? = null
    var schoolId: Int = 0
    var collegeId: Int = 0
}