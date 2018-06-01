package top.zbeboy.isy.elastic.repository

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import top.zbeboy.isy.elastic.pojo.SystemMailboxElastic

/**
 * Created by zbeboy 2017-11-11 .
 **/
interface SystemMailboxElasticRepository : ElasticsearchRepository<SystemMailboxElastic, String>