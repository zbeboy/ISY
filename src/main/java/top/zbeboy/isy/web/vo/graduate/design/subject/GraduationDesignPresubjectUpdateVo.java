package top.zbeboy.isy.web.vo.graduate.design.subject;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/6/5.
 */
@Data
public class GraduationDesignPresubjectUpdateVo {
    @NotNull
    @Size(max = 64)
    private String graduationDesignPresubjectId;
    @NotNull
    @Size(max = 64)
    private String graduationDesignReleaseId;
    @NotNull
    @Size(max = 100)
    private String presubjectTitle;
    @NotNull
    @Size(max = 65535)
    private String presubjectPlan;
    @NotNull
    private Integer publicLevel;
}
