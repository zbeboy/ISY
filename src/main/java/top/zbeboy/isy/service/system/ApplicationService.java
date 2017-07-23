package top.zbeboy.isy.service.system;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Application;
import top.zbeboy.isy.domain.tables.records.ApplicationRecord;
import top.zbeboy.isy.web.bean.system.application.ApplicationBean;
import top.zbeboy.isy.web.bean.tree.TreeBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2016-09-28.
 */
public interface ApplicationService {

    /**
     * 保存
     *
     * @param application 应用
     */
    void save(Application application);

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
    void deletes(List<String> ids);

    /**
     * 通过id查询
     *
     * @param id id
     * @return 应用
     */
    Application findById(String id);

    /**
     * 通过pid查询
     *
     * @param pid 父id
     * @return 应用
     */
    List<Application> findByPid(String pid);

    /**
     * 通过pid查询
     *
     * @param pid       父id
     * @param collegeId 院id
     * @return 应用
     */
    List<Application> findByPidAndCollegeId(String pid, int collegeId);

    /**
     * 通过pids查询
     *
     * @param pids 父ids
     * @return 应用
     */
    Result<ApplicationRecord> findInPids(List<String> pids);

    /**
     * 通过ids和父id查询
     *
     * @param ids ids
     * @param pid 父id
     * @return 应用
     */
    Result<ApplicationRecord> findInIdsAndPid(List<String> ids, String pid);

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
    Result<ApplicationRecord> findByApplicationNameNeApplicationId(String applicationName, String applicationId);

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
    Result<ApplicationRecord> findByApplicationEnNameNeApplicationId(String applicationEnName, String applicationId);

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
    Result<ApplicationRecord> findByApplicationUrlNeApplicationId(String applicationUrl, String applicationId);

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
    Result<ApplicationRecord> findByApplicationCodeNeApplicationId(String applicationCode, String applicationId);

    /**
     * 通过父id获取所有子类组成的List
     *
     * @param pid 父id
     * @return list json
     */
    List<TreeBean> getApplicationJson(String pid);

    /**
     * 通过父id与院id获取所有子类组成的List
     *
     * @param pid 父id
     * @return list json
     */
    List<TreeBean> getApplicationJsonByCollegeId(String pid, int collegeId);
}
