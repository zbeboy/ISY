package top.zbeboy.isy.service.cache;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.zbeboy.isy.domain.tables.daos.UsersTypeDao;
import top.zbeboy.isy.domain.tables.pojos.Application;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.pojos.RoleApplication;
import top.zbeboy.isy.domain.tables.pojos.UsersType;
import top.zbeboy.isy.domain.tables.records.ApplicationRecord;
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord;
import top.zbeboy.isy.service.system.ApplicationService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static top.zbeboy.isy.domain.Tables.*;
import static top.zbeboy.isy.domain.tables.UsersType.USERS_TYPE;

/**
 * Created by lenovo on 2017-03-11.
 */
@Service("cacheManageService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CacheManageServiceImpl implements CacheManageService {

    private final Logger log = LoggerFactory.getLogger(CacheManageServiceImpl.class);

    private final DSLContext create;

    @Resource
    private ApplicationService applicationService;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Resource
    private UsersTypeDao usersTypeDao;

    @Autowired
    public CacheManageServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Cacheable(cacheNames = "queryUsersTypeByName", key = "#usersTypeName")
    @Override
    public UsersType findByUsersTypeName(String usersTypeName) {
        return usersTypeDao.fetchOne(USERS_TYPE.USERS_TYPE_NAME, usersTypeName);
    }

    @Cacheable(cacheNames = "queryUsersTypeById", key = "#usersTypeId")
    @Override
    public UsersType findByUsersTypeId(int usersTypeId) {
        return usersTypeDao.findById(usersTypeId);
    }

    @Cacheable(cacheNames = "menuHtml", key = "#username")
    @Override
    public String menuHtml(List<Role> roles, String username) {
        List<Integer> roleIds = new ArrayList<>();
        String html = "";
        roleIds.addAll(roles.stream().map(Role::getRoleId).collect(Collectors.toList()));
        List<RoleApplication> roleApplications = findInRoleIdsWithUsername(roleIds, username);
        if (!roleApplications.isEmpty()) {
            List<Integer> applicationIds = new ArrayList<>();
            for (RoleApplication roleApplication : roleApplications) {
                if (!applicationIds.contains(roleApplication.getApplicationId())) {// 防止重复菜单加载
                    applicationIds.add(roleApplication.getApplicationId());
                }
            }
            Result<ApplicationRecord> applicationRecords = applicationService.findInIdsAndPid(applicationIds, 0);
            html = firstLevelHtml(applicationRecords, applicationIds);
        }
        return html;
    }

    @Cacheable(cacheNames = "userApplicationId", key = "#username")
    @Override
    public List<Application> findInIdsWithUsername(List<Integer> ids, String username) {
        List<Application> applications = new ArrayList<>();
        Result<ApplicationRecord> applicationRecords = create.selectFrom(APPLICATION)
                .where(APPLICATION.APPLICATION_ID.in(ids))
                .fetch();
        if (applicationRecords.isNotEmpty()) {
            applications = applicationRecords.into(Application.class);
        }
        return applications;
    }

    @Cacheable(cacheNames = "urlMapping", key = "#application.getApplicationId()")
    @Override
    public List<String> urlMapping(Application application) {
        List<String> urlMapping = new ArrayList<>();
        if (!ObjectUtils.isEmpty(application)) {
            List<String> urlMappingAll = getUrlMapping();
            urlMappingAll.stream().filter(url -> url.startsWith(application.getApplicationDataUrlStartWith())).forEach(urlMapping::add);
        }
        return urlMapping;
    }

    @Cacheable(cacheNames = "userRoleId", key = "#username")
    @Override
    public List<RoleApplication> findInRoleIdsWithUsername(List<Integer> roleIds, String username) {
        List<RoleApplication> roleApplications = new ArrayList<>();
        Result<RoleApplicationRecord> roleApplicationRecords = create.selectFrom(ROLE_APPLICATION)
                .where(ROLE_APPLICATION.ROLE_ID.in(roleIds))
                .fetch();
        if (roleApplicationRecords.isNotEmpty()) {
            roleApplications = roleApplicationRecords.into(RoleApplication.class);
        }
        return roleApplications;
    }

    @Cacheable(cacheNames = "userRole", key = "#username")
    @Override
    public List<Role> findByUsernameWithRole(String username) {
        List<Role> roleList = new ArrayList<>();
        Result<Record> records = create.select()
                .from(USERS)
                .leftJoin(AUTHORITIES)
                .on(USERS.USERNAME.eq(AUTHORITIES.USERNAME))
                .leftJoin(ROLE)
                .on(AUTHORITIES.AUTHORITY.eq(ROLE.ROLE_EN_NAME))
                .where(USERS.USERNAME.eq(username))
                .fetch();
        if (records.isNotEmpty()) {
            roleList = records.into(Role.class);
        }
        return roleList;
    }

    // 一级菜单
    private String firstLevelHtml(Result<ApplicationRecord> applicationRecords, List<Integer> applicationIds) {
        String html = "<ul class=\"nav\" id=\"side-menu\">" +
                "</ul>";
        Document doc = Jsoup.parse(html);
        Element element = doc.getElementById("side-menu");
        for (ApplicationRecord applicationRecord : applicationRecords) { // pid = 0
            String li = "<li>";
            Result<ApplicationRecord> secondLevelRecord = applicationService.findInIdsAndPid(applicationIds, applicationRecord.getApplicationId());// 查询二级菜单
            String url = getWebPath(applicationRecord.getApplicationUrl());
            if (secondLevelRecord.isEmpty()) { // 无下级菜单
                li += "<a href=\"" + url + "\" class=\"dy_href\"><i class=\"fa " + applicationRecord.getIcon() + " fa-fw\"></i> " + applicationRecord.getApplicationName() + "</a>";
            } else {
                li += "<a href=\"" + url + "\"><i class=\"fa " + applicationRecord.getIcon() + " fa-fw\"></i> " + applicationRecord.getApplicationName() + "<span class=\"fa arrow\"></span></a>";
                // 生成下级菜单
                li += secondLevelHtml(secondLevelRecord, applicationIds);
            }
            li += "</li>";
            element.append(li);
        }
        return element.html();
    }

    // 二级菜单
    private String secondLevelHtml(Result<ApplicationRecord> applicationRecords, List<Integer> applicationIds) {
        StringBuilder stringBuilder = new StringBuilder("<ul class=\"nav nav-second-level\">");
        for (ApplicationRecord applicationRecord : applicationRecords) { // pid = 1级菜单id
            String li = "<li>";
            Result<ApplicationRecord> thirdLevelRecord = applicationService.findInIdsAndPid(applicationIds, applicationRecord.getApplicationId());// 查询三级菜单
            String url = getWebPath(applicationRecord.getApplicationUrl());
            if (thirdLevelRecord.isEmpty()) { // 无下级菜单
                li += "<a href=\"" + url + "\" class=\"dy_href\">" + applicationRecord.getApplicationName() + "</a>";
            } else {
                li += "<a href=\"" + url + "\">" + applicationRecord.getApplicationName() + "<span class=\"fa arrow\"></span></a>";
                // 生成下级菜单
                li += thirdLevelHtml(thirdLevelRecord);
            }
            li += "</li>";
            stringBuilder.append(li);
        }
        stringBuilder.append("</ul>");
        return stringBuilder.toString();
    }

    // 三级菜单
    private String thirdLevelHtml(Result<ApplicationRecord> applicationRecords) {
        StringBuilder stringBuilder = new StringBuilder("<ul class=\"nav nav-third-level\">");
        for (ApplicationRecord applicationRecord : applicationRecords) { // pid = 2级菜单id
            String url = getWebPath(applicationRecord.getApplicationUrl());
            String li = "<li>";
            li += "<a href=\"" + url + "\" class=\"dy_href\">" + applicationRecord.getApplicationName() + "</a>";
            li += "</li>";
            stringBuilder.append(li);
        }
        stringBuilder.append("</ul>");
        return stringBuilder.toString();
    }

    /**
     * 得到web path
     *
     * @param applicationUrl 应用链接
     * @return web path
     */
    private String getWebPath(String applicationUrl) {
        String url = applicationUrl.trim();
        if ("#".equals(url)) {
            url = "javascript:;";
        } else {
            url = "#" + url;
        }
        return url;
    }

    /**
     * 获取所有url
     *
     * @return urls
     */
    public List<String> getUrlMapping() {
        List<String> urlMapping = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> map = this.handlerMapping.getHandlerMethods();
        final String[] url = {""};
        map.forEach((key, value) -> {
            url[0] = key.toString();
            url[0] = url[0].split(",")[0];
            int i1 = url[0].indexOf("[") + 1;
            int i2 = url[0].lastIndexOf("]");
            url[0] = url[0].substring(i1, i2);
            urlMapping.add(url[0]);
        });
        return urlMapping;
    }
}
