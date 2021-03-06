package top.zbeboy.isy.web.vo.internship.apply

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2017-12-23 .
 **/
open class GraduationPracticeCollegeVo {
    var graduationPracticeCollegeId: String? = null
    @NotNull
    var studentId: Int? = null
    @NotNull
    var studentUsername: String? = null
    @NotNull
    @Size(max = 100)
    var internshipReleaseId: String? = null
    @NotNull
    @Size(max = 15)
    var studentName: String? = null
    @NotNull
    @Size(max = 50)
    var collegeClass: String? = null
    @NotNull
    @Size(max = 2)
    var studentSex: String? = null
    @NotNull
    @Size(max = 20)
    var studentNumber: String? = null
    @NotNull
    @Size(max = 15)
    var phoneNumber: String? = null
    @NotNull
    @Size(max = 100)
    var qqMailbox: String? = null
    @NotNull
    @Size(max = 20)
    var parentalContact: String? = null
    @NotNull
    var headmaster: String? = null
    var headmasterContact: String? = null
    @NotNull
    @Size(max = 200)
    var graduationPracticeCollegeName: String? = null
    @NotNull
    @Size(max = 500)
    var graduationPracticeCollegeAddress: String? = null
    @NotNull
    @Size(max = 10)
    var graduationPracticeCollegeContacts: String? = null
    @NotNull
    @Size(max = 20)
    var graduationPracticeCollegeTel: String? = null
    @NotNull
    var schoolGuidanceTeacher: String? = null
    var schoolGuidanceTeacherTel: String? = null
    @NotNull
    var startTime: String? = null
    @NotNull
    var endTime: String? = null
    var commitmentBook: Byte? = null
    var safetyResponsibilityBook: Byte? = null
    var practiceAgreement: Byte? = null
    var internshipApplication: Byte? = null
    var practiceReceiving: Byte? = null
    var securityEducationAgreement: Byte? = null
    var parentalConsent: Byte? = null
}