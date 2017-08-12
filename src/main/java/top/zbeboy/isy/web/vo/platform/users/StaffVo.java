package top.zbeboy.isy.web.vo.platform.users;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-11-02.
 */
@Data
public class StaffVo {
    @NotNull
    @Pattern(regexp = "^[\\d]{8,}$")
    private String staffNumber;
    private String birthday;
    private String sex;
    private String idCard;
    private String familyResidence;
    private Integer politicalLandscapeId;
    private Integer nationId;
    private Integer academicTitleId;
    private String post;
    private Integer departmentId;
    @NotNull
    private String username;
    @NotNull
    @Size(max = 30)
    private String realName;
    private String avatar;
}
