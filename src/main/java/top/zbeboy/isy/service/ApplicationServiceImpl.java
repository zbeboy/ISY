package top.zbeboy.isy.service;

import com.alibaba.fastjson.JSONObject;
import org.jooq.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.daos.ApplicationDao;
import top.zbeboy.isy.domain.tables.pojos.Application;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.records.ApplicationRecord;
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.application.ApplicationBean;
import top.zbeboy.isy.web.bean.tree.TreeBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static top.zbeboy.isy.domain.Tables.APPLICATION;

/**
 * Created by lenovo on 2016-09-28.
 */
@Service("applicationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ApplicationServiceImpl extends DataTablesPlugin<ApplicationBean> implements ApplicationService {

    private final Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final DSLContext create;

    private ApplicationDao applicationDao;

    @Resource
    private RoleApplicationService roleApplicationService;

    @Autowired
    public ApplicationServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.applicationDao = new ApplicationDao(configuration);
    }

    @Override
    public String menuHtml(List<Role> roles, String web_path, String username) {
        List<Integer> roleIds = new ArrayList<>();
        String html = "";
        roleIds.addAll(roles.stream().map(Role::getRoleId).collect(Collectors.toList()));
        Result<RoleApplicationRecord> roleApplicationRecords = roleApplicationService.findInRoleIdsWithUsername(roleIds, username);
        if (roleApplicationRecords.isNotEmpty()) {
            List<Integer> applicationIds = new ArrayList<>();
            for (RoleApplicationRecord roleApplicationRecord : roleApplicationRecords) {
                if (!applicationIds.contains(roleApplicationRecord.getApplicationId())) {// 防止重复菜单加载
                    applicationIds.add(roleApplicationRecord.getApplicationId());
                }
            }
            Result<ApplicationRecord> applicationRecords = findInIdsAndPid(applicationIds, 0);
            html = firstLevelHtml(applicationRecords, applicationIds, web_path);
        }
        return html;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(Application application) {
        applicationDao.insert(application);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public int saveAndReturnId(Application application) {
        ApplicationRecord applicationRecord = create.insertInto(APPLICATION)
                .set(APPLICATION.APPLICATION_NAME, application.getApplicationName())
                .set(APPLICATION.APPLICATION_SORT, application.getApplicationSort())
                .set(APPLICATION.APPLICATION_PID, application.getApplicationPid())
                .set(APPLICATION.APPLICATION_URL, application.getApplicationUrl())
                .set(APPLICATION.APPLICATION_CODE, application.getApplicationCode())
                .set(APPLICATION.APPLICATION_EN_NAME,application.getApplicationEnName())
                .set(APPLICATION.ICON,application.getIcon())
                .set(APPLICATION.APPLICATION_DATA_URL_START_WITH,application.getApplicationDataUrlStartWith())
                .returning(APPLICATION.APPLICATION_ID)
                .fetchOne();

        return applicationRecord.getApplicationId();
    }

    @Override
    public void update(Application application) {
        applicationDao.update(application);
    }

    @Override
    public void deletes(List<Integer> ids) {
        ids.forEach(id->{
            applicationDao.deleteById(id);
        });
    }

    // 一级菜单
    private String firstLevelHtml(Result<ApplicationRecord> applicationRecords, List<Integer> applicationIds, String web_path) {
        String html = "<ul class=\"nav\" id=\"side-menu\">" +
                "</ul>";
        Document doc = Jsoup.parse(html);
        Element element = doc.getElementById("side-menu");
        for (ApplicationRecord applicationRecord : applicationRecords) { // pid = 0
            String li = "<li>";
            Result<ApplicationRecord> secondLevelRecord = findInIdsAndPid(applicationIds, applicationRecord.getApplicationId());// 查询二级菜单
            String url = getWebPath(applicationRecord.getApplicationUrl(), web_path);
            if (secondLevelRecord.isEmpty()) { // 无下级菜单
                li += "<a href=\"" + url + "\"><i class=\"fa " + applicationRecord.getIcon() + " fa-fw\"></i> " + applicationRecord.getApplicationName() + "<span class=\"fa arrow\"></span></a>";
            } else {
                li += "<a href=\"" + url + "\"><i class=\"fa " + applicationRecord.getIcon() + " fa-fw\"></i> " + applicationRecord.getApplicationName() + "<span class=\"fa arrow\"></span></a>";
                // 生成下级菜单
                li += secondLevelHtml(secondLevelRecord, applicationIds, web_path);
            }
            li += "</li>";
            element.append(li);
        }
        return element.html();
    }

    // 二级菜单
    private String secondLevelHtml(Result<ApplicationRecord> applicationRecords, List<Integer> applicationIds, String web_path) {
        String ul = "<ul class=\"nav nav-second-level\">";
        for (ApplicationRecord applicationRecord : applicationRecords) { // pid = 1级菜单id
            String li = "<li>";
            Result<ApplicationRecord> thirdLevelRecord = findInIdsAndPid(applicationIds, applicationRecord.getApplicationId());// 查询三级菜单
            String url = getWebPath(applicationRecord.getApplicationUrl(), web_path);
            if (thirdLevelRecord.isEmpty()) { // 无下级菜单
                li += "<a href=\"" + url + "\">" + applicationRecord.getApplicationName() + "</a>";
            } else {
                li += "<a href=\"" + url + "\">" + applicationRecord.getApplicationName() + "</a>";
                // 生成下级菜单
                li += thirdLevelHtml(thirdLevelRecord, applicationIds, web_path);
            }
            li += "</li>";
            ul += li;
        }
        ul += "</ul>";
        return ul;
    }

    // 三级菜单
    private String thirdLevelHtml(Result<ApplicationRecord> applicationRecords, List<Integer> applicationIds, String web_path) {
        String ul = "<ul class=\"nav nav-third-level\">";
        for (ApplicationRecord applicationRecord : applicationRecords) { // pid = 2级菜单id
            String url = getWebPath(applicationRecord.getApplicationUrl(), web_path);
            String li = "<li>";
            li += "<a href=\"" + url + "\">" + applicationRecord.getApplicationName() + "</a>";
            li += "</li>";
            ul += li;
        }
        ul += "</ul>";
        return ul;
    }

    /**
     * 得到web path
     *
     * @param applicationUrl
     * @param web_path
     * @return web path
     */
    private String getWebPath(String applicationUrl, String web_path) {
        String url = applicationUrl.trim();
        if (!url.equals("#")) {
            url = web_path + url;
        }
        return url;
    }

    @Override
    public Application findById(int id) {
        return applicationDao.findById(id);
    }

    @Override
    public List<Application> findByPid(int pid) {
        return applicationDao.fetchByApplicationPid(pid);
    }

    @Override
    public Result<ApplicationRecord> findInIdsWithUsername(List<Integer> ids, String username) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_ID.in(ids))
                .fetch();
    }

    @Override
    public Result<ApplicationRecord> findInPids(List<Integer> pids) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_PID.in(pids))
                .fetch();
    }

    @Override
    public Result<ApplicationRecord> findInIdsAndPid(List<Integer> ids, int pid) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_ID.in(ids).and(APPLICATION.APPLICATION_PID.eq(pid)))
                .orderBy(APPLICATION.APPLICATION_SORT)
                .fetch();
    }

    @Override
    public List<String> urlMapping(ApplicationRecord applicationRecord) {
        List<String> urlMapping = new ArrayList<>();
        try {
            if (!ObjectUtils.isEmpty(applicationRecord)) {
                List<String> urlMappingFromText = getUrlMappingFromTxt();
                urlMappingFromText.stream().filter(url -> url.startsWith(applicationRecord.getApplicationDataUrlStartWith())).forEach(urlMapping::add);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return urlMapping;
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<ApplicationBean> dataTablesUtils) {
        return dataPagingQueryAll(dataTablesUtils,create,APPLICATION);
    }

    @Override
    public int countAll() {
        return statisticsAll(create, APPLICATION);
    }

    @Override
    public int countByCondition(DataTablesUtils<ApplicationBean> dataTablesUtils) {
        return statisticsWithCondition(dataTablesUtils,create,APPLICATION);
    }

    @Override
    public List<Application> findByApplicationName(String applicationName) {
        return applicationDao.fetchByApplicationName(applicationName);
    }

    @Override
    public Result<ApplicationRecord> findByApplicationNameNeApplicationId(String applicationName, int applicationId) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_NAME.eq(applicationName).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch();
    }

    @Override
    public List<Application> findByApplicationEnName(String applicationEnName) {
        return applicationDao.fetchByApplicationEnName(applicationEnName);
    }

    @Override
    public Result<ApplicationRecord> findByApplicationEnNameNeApplicationId(String applicationEnName, int applicationId) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_EN_NAME.eq(applicationEnName).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch();
    }

    @Override
    public List<Application> findByApplicationUrl(String applicationUrl) {
        return applicationDao.fetchByApplicationUrl(applicationUrl);
    }

    @Override
    public Result<ApplicationRecord> findByApplicationUrlNeApplicationId(String applicationUrl, int applicationId) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_URL.eq(applicationUrl).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch();
    }

    @Override
    public List<Application> findByApplicationCode(String applicationCode) {
        return applicationDao.fetchByApplicationCode(applicationCode);
    }

    @Override
    public Result<ApplicationRecord> findByApplicationCodeNeApplicationId(String applicationCode, int applicationId) {
        return create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_CODE.eq(applicationCode).and(APPLICATION.APPLICATION_ID.ne(applicationId)))
                .fetch();
    }

    @Override
    public List<TreeBean> getApplicationJson(int pid) {
        return bindingDataToJson(pid);
    }

    /**
     * 绑定数据到treeBean
     * @param id 父id
     * @return list treeBean
     */
    private List<TreeBean> bindingDataToJson(int id){
        List<Application> applications = findByPid(id);
        List<TreeBean> treeBeens = new ArrayList<>();
        if(ObjectUtils.isEmpty(applications)&&applications.isEmpty()){
            treeBeens = null;
        } else {
            for (Application application : applications) { // pid = 0
                TreeBean treeBean = new TreeBean();
                treeBean.setText(application.getApplicationName());
                treeBean.setDataId(application.getApplicationId());
                treeBean.setNodes(bindingDataToJson(application.getApplicationId()));
                treeBeens.add(treeBean);
            }
        }
        return treeBeens;
    }


    /**
     * 应用数据全局搜索条件
     *
     * @param dataTablesUtils
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
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    @Override
    public void sortCondition(DataTablesUtils<ApplicationBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = orderDir.equalsIgnoreCase("asc");
        SortField<Integer> a = null;
        SortField<String> b = null;
        SortField<Byte> c = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if (orderColumnName.equalsIgnoreCase("application_name")) {
                if (isAsc) {
                    b = APPLICATION.APPLICATION_NAME.asc();
                } else {
                    b = APPLICATION.APPLICATION_NAME.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("application_en_name")) {
                if (isAsc) {
                    b = APPLICATION.APPLICATION_EN_NAME.asc();
                } else {
                    b = APPLICATION.APPLICATION_EN_NAME.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("application_pid")) {
                if (isAsc) {
                    a = APPLICATION.APPLICATION_PID.asc();
                } else {
                    a = APPLICATION.APPLICATION_PID.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("application_url")) {
                if (isAsc) {
                    b = APPLICATION.APPLICATION_URL.asc();
                } else {
                    b = APPLICATION.APPLICATION_URL.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("icon")) {
                if (isAsc) {
                    b = APPLICATION.ICON.asc();
                } else {
                    b = APPLICATION.ICON.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("application_sort")) {
                if (isAsc) {
                    a = APPLICATION.APPLICATION_SORT.asc();
                } else {
                    a = APPLICATION.APPLICATION_SORT.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("application_code")) {
                if (isAsc) {
                    b = APPLICATION.APPLICATION_CODE.asc();
                } else {
                    b = APPLICATION.APPLICATION_CODE.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("application_data_url_start_with")) {
                if (isAsc) {
                    b = APPLICATION.APPLICATION_DATA_URL_START_WITH.asc();
                } else {
                    b = APPLICATION.APPLICATION_DATA_URL_START_WITH.desc();
                }
            }
        }

        if (!ObjectUtils.isEmpty(a)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(a);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(a);
            }

        } else if (!ObjectUtils.isEmpty(b)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(b);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(b);
            }
        } else if (!ObjectUtils.isEmpty(c)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(c);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(c);
            }
        } else {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(APPLICATION.APPLICATION_ID.desc());
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(APPLICATION.APPLICATION_ID.desc());
            }
        }
    }

    /**
     * 从文本文件中获取所有url
     * @return urls
     * @throws FileNotFoundException
     */
    public List<String> getUrlMappingFromTxt() throws FileNotFoundException {
        List<String> urlMapping = new ArrayList<>();
        File file = new File(Workbook.URL_MAPPING_FILE_PATH);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                urlMapping.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return urlMapping;
    }
}
