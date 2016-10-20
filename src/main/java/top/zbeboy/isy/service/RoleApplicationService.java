package top.zbeboy.isy.service;

import org.jooq.Result;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import top.zbeboy.isy.domain.tables.pojos.RoleApplication;
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord;

import java.util.List;

/**
 * Created by 杨逸云 on 2016/9/29.
 */
@CacheConfig(cacheNames = "role_application")
public interface RoleApplicationService {

    /**
     * 通过角色ids查询 缓存
     * @param roleIds 角色id
     * @param username 用户账号用于缓存
     * @return 数据
     */
    @Cacheable(cacheNames="findInRoleIdsWithUsername",key="#username")
    Result<RoleApplicationRecord> findInRoleIdsWithUsername(List<Integer> roleIds, String username);

    /**
     * 保存
     * @param roleApplication
     */
    void save(RoleApplication roleApplication);

    /**
     * 通过应用id删除
     * @param applicationId 应用id
     */
    void deleteByApplicationId(int applicationId);

    /**
     * 通过角色id删除
     * @param roleId 角色id
     */
    void deleteByRoleId(int roleId);

    /**
     * 根据角色id查询
     * @param roleId 角色id
     * @return 数据
     */
    Result<RoleApplicationRecord> findByRoleId(int roleId);
}
