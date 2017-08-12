package top.zbeboy.isy.web.vo.data.college;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016/9/22.
 */
@Data
public class CollegeVo {
    private Integer collegeId;
    @NotNull
    @Size(max = 200)
    private String collegeName;
    private Byte collegeIsDel;
    @NotNull
    @Min(1)
    private Integer schoolId;
    @NotNull
    @Size(max = 500)
    private String  collegeAddress;
    @NotNull
    @Size(max = 20)
    private String  collegeCode;
}
