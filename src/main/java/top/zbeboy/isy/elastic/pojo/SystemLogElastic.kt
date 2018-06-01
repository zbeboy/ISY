package top.zbeboy.isy.elastic.pojo

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import java.sql.Timestamp

/**
 * Created by zbeboy 2017-10-31 .
 **/
@Document(indexName = "systemlog", type = "systemlog", shards = 1, replicas = 0, refreshInterval = "-1")
open class SystemLogElastic {
    @Id
    var systemLogId: String = ""
    var behavior: String = ""
    var operatingTime: Timestamp? = null
    var username: String = ""
    var ipAddress: String = ""

    constructor()

    constructor(systemLogId: String, behavior: String, operatingTime: Timestamp, username: String, ipAddress: String) {
        this.systemLogId = systemLogId
        this.behavior = behavior
        this.operatingTime = operatingTime
        this.username = username
        this.ipAddress = ipAddress
    }
}