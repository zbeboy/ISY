package top.zbeboy.isy.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.zbeboy.isy.elastic.pojo.SystemLogElastic;

import java.sql.Timestamp;

/**
 * Created by lenovo on 2017-03-27.
 */
public interface SystemLogElasticRepository extends ElasticsearchRepository<SystemLogElastic, String> {
    void deleteByOperatingTimeLessThanEqual(long operatingTime);
}
