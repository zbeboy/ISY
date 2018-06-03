package top.zbeboy.isy.service.data

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.elastic.repository.SystemLogElasticRepository
import top.zbeboy.isy.elastic.repository.SystemMailboxElasticRepository
import top.zbeboy.isy.elastic.repository.SystemSmsElasticRepository

/**
 * Created by zbeboy 2017-12-12 .
 **/
@Service("elasticSyncService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class ElasticSyncServiceImpl @Autowired constructor(dslContext: DSLContext) : ElasticSyncService {

    private val create: DSLContext = dslContext

    @Autowired
    open lateinit var systemLogElasticRepository: SystemLogElasticRepository

    @Autowired
    open lateinit var systemMailboxElasticRepository: SystemMailboxElasticRepository

    @Autowired
    open lateinit var systemSmsElasticRepository: SystemSmsElasticRepository

    @Async
    override fun cleanSystemLog() {
        systemLogElasticRepository.deleteAll()
    }

    @Async
    override fun cleanSystemMailbox() {
        systemMailboxElasticRepository.deleteAll()
    }

    @Async
    override fun cleanSystemSms() {
        systemSmsElasticRepository.deleteAll()
    }
}