package top.zbeboy.isy.elastic.repository

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import top.zbeboy.isy.elastic.pojo.UsersElastic

/**
 * Created by zbeboy 2017-11-19 .
 **/
interface UsersElasticRepository : ElasticsearchRepository<UsersElastic, String> {
    /**
     * 通过权限id统计
     *
     * @param authorities 权限区分字段(详见:UsersElastic)
     * @return 数量
     */
    fun countByAuthorities(authorities: Int): Long

    /**
     * 通过权限id统计
     *
     * @param authorities 权限区分字段(详见:UsersElastic)
     * @return 数量
     */
    fun countByAuthoritiesNotIn(authorities: Collection<Int>): Long

    /**
     * 通过角色名模糊查询
     *
     * @param roleName 角色名
     * @return 用户
     */
    fun findByRoleNameLike(roleName: String): List<UsersElastic>
}