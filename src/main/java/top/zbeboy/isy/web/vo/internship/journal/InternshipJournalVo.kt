package top.zbeboy.isy.web.vo.internship.journal

import java.sql.Date
import java.sql.Timestamp
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-29 .
 **/
open class InternshipJournalVo {
    var internshipJournalId: String? = null
    @NotNull
    @Size(max = 30)
    var studentName: String? = null
    @NotNull
    @Size(max = 20)
    var studentNumber: String? = null
    @NotNull
    @Size(max = 200)
    var organize: String? = null
    @NotNull
    @Size(max = 30)
    var schoolGuidanceTeacher: String? = null
    @NotNull
    @Size(max = 200)
    var graduationPracticeCompanyName: String? = null
    @NotNull
    @Size(max = 65535)
    var internshipJournalContent: String? = null
    @NotNull
    @Size(max = 65535)
    var internshipJournalHtml: String? = null
    @NotNull
    var internshipJournalDate: Date? = null
    var createDate: Timestamp? = null
    @NotNull
    var studentId: Int? = null
    @NotNull
    var staffId: Int? = null
    @NotNull
    var internshipReleaseId: String? = null
    var internshipJournalWord: String? = null
    var isSeeStaff: Byte? = null
}