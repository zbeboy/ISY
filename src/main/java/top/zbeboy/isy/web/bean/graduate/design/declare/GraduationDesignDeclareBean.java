package top.zbeboy.isy.web.bean.graduate.design.declare;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclare;

/**
 * Created by zbeboy on 2017/6/8.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignDeclareBean extends GraduationDesignDeclare {
    private String staffName;
    private String academicTitleName;
    private String studentName;
    private String studentNumber;
    private String organizeName;
    private String subjectTypeName;
    private String originTypeName;
    private String presubjectTitle;
    private int staffId;
    private int studentId;
    private String graduationDesignReleaseId;
    private int publicLevel;
    private String scoreTypeName;
    private String defenseOrderId;
}
