package top.zbeboy.isy.web.vo.internship.release;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2016/11/18.
 */
@Data
public class InternshipReleaseUpdateVo {
    @NotNull
    @Size(max = 64)
    private String internshipReleaseId;
    @NotNull
    @Size(max = 100)
    private String releaseTitle;
    @NotNull
    private String teacherDistributionTime;
    @NotNull
    private String time;
    private Byte internshipReleaseIsDel;
    private String files;
}
