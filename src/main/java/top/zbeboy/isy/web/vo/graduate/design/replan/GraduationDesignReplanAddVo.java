package top.zbeboy.isy.web.vo.graduate.design.replan;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2017-07-09.
 */
@Data
public class GraduationDesignReplanAddVo {
    @NotNull
    @Size(max = 64)
    private String graduationDesignReleaseId;
    @NotNull
    private String paperDate;
    @NotNull
    private String defenseDate;
    private String[] defenseStartTime;
    private String[] defenseEndTime;
    @NotNull
    private int intervalTime;
    private String defenseNote;
}
