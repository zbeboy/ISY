package top.zbeboy.isy.web.vo.data.department;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016/9/23.
 */
@Data
public class DepartmentVo {
    private Integer departmentId;
    @NotNull
    @Size(max = 200)
    private String departmentName;
    private Byte departmentIsDel;
    private Integer collegeId;
}
