package top.zbeboy.isy.elastic.repository

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import top.zbeboy.isy.elastic.pojo.GraduationWishesElastic

/**
 * Created by zbeboy 2018-05-23 .
 **/
interface GraduationWishesElasticRepository : ElasticsearchRepository<GraduationWishesElastic, String>