package top.zbeboy.isy.web.vo.graduate.design.subject;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/6/14.
 */
@Data
public class GraduationDesignDeclareUpdateVo {
    private Integer subjectTypeId;
    private Integer originTypeId;
    private Byte isNewSubject;
    private Byte isNewTeacherMake;
    private Byte isNewSubjectMake;
    private Byte isOldSubjectChange;
    private Integer oldSubjectUsesTimes;
    private String planPeriod;
    private String assistantTeacher;
    private String assistantTeacherAcademic;
    private String assistantTeacherNumber;
    private Integer guideTimes;
    private Integer guidePeoples;
    private Byte isOkApply;
    @NotNull
    private String graduationDesignPresubjectId;
    @NotNull
    @Size(max = 64)
    private String graduationDesignReleaseId;
    @NotNull
    private Integer staffId;
}