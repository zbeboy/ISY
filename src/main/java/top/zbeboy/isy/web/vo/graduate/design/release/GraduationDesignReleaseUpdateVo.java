package top.zbeboy.isy.web.vo.graduate.design.release;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2017-05-06.
 */
@Data
public class GraduationDesignReleaseUpdateVo {
    @NotNull
    @Size(max = 64)
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
    private Byte graduationDesignIsDel;
    private String files;
}
