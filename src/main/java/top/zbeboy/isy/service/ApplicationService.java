package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import top.zbeboy.isy.domain.tables.pojos.Application;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.records.ApplicationRecord;
import top.zbeboy.isy.web.bean.system.application.ApplicationBean;
import top.zbeboy.isy.web.bean.tree.TreeBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2016-09-28.
 */
@CacheConfig(cacheNames = "application")
public interface ApplicationService {

    /**
     * 通过角色id查询出应用id并生成菜单html
     *
     * @param roles 角色
     * @return 菜单html
     */
    @Cacheable(cacheNames = "menuHtml", key = "#username")
    String menuHtml(List<Role> roles, String username);

    /**
     * 保存
     *
     * @param application 应用
     */
    void save(Application application);

    /**
     * 保存并返回id
     *
     * @param application 应用
     * @return id
     */
    int saveAndReturnId(Application application);

    /**
     * 更新
     *
     * @param application 应用
     */
    void update(Application application);

    /**
     * 批量删除
     *
     * @param ids ids
     */
    void deletes(List<Integer> ids);

    /**
     * 通过id查询
     *
     * @param id id
     * @return 应用
     */
    Application findById(int id);

    /**
     * 通过pid查询
     *
     * @param pid 父id
     * @return 应用
     */
    List<Application> findByPid(int pid);

    /**
     * 通过pid查询
     *
     * @param pid       父id
     * @param collegeId 院id
     * @return 应用
     */
    List<Application> findByPidAndCollegeId(int pid, int collegeId);

    /**
     * 通过ids查询
     *
     * @param ids      ids
     * @param username 用户账号 缓存
     * @return 应用
     */
    @Cacheable(cacheNames = "findInIdsWithUsername", key = "#username")
    Result<ApplicationRecord> findInIdsWithUsername(List<Integer> ids, String username);

    /**
     * 通过pids查询
     *
     * @param pids 父ids
     * @return 应用
     */
    Result<ApplicationRecord> findInPids(List<Integer> pids);

    /**
     * 通过ids和父id查询
     *
     * @param ids ids
     * @param pid 父id
     * @return 应用
     */
    Result<ApplicationRecord> findInIdsAndPid(List<Integer> ids, int pid);

    /**
     * 获取该菜单下的data url
     *
     * @param applicationRecord 菜单
     * @return data url
     */
    @Cacheable(cacheNames = "urlMapping", key = "#applicationRecord.getApplicationId()")
    List<String> urlMapping(ApplicationRecord applicationRecord);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<ApplicationBean> dataTablesUtils);

    /**
     * 应用 总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<ApplicationBean> dataTablesUtils);

    /**
     * 通过应用名查询
     *
     * @param applicationName 应用名
     * @return 应用
     */
    List<Application> findByApplicationName(String applicationName);

    /**
     * 通过应用名与应用id查询
     *
     * @param applicationName 应用名
     * @param applicationId   应用id
     * @return 应用
     */
    Result<ApplicationRecord> findByApplicationNameNeApplicationId(String applicationName, int applicationId);

    /**
     * 通过应用英文名查询
     *
     * @param applicationEnName 应用英文名
     * @return 应用
     */
    List<Application> findByApplicationEnName(String applicationEnName);

    /**
     * 通过应用英文名与应用id查询
     *
     * @param applicationEnName 应用英文名
     * @param applicationId     应用id
     * @return 应用
     */
    Result<ApplicationRecord> findByApplicationEnNameNeApplicationId(String applicationEnName, int applicationId);

    /**
     * 通过应用链接查询
     *
     * @param applicationUrl 应用链接
     * @return 应用
     */
    List<Application> findByApplicationUrl(String applicationUrl);

    /**
     * 通过应用链接与应用id查询
     *
     * @param applicationUrl 应用链接
     * @param applicationId  应用id
     * @return 应用
     */
    Result<ApplicationRecord> findByApplicationUrlNeApplicationId(String applicationUrl, int applicationId);

    /**
     * 通过应用识别码查询
     *
     * @param applicationCode 应用识别码
     * @return 应用
     */
    List<Application> findByApplicationCode(String applicationCode);

    /**
     * 通过应用识别码与应用id查询
     *
     * @param applicationCode 应用识别码
     * @param applicationId   应用id
     * @return 应用
     */
    Result<ApplicationRecord> findByApplicationCodeNeApplicationId(String applicationCode, int applicationId);

    /**
     * 通过父id获取所有子类组成的List
     *
     * @param pid 父id
     * @return list json
     */
    List<TreeBean> getApplicationJson(int pid);

    /**
     * 通过父id与院id获取所有子类组成的List
     *
     * @param pid 父id
     * @return list json
     */
    List<TreeBean> getApplicationJsonByCollegeId(int pid, int collegeId);
}
