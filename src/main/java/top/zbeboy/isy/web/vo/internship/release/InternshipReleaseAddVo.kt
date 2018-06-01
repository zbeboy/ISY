package top.zbeboy.isy.web.vo.internship.release

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-18 .
 **/
open class InternshipReleaseAddVo {
    var internshipReleaseId: String? = null
    @NotNull
    @Size(max = 100)
    var releaseTitle: String? = null
    @NotNull
    @Min(1)
    var internshipTypeId: Int? = null
    @NotNull
    var teacherDistributionTime: String? = null
    @NotNull
    var time: String? = null
    var schoolId: Int? = null
    var collegeId: Int? = null
    @NotNull
    @Min(1)
    var departmentId: Int? = null
    @NotNull
    var grade: String? = null
    @NotNull
    var scienceId: String? = null
    var internshipReleaseIsDel: Byte? = null
    var files: String? = null
}