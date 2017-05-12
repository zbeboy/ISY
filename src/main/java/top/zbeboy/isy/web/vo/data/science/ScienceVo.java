package top.zbeboy.isy.web.vo.data.science;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-09-24.
 */
@Data
public class ScienceVo {
    private Integer scienceId;
    @NotNull
    @Size(max = 200)
    private String scienceName;
    private Byte scienceIsDel;
    @NotNull
    @Min(1)
    private Integer departmentId;
    @NotNull
    @Size(max = 20)
    private String  scienceCode;
}
