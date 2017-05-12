package top.zbeboy.isy.web.vo.data.academic;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/4/21.
 */
@Data
public class AcademicTitleVo {
    private Integer academicTitleId;
    @NotNull
    @Size(max = 30)
    private String  academicTitleName;
}
