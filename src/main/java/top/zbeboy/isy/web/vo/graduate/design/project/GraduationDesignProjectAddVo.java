package top.zbeboy.isy.web.vo.graduate.design.project;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/6/2.
 */
@Data
public class GraduationDesignProjectAddVo {
    @NotNull
    @Size(max = 64)
    private String graduationDesignReleaseId;
    @NotNull
    @Size(max = 100)
    private String scheduling;
    @NotNull
    @Size(max = 100)
    private String supervisionTime;
    @NotNull
    @Size(max = 150)
    private String guideContent;
    @Size(max = 100)
    private String note;
    @NotNull
    private Integer schoolroomId;
}
