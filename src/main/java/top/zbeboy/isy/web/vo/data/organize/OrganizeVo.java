package top.zbeboy.isy.web.vo.data.organize;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-09-25.
 */
@Data
public class OrganizeVo {
    private Integer organizeId;
    @NotNull
    @Size(max = 200)
    private String organizeName;
    private Byte organizeIsDel;
    @NotNull
    @Min(1)
    private Integer scienceId;
    @NotNull
    @Size(max = 5)
    private String grade;

    private Integer schoolId;
    private String schoolName;
    private Integer collegeId;
    private String collegeName;
    private Integer departmentId;
    private String departmentName;
    private String scienceName;
}
