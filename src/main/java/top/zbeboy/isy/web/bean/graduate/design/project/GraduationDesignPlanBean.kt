package top.zbeboy.isy.web.bean.graduate.design.project

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan

/**
 * Created by zbeboy 2017-12-20 .
 **/
class GraduationDesignPlanBean: GraduationDesignPlan() {
    var buildingName: String? = null
    var buildingCode: String? = null
    var buildingId: Int = 0
}