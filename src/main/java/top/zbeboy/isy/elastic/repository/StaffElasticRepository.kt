package top.zbeboy.isy.elastic.repository

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import top.zbeboy.isy.elastic.pojo.StaffElastic

/**
 * Created by zbeboy 2017-12-09 .
 **/
interface StaffElasticRepository : ElasticsearchRepository<StaffElastic, String> {
    /**
     * 通过账号查询
     *
     * @param username 账号
     * @return 教职工
     */
    fun findByUsername(username: String): StaffElastic

    /**
     * 根据账号删除
     *
     * @param username 账号
     */
    fun deleteByUsername(username: String)

    /**
     * 通过权限id统计
     *
     * @param authorities 权限区分字段(详见:UsersElastic)
     * @return 数量
     */
    fun countByAuthorities(authorities: Int): Long

    /**
     * 通过权限id和院id统计
     *
     * @param authorities 权限区分字段(详见:UsersElastic)
     * @param collegeId   院id
     * @return 数量
     */
    fun countByAuthoritiesAndCollegeId(authorities: Int, collegeId: Int): Long

    /**
     * 通过权限id,系id和用户账号统计，注不等于用户账号
     *
     * @param authorities  权限区分字段(详见:UsersElastic)
     * @param departmentId 系id
     * @param username     用户账号
     * @return 数量
     */
    fun countByAuthoritiesAndDepartmentIdAndUsernameNot(authorities: Int, departmentId: Int, username: String): Long

    /**
     * 通过权限id统计
     *
     * @param authorities 权限区分字段(详见:UsersElastic)
     * @return 数量
     */
    fun countByAuthoritiesNotIn(authorities: Collection<Int>): Long

    /**
     * 通过院id与角色名模糊查询
     *
     * @param collegeId 院id
     * @param roleName  角色名
     * @return 教职工
     */
    fun findByCollegeIdAndRoleNameLike(collegeId: Int, roleName: String): List<StaffElastic>
}