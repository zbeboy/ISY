package top.zbeboy.isy.service.system;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.ApplicationDao;
import top.zbeboy.isy.domain.tables.pojos.Application;
import top.zbeboy.isy.domain.tables.records.ApplicationRecord;
import top.zbeboy.isy.service.platform.RoleApplicationService;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.application.ApplicationBean;
import top.zbeboy.isy.web.bean.tree.TreeBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.APPLICATION;
import static top.zbeboy.isy.domain.Tables.COLLEGE_APPLICATION;

/**
 * Created by lenovo on 2016-09-28.
 */
@Slf4j
@Service("applicationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ApplicationServiceImpl extends DataTablesPlugin<ApplicationBean> implements ApplicationService {

    private final DSLContext create;

    @Resource
    private ApplicationDao applicationDao;

    @Resource
    private RoleApplicationService roleApplicationService;

    @Autowired
    public ApplicationServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(Application application) {
        applicationDao.insert(application);
    }

    @Override
    public void update(Application application) {
        applicationDao.update(application);
    }

    @Override
    public void deletes(List<String> ids) {
        applicationDao.deleteById(ids);
    }

    @Override
    public Application findById(String id) {
        return applicationDao.findById(id);
    }

    @Override
    public List<Application> findByPid(String pid) {
        return applicationDao.fetchByApplicationPid(pid);
    }

    @Override
    public List<Application> findByPidAndCollegeId(String pid, int collegeId) {
        List<Application> applications = new ArrayList<>();
        Result<Record> records = create.select()
                .from(APPLICATION)
                .join(COLLEGE_APPLICATION)
                .on(APPLICATION.APPLICATION_ID.eq(COLLEGE_APPLICATION.APPLICATION_ID))
                .where(APPLICATION.APPLICATION_PID.eq(pid).and(COLLEGE_APPLICATION.COLLEGE_ID.eq(collegeId)))
                .fetch();
        if (records.isNotEmpty()) {
            applications = records.into(Application.class);
        }
        return applications;
    }

    @Override
    public Result<ApplicationRecord> findInPids(List<String> pids) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_PID.in(pids))
                .fetch();
    }

    @Override
    public Result<ApplicationRecord> findInIdsAndPid(List<String> ids, String pid) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_ID.in(ids).and(APPLICATION.APPLICATION_PID.eq(pid)))
                .orderBy(APPLICATION.APPLICATION_SORT)
                .fetch();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<ApplicationBean> dataTablesUtils) {
        return dataPagingQueryAll(dataTablesUtils, create, APPLICATION);
    }

    @Override
    public int countAll() {
        return statisticsAll(create, APPLICATION);
    }

    @Override
    public int countByCondition(DataTablesUtils<ApplicationBean> dataTablesUtils) {
        return statisticsWithCondition(dataTablesUtils, create, APPLICATION);
    }

    @Override
    public List<Application> findByApplicationName(String applicationName) {
        return applicationDao.fetchByApplicationName(applicationName);
    }

    @Override
    public Result<ApplicationRecord> findByApplicationNameNeApplicationId(String applicationName, String applicationId) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_NAME.eq(applicationName).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch();
    }

    @Override
    public List<Application> findByApplicationEnName(String applicationEnName) {
        return applicationDao.fetchByApplicationEnName(applicationEnName);
    }

    @Override
    public Result<ApplicationRecord> findByApplicationEnNameNeApplicationId(String applicationEnName, String applicationId) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_EN_NAME.eq(applicationEnName).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch();
    }

    @Override
    public List<Application> findByApplicationUrl(String applicationUrl) {
        return applicationDao.fetchByApplicationUrl(applicationUrl);
    }

    @Override
    public Result<ApplicationRecord> findByApplicationUrlNeApplicationId(String applicationUrl, String applicationId) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_URL.eq(applicationUrl).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch();
    }

    @Override
    public List<Application> findByApplicationCode(String applicationCode) {
        return applicationDao.fetchByApplicationCode(applicationCode);
    }

    @Override
    public Result<ApplicationRecord> findByApplicationCodeNeApplicationId(String applicationCode, String applicationId) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_CODE.eq(applicationCode).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch();
    }

    @Override
    public List<TreeBean> getApplicationJson(String pid) {
        return bindingDataToJson(pid);
    }

    @Override
    public List<TreeBean> getApplicationJsonByCollegeId(String pid, int collegeId) {
        return bindingDataToJson(pid, collegeId);
    }

    /**
     * 绑定数据到treeBean
     *
     * @param id 父id
     * @return list treeBean
     */
    private List<TreeBean> bindingDataToJson(String id) {
        List<Application> applications = findByPid(id);
        List<TreeBean> treeBeens = new ArrayList<>();
        if (ObjectUtils.isEmpty(applications)) {
            treeBeens = null;
        } else {
            for (Application application : applications) { // pid = 0
                TreeBean treeBean = new TreeBean(application.getApplicationName(), bindingDataToJson(application.getApplicationId()), application.getApplicationId());
                treeBeens.add(treeBean);
            }
        }
        return treeBeens;
    }

    /**
     * 绑定数据到treeBean
     *
     * @param id        父id
     * @param collegeId 院id
     * @return list treeBean
     */
    private List<TreeBean> bindingDataToJson(String id, int collegeId) {
        List<Application> applications = findByPidAndCollegeId(id, collegeId);
        List<TreeBean> treeBeens = new ArrayList<>();
        if (ObjectUtils.isEmpty(applications)) {
            treeBeens = null;
        } else {
            for (Application application : applications) { // pid = 0
                TreeBean treeBean = new TreeBean(application.getApplicationName(), bindingDataToJson(application.getApplicationId(), collegeId), application.getApplicationId());
                treeBeens.add(treeBean);
            }
        }
        return treeBeens;
    }


    /**
     * 应用数据全局搜索条件
     *
     * @param dataTablesUtils datatable工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<ApplicationBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String applicationName = StringUtils.trimWhitespace(search.getString("applicationName"));
            String applicationEnName = StringUtils.trimWhitespace(search.getString("applicationEnName"));
            String applicationCode = StringUtils.trimWhitespace(search.getString("applicationCode"));
            if (StringUtils.hasLength(applicationName)) {
                a = APPLICATION.APPLICATION_NAME.like(SQLQueryUtils.likeAllParam(applicationName));
            }

            if (StringUtils.hasLength(applicationEnName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = APPLICATION.APPLICATION_EN_NAME.like(SQLQueryUtils.likeAllParam(applicationEnName));
                } else {
                    a = a.and(APPLICATION.APPLICATION_EN_NAME.like(SQLQueryUtils.likeAllParam(applicationEnName)));
                }
            }

            if (StringUtils.hasLength(applicationCode)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = APPLICATION.APPLICATION_CODE.like(SQLQueryUtils.likeAllParam(applicationCode));
                } else {
                    a = a.and(APPLICATION.APPLICATION_CODE.like(SQLQueryUtils.likeAllParam(applicationCode)));
                }
            }

        }
        return a;
    }

    /**
     * 应用数据排序
     *
     * @param dataTablesUtils     datatable工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<ApplicationBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("application_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_NAME.asc();
                } else {
                    sortField[0] = APPLICATION.APPLICATION_NAME.desc();
                }
            }

            if ("application_en_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_EN_NAME.asc();
                } else {
                    sortField[0] = APPLICATION.APPLICATION_EN_NAME.desc();
                }
            }

            if ("application_pid".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_PID.asc();
                    sortField[1] = APPLICATION.APPLICATION_ID.asc();
                } else {
                    sortField[0] = APPLICATION.APPLICATION_PID.desc();
                    sortField[1] = APPLICATION.APPLICATION_ID.desc();
                }
            }

            if ("application_url".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_URL.asc();
                } else {
                    sortField[0] = APPLICATION.APPLICATION_URL.desc();
                }
            }

            if ("icon".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = APPLICATION.ICON.asc();
                    sortField[1] = APPLICATION.APPLICATION_ID.asc();
                } else {
                    sortField[0] = APPLICATION.ICON.desc();
                    sortField[1] = APPLICATION.APPLICATION_ID.desc();
                }
            }

            if ("application_sort".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_SORT.asc();
                } else {
                    sortField[0] = APPLICATION.APPLICATION_SORT.desc();
                }
            }

            if ("application_code".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_CODE.asc();
                } else {
                    sortField[0] = APPLICATION.APPLICATION_CODE.desc();
                }
            }

            if ("application_data_url_start_with".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = APPLICATION.APPLICATION_DATA_URL_START_WITH.asc();
                } else {
                    sortField[0] = APPLICATION.APPLICATION_DATA_URL_START_WITH.desc();
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
