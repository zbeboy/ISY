package top.zbeboy.isy.web.bean.data.department

import top.zbeboy.isy.domain.tables.pojos.Department

/**
 * Created by zbeboy 2017-12-02 .
 **/
class DepartmentBean : Department() {
    var schoolName: String? = null
    var collegeName: String? = null
    var schoolId: Int = 0
}