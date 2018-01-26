package top.zbeboy.isy.web.bean.graduate.design.subject

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPresubject

/**
 * Created by zbeboy 2018-01-26 .
 **/
class GraduationDesignPresubjectBean : GraduationDesignPresubject() {
    var updateTimeStr: String? = null
    var realName: String? = null
    var studentNumber: String? = null
    var organizeName: String? = null
    var staffId: Int = 0
    var subjectTypeId: Int = 0
    var originTypeId: Int = 0
    var isNewSubject: Byte? = null
    var isNewTeacherMake: Byte? = null
    var isNewSubjectMake: Byte? = null
    var isOldSubjectChange: Byte? = null
    var oldSubjectUsesTimes: Int? = null
    var planPeriod: String? = null
    var assistantTeacher: String? = null
    var assistantTeacherAcademic: String? = null
    var assistantTeacherNumber: String? = null
    var guideTimes: Int = 0
    var guidePeoples: Int = 0
    var isOkApply: Byte? = null
}