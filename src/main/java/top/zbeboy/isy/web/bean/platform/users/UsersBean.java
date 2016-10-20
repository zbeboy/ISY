package top.zbeboy.isy.web.bean.platform.users;

import top.zbeboy.isy.domain.tables.pojos.Users;

/**
 * Created by lenovo on 2016-10-06.
 */
public class UsersBean extends Users {
    private String usersTypeName;
    private String roleName;

    public String getUsersTypeName() {
        return usersTypeName;
    }

    public void setUsersTypeName(String usersTypeName) {
        this.usersTypeName = usersTypeName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
