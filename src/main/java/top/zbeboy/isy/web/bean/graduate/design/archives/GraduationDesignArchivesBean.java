package top.zbeboy.isy.web.bean.graduate.design.archives;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignArchives;

/**
 * Created by lenovo on 2017-08-06.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignArchivesBean extends GraduationDesignArchives {
    private String graduationDesignReleaseId;
    private String collegeName;
    private String collegeCode;
    private String scienceName;
    private String scienceCode;
    private String graduationDate;
    private String staffName;
    private String staffNumber;
    private String academicTitleName;
    private String assistantTeacher;
    private String assistantTeacherAcademic;
    private String assistantTeacherNumber;
    private String presubjectTitle;
    private String subjectTypeName;
    private String originTypeName;
    private String studentName;
    private String studentNumber;
    private String scoreTypeName;
}
