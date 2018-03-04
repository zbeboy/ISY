package top.zbeboy.isy.elastic.pojo

import org.apache.commons.lang.math.NumberUtils
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import java.sql.Date

/**
 * Created by zbeboy 2017-11-19 .
 **/
@Document(indexName = "staff", type = "staff", shards = 1, replicas = 0, refreshInterval = "-1")
open class StaffElastic {
    @Id
    private var staffId: String? = null
    var staffNumber: String? = null
    var birthday: String? = null
    var sex: String? = null
    var idCard: String? = null
    var familyResidence: String? = null
    var politicalLandscapeId: Int? = null
    var politicalLandscapeName: String? = null
    var nationId: Int? = null
    var nationName: String? = null
    var academicTitleId: Int? = null
    var academicTitleName: String? = null
    var post: String? = null
    var schoolId: Int? = null
    var schoolName: String? = null
    var collegeId: Int? = null
    var collegeName: String? = null
    var departmentId: Int? = null
    var departmentName: String? = null
    var username: String? = null
    var enabled: Byte? = null
    var realName: String? = null
    var mobile: String? = null
    var avatar: String? = null
    var verifyMailbox: Byte? = null
    var langKey: String? = null
    var joinDate: Date? = null
    /**
     * 详见：ElasticBook
     */
    var authorities: Int? = null
    /**
     * 以空格分隔的角色名
     */
    var roleName: String? = null

    constructor()

    constructor(staffId: String?, staffNumber: String?, birthday: String?, sex: String?, idCard: String?, familyResidence: String?, politicalLandscapeId: Int?, politicalLandscapeName: String?, nationId: Int?, nationName: String?, academicTitleId: Int?, academicTitleName: String?, post: String?, schoolId: Int?, schoolName: String?, collegeId: Int?, collegeName: String?, departmentId: Int?, departmentName: String?, username: String?, enabled: Byte?, realName: String?, mobile: String?, avatar: String?, verifyMailbox: Byte?, langKey: String?, joinDate: Date?, authorities: Int?, roleName: String?) {
        this.staffId = staffId
        this.staffNumber = staffNumber
        this.birthday = birthday
        this.sex = sex
        this.idCard = idCard
        this.familyResidence = familyResidence
        this.politicalLandscapeId = politicalLandscapeId
        this.politicalLandscapeName = politicalLandscapeName
        this.nationId = nationId
        this.nationName = nationName
        this.academicTitleId = academicTitleId
        this.academicTitleName = academicTitleName
        this.post = post
        this.schoolId = schoolId
        this.schoolName = schoolName
        this.collegeId = collegeId
        this.collegeName = collegeName
        this.departmentId = departmentId
        this.departmentName = departmentName
        this.username = username
        this.enabled = enabled
        this.realName = realName
        this.mobile = mobile
        this.avatar = avatar
        this.verifyMailbox = verifyMailbox
        this.langKey = langKey
        this.joinDate = joinDate
        this.authorities = authorities
        this.roleName = roleName
    }

    fun getStaffId(): Int? {
        return NumberUtils.toInt(staffId)
    }

    fun setStaffId(staffId: Int?) {
        this.staffId = staffId!!.toString() + ""
    }
}