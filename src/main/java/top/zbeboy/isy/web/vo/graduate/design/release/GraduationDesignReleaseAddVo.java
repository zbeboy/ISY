package top.zbeboy.isy.web.vo.graduate.design.release;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/5/5.
 */
@Data
public class GraduationDesignReleaseAddVo {
    private String graduationDesignReleaseId;
    @NotNull
    @Size(max = 100)
    private String graduationDesignTitle;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
    @NotNull
    private String fillTeacherTime;
    private Integer schoolId;
    private Integer collegeId;
    @NotNull
    @Min(1)
    private Integer departmentId;
    @NotNull
    private String grade;
    private Byte graduationDesignIsDel;
    @NotNull
    @Min(1)
    private Integer scienceId;
    private String files;
}
