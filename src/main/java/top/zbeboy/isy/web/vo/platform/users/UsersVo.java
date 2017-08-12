package top.zbeboy.isy.web.vo.platform.users;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-11-02.
 */
@Data
public class UsersVo {
    @NotNull
    @Size(max = 200)
    private String username;
    @NotNull
    @Size(max = 30)
    private String realName;
    private String avatar;
}
