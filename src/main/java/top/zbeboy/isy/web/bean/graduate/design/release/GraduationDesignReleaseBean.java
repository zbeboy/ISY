package top.zbeboy.isy.web.bean.graduate.design.release;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;

/**
 * Created by zbeboy on 2017/5/5.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignReleaseBean extends GraduationDesignRelease {
    private String realName;
    private String departmentName;
    private String schoolName;
    private String collegeName;
    private String scienceName;
    private int schoolId;
    private int collegeId;
    private String fillTeacherStartTimeStr;
    private String fillTeacherEndTimeStr;
    private String startTimeStr;
    private String endTimeStr;
    private String releaseTimeStr;
}
