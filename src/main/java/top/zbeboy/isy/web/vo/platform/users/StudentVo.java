package top.zbeboy.isy.web.vo.platform.users;


import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-10-31.
 */
@Data
public class StudentVo {
    @NotNull
    @Pattern(regexp = "^[\\d]{13,}$")
    private String studentNumber;
    private String birthday;
    private String sex;
    private String idCard;
    private String familyResidence;
    private Integer politicalLandscapeId;
    private Integer nationId;
    private String dormitoryNumber;
    private String parentName;
    private String parentContactPhone;
    private String placeOrigin;
    @NotNull
    private String username;
    @NotNull
    @Size(max = 30)
    private String realName;
    private String avatar;
}
