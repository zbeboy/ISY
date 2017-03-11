package top.zbeboy.isy.service.platform;

import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.RoleApplication;
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord;

/**
 * Created by lenovo on 2016/9/29.
 */
public interface RoleApplicationService {

    /**
     * 保存
     *
     * @param roleApplication 角色与应用
     */
    void save(RoleApplication roleApplication);

    /**
     * 通过应用id删除
     *
     * @param applicationId 应用id
     */
    void deleteByApplicationId(int applicationId);

    /**
     * 通过角色id删除
     *
     * @param roleId 角色id
     */
    void deleteByRoleId(int roleId);

    /**
     * 根据角色id查询
     *
     * @param roleId 角色id
     * @return 数据
     */
    Result<RoleApplicationRecord> findByRoleId(int roleId);
}
