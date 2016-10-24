package top.zbeboy.isy.service;


import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Select;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;
import top.zbeboy.isy.web.bean.platform.users.UsersBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-01-05.
 */
@CacheConfig(cacheNames = "users")
public interface UsersService {

    /**
     * 根据用户名获取Users表完整信息
     *
     * @param username 用户账号
     * @return 用户信息
     */
    Users findByUsername(String username);

    /**
     * 从session中获取用户完整信息
     *
     * @return session中的用户信息
     */
    Users getUserFromSession();

    /**
     * 获取用户学校相关信息 注：用户必须是学生或教职工
     *
     * @return 用户学校相关信息
     */
    Optional<Record> findUserSchoolInfo(Users users);

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户们
     */
    List<Users> findByMobile(String mobile);

    /**
     * 保存用户
     *
     * @param users
     */
    void save(Users users);

    /**
     * 更新用户
     *
     * @param users
     */
    void update(Users users);

    /**
     * 更新注销状态
     *
     * @param ids
     * @param enabled
     */
    void updateEnabled(List<String> ids, Byte enabled);

    /**
     * 通过id删除
     *
     * @param username 用户账号
     */
    void deleteById(String username);

    /**
     * 检验用户所在院校系专业班级是否已被注销
     *
     * @param users 用户信息
     * @return true 被注销 false 未被注销
     */
    boolean validSCDSOIsDel(Users users);

    /**
     * 根据用户账号查询角色
     *
     * @param username 用户账号
     * @return 角色
     */
    @Cacheable(cacheNames = "findByUsernameWithRole", key = "#username")
    Result<Record> findByUsernameWithRole(String username);

    /**
     * 根据用户账号查询角色 无缓存
     *
     * @param username 用户账号
     * @return 角色
     */
    Result<Record1<String>> findByUsernameWithRoleNoCache(String username);

    /**
     * 根据当前用户权限查询低于当前用户权限的用户的 select
     *
     * @return
     */
    Select<AuthoritiesRecord> existsAuthoritiesSelect();

    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils
     * @return 用户
     */
    Result<Record> findAllByPageExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils);

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils
     * @return 用户
     */
    Result<Record> findAllByPageNotExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils);

    /**
     * 统计有权限的用户
     *
     * @return 总数
     */
    int countAllExistsAuthorities();

    /**
     * 统计无权限的用户
     *
     * @return 总数
     */
    int countAllNotExistsAuthorities();

    /**
     * 根据条件统计有权限的用户
     *
     * @param dataTablesUtils
     * @return 数量
     */
    int countByConditionExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils);

    /**
     * 根据条件统计无权限的用户
     *
     * @param dataTablesUtils
     * @return 数量
     */
    int countByConditionNotExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils);
}
