package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.records.RoleRecord;
import top.zbeboy.isy.web.bean.platform.role.RoleBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-10-04.
 */
public interface RoleService {

    /**
     *通过角色名查询
     * @param roleName 角色名
     * @return 角色
     */
    Role findByRoleName(String roleName);

    /**
     * 通过主键查询
     * @param id 主键
     * @return 角色
     */
    Role findById(int id);

    /**
     * 根据角色id关联查询
     * @param roleId 角色id
     * @return 关联查询结果
     */
    Optional<Record> findByRoleIdRelation(int roleId);

    /**
     * 通过角色名和院id查询
     * @param roleName 角色名
     * @param collegeId 院id
     * @return 结果集
     */
    Result<Record> findByRoleNameAndCollegeId(String roleName, int collegeId);

    /**
     * 通过角色名和院id查询 注不等于角色id
     * @param roleName 角色名
     * @param collegeId 院id
     * @param roleId 角色id
     * @return 结果集
     */
    Result<Record> findByRoleNameAndCollegeIdNeRoleId(String roleName, int collegeId, int roleId);

    /**
     * 保存
     * @param role
     */
    void save(Role role);

    /**
     * 保存并返回id
     * @param role 角色
     * @return id
     */
    int saveAndReturnId(Role role);

    /**
     * 更新
     * @param role 角色
     */
    void update(Role role);

    /**
     * 通过主键删除
     * @param id 主键
     */
    void deleteById(int id);

    /**
     * 批量查询
     * @param ids
     * @return roles
     */
    Result<RoleRecord> findInRoleId(List<Integer> ids);

    /**
     * 分页查询
     *
     * @param dataTablesUtils
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<RoleBean> dataTablesUtils);

    /**
     * 角色总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<RoleBean> dataTablesUtils);

    /**
     * 查询不在院与角色关联表中的角色
     * @param roleName 角色名
     * @return 数据
     */
    Result<RoleRecord> findByRoleNameNotExistsCollegeRole(String roleName);

    /**
     * 查询不在院与角色关联表中的角色 注：不等于角色id
     * @param roleName 角色名
     * @param roleId 角色id
     * @return 结果集
     */
    Result<RoleRecord> findByRoleNameNotExistsCollegeRoleNeRoleId(String roleName, int roleId);
}
