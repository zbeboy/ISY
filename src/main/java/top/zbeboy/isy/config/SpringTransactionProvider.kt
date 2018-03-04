package top.zbeboy.isy.config

import org.jooq.TransactionContext
import org.jooq.TransactionProvider
import org.jooq.tools.JooqLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.TransactionDefinition.PROPAGATION_NESTED
import org.springframework.transaction.support.DefaultTransactionDefinition

/**
 * Created by zbeboy 2017-10-30 .
 **/
class SpringTransactionProvider : TransactionProvider {

    private val log = JooqLogger.getLogger(SpringTransactionProvider::class.java)

    @Autowired
    lateinit var txMgr: DataSourceTransactionManager

    override fun begin(ctx: TransactionContext) {
        log.info("Begin transaction")

        // This TransactionProvider behaves like jOOQ's DefaultTransactionProvider,
        // which supports nested transactions using Savepoints
        val tx = this.txMgr.getTransaction(DefaultTransactionDefinition(PROPAGATION_NESTED))
        ctx.transaction(SpringTransaction(tx))
    }

    override fun commit(ctx: TransactionContext) {
        log.info("commit transaction")

        this.txMgr.commit((ctx.transaction() as SpringTransaction).tx)
    }

    override fun rollback(ctx: TransactionContext) {
        log.info("rollback transaction")

        this.txMgr.rollback((ctx.transaction() as SpringTransaction).tx)
    }
}