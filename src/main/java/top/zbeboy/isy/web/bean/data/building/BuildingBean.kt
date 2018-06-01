package top.zbeboy.isy.web.bean.data.building

import top.zbeboy.isy.domain.tables.pojos.Building

/**
 * Created by zbeboy 2017-12-07 .
 **/
class BuildingBean: Building() {
    var schoolName: String? = null
    var collegeName: String? = null
    var schoolId: Int = 0
}