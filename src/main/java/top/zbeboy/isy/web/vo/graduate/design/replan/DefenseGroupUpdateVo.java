package top.zbeboy.isy.web.vo.graduate.design.replan;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/7/12.
 */
@Data
public class DefenseGroupUpdateVo {
    @NotNull
    @Size(max = 64)
    private String defenseGroupId;
    @NotNull
    @Size(max = 20)
    private String defenseGroupName;
    @NotNull
    private int schoolroomId;
    @Size(max = 100)
    private String note;
    @NotNull
    @Size(max = 64)
    private String  graduationDesignReleaseId;
}
