package top.zbeboy.isy.glue.common

import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.stereotype.Component
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.elastic.config.ElasticBook
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.SQLQueryUtils
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-11 .
 **/
@Component
open class MethodGlueCommon {

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    /**
     * 构建该角色查询条件
     */
    fun buildExistsAuthoritiesCondition(): BoolQueryBuilder {
        val boolqueryBuilder = QueryBuilders.boolQuery()
        when {
            roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) -> {
                boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", ElasticBook.SYSTEM_AUTHORITIES))
                boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES))
            }
            roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) -> {// 管理员
                val users = usersService.getUserFromSession()
                val collegeId = cacheManageService.getRoleCollegeId(users!!)
                boolqueryBuilder.must(QueryBuilders.termQuery("authorities", ElasticBook.HAS_AUTHORITIES))
                boolqueryBuilder.must(QueryBuilders.termQuery("collegeId", collegeId))
            }
            else -> {
                val users = usersService.getUserFromSession()
                val departmentId = cacheManageService.getRoleDepartmentId(users!!)
                boolqueryBuilder.must(QueryBuilders.termQuery("authorities", ElasticBook.HAS_AUTHORITIES))
                boolqueryBuilder.must(QueryBuilders.termQuery("departmentId", departmentId))
                boolqueryBuilder.mustNot(QueryBuilders.termQuery("username", SQLQueryUtils.phraseQueryingUsername(users.username)))
            }
        }
        return boolqueryBuilder
    }

    /**
     * 构建该角色查询条件
     */
    fun buildNoExistsAuthoritiesCondition(): BoolQueryBuilder {
        val boolqueryBuilder = QueryBuilders.boolQuery()
        when {
            roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) -> boolqueryBuilder.must(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES))
            roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES) -> {// 管理员
                val users = usersService.getUserFromSession()
                val collegeId = cacheManageService.getRoleCollegeId(users!!)
                boolqueryBuilder.must(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES))
                boolqueryBuilder.must(QueryBuilders.termQuery("collegeId", collegeId))
            }
            else -> {
                val users = usersService.getUserFromSession()
                val departmentId = cacheManageService.getRoleDepartmentId(users!!)
                boolqueryBuilder.must(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES))
                boolqueryBuilder.must(QueryBuilders.termQuery("departmentId", departmentId))
                boolqueryBuilder.mustNot(QueryBuilders.termQuery("username", SQLQueryUtils.phraseQueryingUsername(users.username)))
            }
        }
        return boolqueryBuilder
    }
}