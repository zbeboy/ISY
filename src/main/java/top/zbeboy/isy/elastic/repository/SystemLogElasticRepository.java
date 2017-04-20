package top.zbeboy.isy.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.zbeboy.isy.elastic.pojo.SystemLogElastic;

import java.sql.Timestamp;

/**
 * Created by lenovo on 2017-03-27.
 */
public interface SystemLogElasticRepository extends ElasticsearchRepository<SystemLogElastic, String> {
    /**
     * 根据操作时间删除
     *
     * @param operatingTime 操作时间
     */
    void deleteByOperatingTimeLessThanEqual(long operatingTime);
}
