package top.zbeboy.isy.web.vo.register.staff;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-08-28.
 */
@Data
public class StaffVo {

    @NotNull
    @Min(1)
    private Integer school;
    private String schoolName;

    @NotNull
    @Min(1)
    private Integer college;
    private String collegeName;

    @NotNull
    @Min(1)
    private Integer department;
    private String departmentName;

    @NotNull
    @Pattern(regexp = "^[\\d]{8,}$")
    private String staffNumber;

    @NotNull
    @Pattern(regexp = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$")
    private String email;

    @NotNull
    @Pattern(regexp = "^1[0-9]{10}")
    private String mobile;

    @NotNull
    @Size(max = 30)
    private String realName;

    @NotNull
    private String phoneVerifyCode;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]\\w{5,17}$")
    private String password;

    @NotNull
    private String confirmPassword;
}
