package top.zbeboy.isy.web.vo.graduate.design.replan;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/7/10.
 */
@Data
public class GraduationDesignReplanUpdateVo {
    @NotNull
    @Size(max = 64)
    private String defenseArrangementId;
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
