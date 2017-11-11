package top.zbeboy.isy.elastic.pojo

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import java.sql.Timestamp

/**
 * Created by zbeboy 2017-11-11 .
 **/
@Document(indexName = "systemsms", type = "systemsms", shards = 1, replicas = 0, refreshInterval = "-1")
open class SystemSmsElastic {
    @Id
    var systemSmsId: String? = null
    var sendTime: Timestamp? = null
    var acceptPhone: String? = null
    var sendCondition: String? = null

    constructor()

    constructor(systemSmsId: String?, sendTime: Timestamp?, acceptPhone: String?, sendCondition: String?) {
        this.systemSmsId = systemSmsId
        this.sendTime = sendTime
        this.acceptPhone = acceptPhone
        this.sendCondition = sendCondition
    }
}