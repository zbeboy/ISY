package top.zbeboy.isy.elastic.repository

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import top.zbeboy.isy.elastic.pojo.OrganizeElastic

/**
 * Created by zbeboy 2017-12-01 .
 **/
interface OrganizeElasticRepository : ElasticsearchRepository<OrganizeElastic, String> {
    /**
     * 通过院id统计
     *
     * @param collegeId 院id
     * @return 数量
     */
    fun countByCollegeId(collegeId: Int): Long

    /**
     * 根据学校id查询
     *
     * @param schoolId 学校id
     * @return 班级
     */
    fun findBySchoolId(schoolId: Int): List<OrganizeElastic>

    /**
     * 根据院id查询
     *
     * @param collegeId 院id
     * @return 班级
     */
    fun findByCollegeId(collegeId: Int): List<OrganizeElastic>

    /**
     * 根据系id查询
     *
     * @param departmentId 系id
     * @return 班级
     */
    fun findByDepartmentId(departmentId: Int): List<OrganizeElastic>

    /**
     * 根据专业id查询
     *
     * @param scienceId 专业id
     * @return 班级
     */
    fun findByScienceId(scienceId: Int): List<OrganizeElastic>
}