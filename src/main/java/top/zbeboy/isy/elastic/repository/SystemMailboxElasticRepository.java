package top.zbeboy.isy.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.zbeboy.isy.elastic.pojo.SystemMailboxElastic;

/**
 * Created by lenovo on 2017-04-08.
 */
public interface SystemMailboxElasticRepository extends ElasticsearchRepository<SystemMailboxElastic, String> {
}
