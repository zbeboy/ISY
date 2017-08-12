package top.zbeboy.isy.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.zbeboy.isy.elastic.pojo.OrganizeElastic;

import java.util.List;

/**
 * Created by lenovo on 2017-04-09.
 */
public interface OrganizeElasticRepository extends ElasticsearchRepository<OrganizeElastic, String> {
    /**
     * 通过院id统计
     *
     * @param collegeId 院id
     * @return 数量
     */
    long countByCollegeId(int collegeId);

    /**
     * 根据学校id查询
     *
     * @param schoolId 学校id
     * @return 班级
     */
    List<OrganizeElastic> findBySchoolId(int schoolId);

    /**
     * 根据院id查询
     *
     * @param collegeId 院id
     * @return 班级
     */
    List<OrganizeElastic> findByCollegeId(int collegeId);

    /**
     * 根据系id查询
     *
     * @param departmentId 系id
     * @return 班级
     */
    List<OrganizeElastic> findByDepartmentId(int departmentId);

    /**
     * 根据专业id查询
     *
     * @param scienceId 专业id
     * @return 班级
     */
    List<OrganizeElastic> findByScienceId(int scienceId);
}
