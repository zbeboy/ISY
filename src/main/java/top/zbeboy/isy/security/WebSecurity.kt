package top.zbeboy.isy.security

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.pojos.Role
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.platform.UsersService
import java.util.*
import java.util.stream.Collectors
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Spring security 路径权限控制器
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
class WebSecurity {

    private val log = LoggerFactory.getLogger(WebSecurity::class.java)

    @Autowired
    private val usersService: UsersService? = null

    @Resource
    private val cacheManageService: CacheManageService? = null

    /**
     * 权限控制检查
     *
     * @param authentication 权限对象
     * @param request        请求
     * @return true可访问 false 不可访问该路径
     */
    fun check(authentication: Authentication, request: HttpServletRequest): Boolean {
        try {
            val users = usersService!!.userFromSession
            if (ObjectUtils.isEmpty(users)) {
                return false
            }
            // 权限控制
            val uri = StringUtils.trimAllWhitespace(request.requestURI)
            // 欢迎页
            if (uri.endsWith("/web/menu/backstage")) {
                return true
            }
            var hasRole = false
            val roleList = cacheManageService!!.findByUsernameWithRole(users.username)// 已缓存
            val roleIds = ArrayList<String>()
            roleIds.addAll(roleList.stream().map<String> { Role::getRoleId.toString() }.collect(Collectors.toList()))

            val roleApplications = cacheManageService.findInRoleIdsWithUsername(roleIds, users.username)// 已缓存
            if (!roleApplications.isEmpty()) {
                val applicationIds = ArrayList<String>()
                // 防止重复菜单加载
                roleApplications.stream().filter { roleApplication -> !applicationIds.contains(roleApplication.applicationId) }.forEach {// 防止重复菜单加载
                    roleApplication ->
                    applicationIds.add(roleApplication.applicationId)
                }

                val applications = cacheManageService.findInIdsWithUsername(applicationIds, users.username)// 已缓存
                for (application in applications) {
                    if (uri.endsWith(application.applicationUrl)) {
                        hasRole = true
                        break
                    }
                    if (StringUtils.hasLength(application.applicationDataUrlStartWith)) {
                        val urlMapping = cacheManageService.urlMapping(application)// 已缓存
                        if (!ObjectUtils.isEmpty(urlMapping)) {
                            val urlOne = urlMapping.stream().filter({ uri.endsWith(it) }).findFirst()
                            if (urlOne.isPresent) {
                                hasRole = true
                                break
                            }
                        }
                    }
                }
            }
            return hasRole
        } catch (e: Exception) {
            log.error("Web security exception is {}", e)
            return false;
        }
    }
}