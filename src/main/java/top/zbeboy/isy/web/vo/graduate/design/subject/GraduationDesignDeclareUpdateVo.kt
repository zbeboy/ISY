package top.zbeboy.isy.web.vo.graduate.design.subject

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by zbeboy 2018-01-26 .
 **/
open class GraduationDesignDeclareUpdateVo {
    var subjectTypeId: Int? = null
    var originTypeId: Int? = null
    var isNewSubject: Byte? = null
    var isNewTeacherMake: Byte? = null
    var isNewSubjectMake: Byte? = null
    var isOldSubjectChange: Byte? = null
    var oldSubjectUsesTimes: Int? = null
    var planPeriod: String? = null
    var assistantTeacher: String? = null
    var assistantTeacherAcademic: String? = null
    var assistantTeacherNumber: String? = null
    var guideTimes: Int? = null
    var guidePeoples: Int? = null
    var isOkApply: Byte? = null
    @NotNull
    var graduationDesignPresubjectId: String? = null
    @NotNull
    @Size(max = 64)
    var graduationDesignReleaseId: String? = null
    @NotNull
    var staffId: Int? = null
}