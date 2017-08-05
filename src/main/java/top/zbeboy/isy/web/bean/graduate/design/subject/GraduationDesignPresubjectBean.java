package top.zbeboy.isy.web.bean.graduate.design.subject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPresubject;

/**
 * Created by zbeboy on 2017/6/5.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignPresubjectBean extends GraduationDesignPresubject {
    private String updateTimeStr;
    private String realName;
    private String studentNumber;
    private String organizeName;
    private int staffId;
    private int subjectTypeId;
    private int originTypeId;
    private Byte isNewSubject;
    private Byte isNewTeacherMake;
    private Byte isNewSubjectMake;
    private Byte isOldSubjectChange;
    private Integer oldSubjectUsesTimes;
    private String planPeriod;
    private String assistantTeacher;
    private String assistantTeacherAcademic;
    private String  assistantTeacherNumber;
    private int guideTimes;
    private int guidePeoples;
    private Byte isOkApply;
}
