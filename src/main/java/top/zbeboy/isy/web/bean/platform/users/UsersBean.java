package top.zbeboy.isy.web.bean.platform.users;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.Users;

/**
 * Created by lenovo on 2016-10-06.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class UsersBean extends Users {
    private String usersTypeName;
    private String roleName;
}
