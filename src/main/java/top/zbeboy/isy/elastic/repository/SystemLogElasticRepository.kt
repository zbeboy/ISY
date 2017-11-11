package top.zbeboy.isy.elastic.repository

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import top.zbeboy.isy.elastic.pojo.SystemLogElastic

/**
 * Created by zbeboy 2017-11-11 .
 **/
interface SystemLogElasticRepository : ElasticsearchRepository<SystemLogElastic, String> {
}