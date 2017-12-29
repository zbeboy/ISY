package top.zbeboy.isy.web.vo.internship.regulate

import java.sql.Date
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-29 .
 **/
open class InternshipRegulateVo {
    var internshipRegulateId: String? = null
    @NotNull
    @Size(max = 30)
    var studentName: String? = null
    @NotNull
    @Size(max = 20)
    var studentNumber: String? = null
    @NotNull
    @Size(max = 15)
    var studentTel: String? = null
    @NotNull
    @Size(max = 200)
    var internshipContent: String? = null
    @NotNull
    @Size(max = 200)
    var internshipProgress: String? = null
    @NotNull
    @Size(max = 20)
    var reportWay: String? = null
    @NotNull
    var reportDate: Date? = null
    var schoolGuidanceTeacher: String? = null
    var tliy = "无"
    @NotNull
    var studentId: Int? = null
    @NotNull
    @Size(max = 64)
    var internshipReleaseId: String? = null
    @NotNull
    var staffId: Int? = null
}