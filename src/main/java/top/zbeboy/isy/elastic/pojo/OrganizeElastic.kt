package top.zbeboy.isy.elastic.pojo

import org.apache.commons.lang.math.NumberUtils
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

/**
 * Created by zbeboy 2017-12-01 .
 **/
@Document(indexName = "organize", type = "organize", shards = 1, replicas = 0, refreshInterval = "-1")
class OrganizeElastic {
    @Id
    private var organizeId: String? = null
    var organizeName: String? = null
    var organizeIsDel: Byte? = null
    var scienceId: Int? = null
    var grade: String? = null
    var schoolId: Int? = null
    var schoolName: String? = null
    var collegeId: Int? = null
    var collegeName: String? = null
    var departmentId: Int? = null
    var departmentName: String? = null
    var scienceName: String? = null

    fun getOrganizeId(): Int? {
        return NumberUtils.toInt(organizeId)
    }

    fun setOrganizeId(organizeId: Int?) {
        this.organizeId = organizeId!!.toString() + ""
    }
}