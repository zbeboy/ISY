package top.zbeboy.isy.service.platform;

import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.RoleApplication;
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord;

import java.util.List;

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
     * 保存
     *
     * @param roleApplication 角色与应用
     */
    void save(List<RoleApplication> roleApplication);

    /**
     * 通过应用id删除
     *
     * @param applicationId 应用id
     */
    void deleteByApplicationId(String applicationId);

    /**
     * 通过角色id删除
     *
     * @param roleId 角色id
     */
    void deleteByRoleId(String roleId);

    /**
     * 根据角色id查询
     *
     * @param roleId 角色id
     * @return 数据
     */
    Result<RoleApplicationRecord> findByRoleId(String roleId);

    /**
     * 批量保存或更新角色
     *
     * @param applicationIds 应用ids
     * @param roleId         角色id
     */
    void batchSaveRoleApplication(String applicationIds, String roleId);
}
