package top.zbeboy.isy.web.vo.graduate.design.replan;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/7/17.
 */
@Data
public class DefenseGroupMemberAddVo {
    @NotNull
    @Size(max = 64)
    private String graduationDesignTeacherId;
    @NotNull
    @Size(max = 64)
    private String defenseGroupId;
    @Size(max = 100)
    private String note;
    @NotNull
    @Size(max = 64)
    private String graduationDesignReleaseId;
}
