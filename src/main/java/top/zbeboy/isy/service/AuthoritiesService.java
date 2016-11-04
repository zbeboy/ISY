package top.zbeboy.isy.service;

import org.jooq.Record;
import top.zbeboy.isy.domain.tables.pojos.Authorities;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-02-21.
 */
public interface AuthoritiesService {

    /**
     * 通过用户名查询
     *
     * @param username 账号
     * @return 权限
     */
    List<AuthoritiesRecord> findByUsername(String username);

    /**
     * 保存
     *
     * @param authorities 权限
     */
    void save(Authorities authorities);

    /**
     * 通过用户账号删除
     *
     * @param username 用户账号
     */
    void deleteByUsername(String username);

    /**
     * 通过权限删除
     *
     * @param authorities 权限
     */
    void deleteByAuthorities(String authorities);

    /**
     * 检查当前用户是否有此权限
     *
     * @param authority 权限
     * @return true 有 false 无
     */
    boolean isCurrentUserInRole(String authority);

    /**
     * 获取角色院id
     *
     * @param record 数据库信息
     * @return id
     */
    int getRoleCollegeId(Optional<Record> record);
}
