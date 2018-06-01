package top.zbeboy.isy.web.vo.graduate.design.release

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-01-14 .
 **/
open class GraduationDesignReleaseAddVo {
    var graduationDesignReleaseId: String? = null
    @NotNull
    @Size(max = 100)
    var graduationDesignTitle: String? = null
    @NotNull
    var startTime: String? = null
    @NotNull
    var endTime: String? = null
    @NotNull
    var fillTeacherTime: String? = null
    var schoolId: Int? = null
    var collegeId: Int? = null
    @NotNull
    @Min(1)
    var departmentId: Int? = null
    @NotNull
    var grade: String? = null
    var graduationDesignIsDel: Byte? = null
    @NotNull
    @Min(1)
    var scienceId: Int? = null
    var files: String? = null
}