package top.zbeboy.isy.elastic.pojo

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import java.sql.Timestamp

/**
 * Created by zbeboy 2017-11-11 .
 **/
@Document(indexName = "systemmailbox", type = "systemmailbox", shards = 1, replicas = 0, refreshInterval = "-1")
open class SystemMailboxElastic {
    @Id
    var systemMailboxId: String? = null
    var sendTime: Timestamp? = null
    var acceptMail: String? = null
    var sendCondition: String? = null

    constructor()

    constructor(systemMailboxId: String?, sendTime: Timestamp?, acceptMail: String?, sendCondition: String?) {
        this.systemMailboxId = systemMailboxId
        this.sendTime = sendTime
        this.acceptMail = acceptMail
        this.sendCondition = sendCondition
    }
}