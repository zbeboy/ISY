package top.zbeboy.isy.web.vo.graduate.design.adjustech;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/5/23.
 */
@Data
public class GraduationDesignTutorUpdateVo {
    @NotNull
    @Size(max = 64)
    private String graduationDesignReleaseId;
    @NotNull
    private String graduationDesignTutorId;
    @NotNull
    @Size(max = 64)
    private String graduationDesignTeacherId;
    @NotNull
    private int saveType;
}
