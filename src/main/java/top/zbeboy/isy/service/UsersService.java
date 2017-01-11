package top.zbeboy.isy.service;


import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Select;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;
import top.zbeboy.isy.domain.tables.records.UsersRecord;
import top.zbeboy.isy.web.bean.platform.users.UsersBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.Date;
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
     * 注意 用于定时 查询未验证用户
     *
     * @param joinDate      加入时间
     * @param verifyMailbox 验证否
     * @return 用户
     */
    Result<UsersRecord> findByJoinDateAndVerifyMailbox(Date joinDate, Byte verifyMailbox);

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
     * 根据手机号查询用户 注：不等于用户账号
     *
     * @param mobile   手机号
     * @param username 用户账号
     * @return 用户们
     */
    Result<UsersRecord> findByMobileNeUsername(String mobile, String username);

    /**
     * 保存用户
     *
     * @param users 用户
     */
    void save(Users users);

    /**
     * 更新用户
     *
     * @param users 用户
     */
    void update(Users users);

    /**
     * 更新注销状态
     *
     * @param ids     ids
     * @param enabled 状态
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
     * @return 条件
     */
    Select<AuthoritiesRecord> existsAuthoritiesSelect();

    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    Result<Record> findAllByPageExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils);

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
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
     * @param dataTablesUtils datatables工具类
     * @return 数量
     */
    int countByConditionExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils);

    /**
     * 根据条件统计无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 数量
     */
    int countByConditionNotExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils);
}
