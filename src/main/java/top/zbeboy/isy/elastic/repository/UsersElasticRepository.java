package top.zbeboy.isy.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.zbeboy.isy.elastic.pojo.UsersElastic;

/**
 * Created by lenovo on 2017-04-10.
 */
public interface UsersElasticRepository extends ElasticsearchRepository<UsersElastic, String> {
}
