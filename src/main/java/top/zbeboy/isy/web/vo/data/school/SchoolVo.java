package top.zbeboy.isy.web.vo.data.school;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-09-19.
 */
@Data
public class SchoolVo {
    private Integer schoolId;
    @NotNull
    @Size(max = 200)
    private String schoolName;
    private Byte schoolIsDel;
}
