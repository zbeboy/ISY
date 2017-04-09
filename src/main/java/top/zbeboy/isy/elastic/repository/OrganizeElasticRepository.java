package top.zbeboy.isy.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.zbeboy.isy.elastic.pojo.OrganizeElastic;

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
}
