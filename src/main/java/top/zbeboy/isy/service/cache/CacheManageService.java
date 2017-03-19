package top.zbeboy.isy.service.cache;

import top.zbeboy.isy.domain.tables.pojos.Application;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.pojos.RoleApplication;
import top.zbeboy.isy.domain.tables.pojos.UsersType;

import java.util.List;

/**
 * Created by lenovo on 2017-03-11.
 */
public interface CacheManageService {

    /**
     * 根据用户类型查询id
     *
     * @param usersTypeName 用户类型名
     * @return 用户类型
     */
    UsersType findByUsersTypeName(String usersTypeName);

    /**
     * 根据用户id查询类型
     *
     * @param usersTypeId 用户类型id
     * @return 用户类型
     */
    UsersType findByUsersTypeId(int usersTypeId);

    /**
     * 通过角色id查询出应用id并生成菜单html
     *
     * @param roles 角色
     * @return 菜单html
     */
    String menuHtml(List<Role> roles, String username);

    /**
     * 通过ids查询
     *
     * @param ids      ids
     * @param username 用户账号 缓存
     * @return 应用
     */
    List<Application> findInIdsWithUsername(List<Integer> ids, String username);

    /**
     * 获取该菜单下的data url
     *
     * @param application 菜单
     * @return data url
     */
    List<String> urlMapping(Application application);

    /**
     * 通过角色ids查询 缓存
     *
     * @param roleIds  角色id
     * @param username 用户账号用于缓存
     * @return 数据
     */
    List<RoleApplication> findInRoleIdsWithUsername(List<Integer> roleIds, String username);

    /**
     * 根据用户账号查询角色
     *
     * @param username 用户账号
     * @return 角色
     */
    List<Role> findByUsernameWithRole(String username);
}
