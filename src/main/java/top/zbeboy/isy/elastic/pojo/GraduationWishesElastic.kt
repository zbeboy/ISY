package top.zbeboy.isy.elastic.pojo

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

/**
 * Created by zbeboy 2018-05-23 .
 **/
@Document(indexName = "graduation_wishes", type = "graduation_wishes", shards = 1, replicas = 0, refreshInterval = "-1")
class GraduationWishesElastic {
    @Id
    var id: String? = null
    var schoolName: String? = null
    var username: String? = null
    var content: String? = null
}