package top.zbeboy.isy.service.cache

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.SystemAlertTypeDao
import top.zbeboy.isy.domain.tables.daos.UsersTypeDao
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.domain.tables.records.ApplicationRecord
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.ApplicationService
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-07 .
 **/
@Service("cacheManageService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class CacheManageServiceImpl @Autowired constructor(dslContext: DSLContext) : CacheManageService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var applicationService: ApplicationService

    @Autowired
    open lateinit var handlerMapping: RequestMappingHandlerMapping

    @Resource
    open lateinit var usersTypeDao: UsersTypeDao

    @Resource
    open lateinit var systemAlertTypeDao: SystemAlertTypeDao

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var stringRedisTemplate: StringRedisTemplate

    @Resource(name = "redisTemplate")
    open lateinit var applicationValueOperations: ValueOperations<String, List<Application>>

    @Resource(name = "redisTemplate")
    open lateinit var stringListValueOperations: ValueOperations<String, List<String>>

    @Resource(name = "redisTemplate")
    open lateinit var roleApplicationValueOperations: ValueOperations<String, List<RoleApplication>>

    @Resource(name = "redisTemplate")
    open lateinit var roleValueOperations: ValueOperations<String, List<Role>>

    @Resource(name = "redisTemplate")
    open lateinit var integerValueOperations: ValueOperations<String, Int>

    @Cacheable(cacheNames = arrayOf(CacheBook.QUERY_USER_TYPE_BY_NAME), key = "#usersTypeName")
    override fun findByUsersTypeName(usersTypeName: String): UsersType {
        return usersTypeDao.fetchOne(USERS_TYPE.USERS_TYPE_NAME, usersTypeName)
    }

    @Cacheable(cacheNames = arrayOf(CacheBook.QUERY_USER_TYPE_BY_ID), key = "#usersTypeId")
    override fun findByUsersTypeId(usersTypeId: Int): UsersType {
        return usersTypeDao.findById(usersTypeId)
    }

    override fun getRoleCollegeId(users: Users): Int {
        val cacheKey = CacheBook.USER_COLLEGE_ID + users.username
        if (integerValueOperations.operations.hasKey(cacheKey)!!) {
            return integerValueOperations.get(cacheKey)
        }
        var collegeId = 0
        val record = usersService.findUserSchoolInfo(users)
        if (record.isPresent) {
            val college = record.get().into(College::class.java)
            if (!ObjectUtils.isEmpty(college)) {
                collegeId = college.collegeId!!
                integerValueOperations.set(cacheKey, collegeId, CacheBook.EXPIRES_HOURS, TimeUnit.HOURS)
            }
        }
        return collegeId
    }

    override fun getRoleDepartmentId(users: Users): Int {
        val cacheKey = CacheBook.USER_DEPARTMENT_ID + users.username
        if (integerValueOperations.operations.hasKey(cacheKey)!!) {
            return integerValueOperations.get(cacheKey)
        }
        var departmentId = 0
        val record = usersService.findUserSchoolInfo(users)
        if (record.isPresent) {
            val department = record.get().into(Department::class.java)
            if (!ObjectUtils.isEmpty(department)) {
                departmentId = department.departmentId!!
                integerValueOperations.set(cacheKey, departmentId, CacheBook.EXPIRES_HOURS, TimeUnit.HOURS)
            }
        }
        return departmentId
    }

    @Cacheable(cacheNames = arrayOf(CacheBook.QUERY_SYSTEM_ALERT_TYPE_BY_NAME), key = "#name")
    override fun findBySystemAlertTypeName(name: String): SystemAlertType {
        return systemAlertTypeDao.fetchOne(SYSTEM_ALERT_TYPE.NAME, name)
    }

    override fun menuHtml(roles: List<Role>, username: String): String {
        val cacheKey = CacheBook.MENU_HTML + username
        val ops = this.stringRedisTemplate.opsForValue()
        if (this.stringRedisTemplate.hasKey(cacheKey)!!) {
            return ops.get(cacheKey)
        }
        val roleIds = ArrayList<String>()
        var html = ""

        roles.forEach { role -> roleIds.add(role.roleId) }

        val roleApplications = findInRoleIdsWithUsername(roleIds, username)
        if (!roleApplications.isEmpty()) {
            val applicationIds = ArrayList<String>()
            roleApplications
                    .filterNot { applicationIds.contains(it.applicationId) }
                    .forEach {
                        // 防止重复菜单加载
                        applicationIds.add(it.applicationId)
                    }
            val applicationRecords = applicationService.findInIdsAndPid(applicationIds, "0")
            html = firstLevelHtml(applicationRecords, applicationIds)
        }
        ops.set(cacheKey, html, CacheBook.EXPIRES_MINUTES, TimeUnit.MINUTES)
        return html
    }

    override fun findInIdsWithUsername(ids: List<String>, username: String): List<Application> {
        val cacheKey = CacheBook.USER_APPLICATION_ID + username
        if (applicationValueOperations.operations.hasKey(cacheKey)!!) {
            return applicationValueOperations.get(cacheKey)
        }
        var applications: List<Application> = ArrayList()
        val applicationRecords = create.selectFrom<ApplicationRecord>(APPLICATION)
                .where(APPLICATION.APPLICATION_ID.`in`(ids))
                .fetch()
        if (applicationRecords.isNotEmpty) {
            applications = applicationRecords.into(Application::class.java)
        }
        applicationValueOperations.set(cacheKey, applications, CacheBook.EXPIRES_MINUTES, TimeUnit.MINUTES)
        return applications
    }

    override fun urlMapping(application: Application): List<String> {
        val cacheKey = CacheBook.URL_MAPPING + application.applicationId
        if (stringListValueOperations.operations.hasKey(cacheKey)!!) {
            return stringListValueOperations.get(cacheKey)
        }
        val urlMapping = ArrayList<String>()
        if (!ObjectUtils.isEmpty(application)) {
            val urlMappingAll = getUrlMapping()
            urlMappingAll.stream().filter { url -> url.startsWith(application.applicationDataUrlStartWith) }.forEach({ urlMapping.add(it) })
        }
        stringListValueOperations.set(cacheKey, urlMapping, CacheBook.EXPIRES_APPLICATION_ID_DAYS, TimeUnit.DAYS)
        return urlMapping
    }

    override fun findInRoleIdsWithUsername(roleIds: List<String>, username: String): List<RoleApplication> {
        val cacheKey = CacheBook.USER_ROLE_ID + username
        if (roleApplicationValueOperations.operations.hasKey(cacheKey)!!) {
            return roleApplicationValueOperations.get(cacheKey)
        }
        var roleApplications: List<RoleApplication> = ArrayList()
        val roleApplicationRecords = create.selectFrom<RoleApplicationRecord>(ROLE_APPLICATION)
                .where(ROLE_APPLICATION.ROLE_ID.`in`(roleIds))
                .fetch()
        if (roleApplicationRecords.isNotEmpty) {
            roleApplications = roleApplicationRecords.into(RoleApplication::class.java)
        }
        roleApplicationValueOperations.set(cacheKey, roleApplications, CacheBook.EXPIRES_MINUTES, TimeUnit.MINUTES)
        return roleApplications
    }

    override fun findByUsernameWithRole(username: String): List<Role> {
        val cacheKey = CacheBook.USER_ROLE + username
        if (roleValueOperations.operations.hasKey(cacheKey)!!) {
            return roleValueOperations.get(cacheKey)
        }
        var roleList: List<Role> = ArrayList()
        val records = create.select()
                .from(USERS)
                .leftJoin(AUTHORITIES)
                .on(USERS.USERNAME.eq(AUTHORITIES.USERNAME))
                .leftJoin(ROLE)
                .on(AUTHORITIES.AUTHORITY.eq(ROLE.ROLE_EN_NAME))
                .where(USERS.USERNAME.eq(username))
                .fetch()
        if (records.isNotEmpty) {
            roleList = records.into(Role::class.java)
        }
        roleValueOperations.set(cacheKey, roleList, CacheBook.EXPIRES_MINUTES, TimeUnit.MINUTES)
        return roleList
    }

    // 一级菜单
    private fun firstLevelHtml(applicationRecords: Result<ApplicationRecord>, applicationIds: List<String>): String {
        val html = "<ul class=\"nav\" id=\"side-menu\">" + "</ul>"
        val doc = Jsoup.parse(html)
        val element = doc.getElementById("side-menu")
        for (applicationRecord in applicationRecords) { // pid = 0
            var li = "<li>"
            val secondLevelRecord = applicationService.findInIdsAndPid(applicationIds, applicationRecord.applicationId)// 查询二级菜单
            val url = getWebPath(applicationRecord.applicationUrl)
            if (secondLevelRecord.isEmpty()) { // 无下级菜单
                li += "<a href=\"" + url + "\" class=\"dy_href\"><i class=\"fa " + applicationRecord.icon + " fa-fw\"></i> " + applicationRecord.applicationName + "</a>"
            } else {
                li += "<a href=\"" + url + "\"><i class=\"fa " + applicationRecord.icon + " fa-fw\"></i> " + applicationRecord.applicationName + "<span class=\"fa arrow\"></span></a>"
                // 生成下级菜单
                li += secondLevelHtml(secondLevelRecord, applicationIds)
            }
            li += "</li>"
            element.append(li)
        }
        return element.html()
    }

    // 二级菜单
    private fun secondLevelHtml(applicationRecords: Result<ApplicationRecord>, applicationIds: List<String>): String {
        val stringBuilder = StringBuilder("<ul class=\"nav nav-second-level\">")
        for (applicationRecord in applicationRecords) { // pid = 1级菜单id
            var li = "<li>"
            val thirdLevelRecord = applicationService.findInIdsAndPid(applicationIds, applicationRecord.applicationId)// 查询三级菜单
            val url = getWebPath(applicationRecord.applicationUrl)
            if (thirdLevelRecord.isEmpty()) { // 无下级菜单
                li += "<a href=\"" + url + "\" class=\"dy_href\">" + applicationRecord.applicationName + "</a>"
            } else {
                li += "<a href=\"" + url + "\">" + applicationRecord.applicationName + "<span class=\"fa arrow\"></span></a>"
                // 生成下级菜单
                li += thirdLevelHtml(thirdLevelRecord)
            }
            li += "</li>"
            stringBuilder.append(li)
        }
        stringBuilder.append("</ul>")
        return stringBuilder.toString()
    }

    // 三级菜单
    private fun thirdLevelHtml(applicationRecords: Result<ApplicationRecord>): String {
        val stringBuilder = StringBuilder("<ul class=\"nav nav-third-level\">")
        for (applicationRecord in applicationRecords) { // pid = 2级菜单id
            val url = getWebPath(applicationRecord.applicationUrl)
            var li = "<li>"
            li += "<a href=\"" + url + "\" class=\"dy_href\">" + applicationRecord.applicationName + "</a>"
            li += "</li>"
            stringBuilder.append(li)
        }
        stringBuilder.append("</ul>")
        return stringBuilder.toString()
    }

    /**
     * 得到web path
     *
     * @param applicationUrl 应用链接
     * @return web path
     */
    private fun getWebPath(applicationUrl: String): String {
        var url = applicationUrl.trim { it <= ' ' }
        url = if ("#" == url) {
            "javascript:;"
        } else {
            "#" + url
        }
        return url
    }

    /**
     * 获取所有url
     *
     * @return urls
     */
    private fun getUrlMapping(): List<String> {
        val urlMapping = ArrayList<String>()
        val map = this.handlerMapping.handlerMethods
        val url = arrayOf("")
        map.forEach { key, _ ->
            url[0] = key.toString()
            url[0] = url[0].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val i1 = url[0].indexOf("[") + 1
            val i2 = url[0].lastIndexOf("]")
            url[0] = url[0].substring(i1, i2)
            urlMapping.add(url[0])
        }
        return urlMapping
    }
}