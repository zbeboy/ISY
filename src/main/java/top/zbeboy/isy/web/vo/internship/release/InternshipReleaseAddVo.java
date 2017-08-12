package top.zbeboy.isy.web.vo.internship.release;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-11-12.
 */
@Data
public class InternshipReleaseAddVo {
    private String internshipReleaseId;
    @NotNull
    @Size(max = 100)
    private String releaseTitle;
    @NotNull
    @Min(1)
    private Integer internshipTypeId;
    @NotNull
    private String teacherDistributionTime;
    @NotNull
    private String time;
    private Integer schoolId;
    private Integer collegeId;
    @NotNull
    @Min(1)
    private Integer departmentId;
    @NotNull
    private String grade;
    @NotNull
    private String scienceId;
    private Byte internshipReleaseIsDel;
    private String files;
}
