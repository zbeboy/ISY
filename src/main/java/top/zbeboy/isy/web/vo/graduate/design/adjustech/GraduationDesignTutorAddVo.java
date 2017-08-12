package top.zbeboy.isy.web.vo.graduate.design.adjustech;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/5/25.
 */
@Data
public class GraduationDesignTutorAddVo {
    @NotNull
    @Size(max = 64)
    private String graduationDesignReleaseId;
    @NotNull
    private String studentId;
    @NotNull
    @Size(max = 64)
    private String graduationDesignTeacherId;
}
