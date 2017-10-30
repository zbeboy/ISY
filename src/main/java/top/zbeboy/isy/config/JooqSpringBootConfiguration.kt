package top.zbeboy.isy.config

import org.jooq.*
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.jooq.impl.DefaultExecuteListenerProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import top.zbeboy.isy.exception.ExceptionTranslator
import javax.sql.DataSource

/**
 * Created by zbeboy 2017-10-30 .
 **/
@Configuration
open class JooqSpringBootConfiguration {

    @Autowired
    private val isyProperties: ISYProperties? = null

    /**
     * datasource transaction
     *
     * @param dataSource
     * @return
     */
    @Bean
    open fun transactionManager(@Qualifier("dataSource") dataSource: DataSource): DataSourceTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    /**
     * use jooq config
     *
     * @param config
     * @return
     */
    @Bean
    open fun dsl(@Qualifier("jooqConfig") config: org.jooq.Configuration): DSLContext {
        return DefaultDSLContext(config)
    }

    @Bean
    open fun connectionProvider(@Qualifier("dataSource") dataSource: DataSource): ConnectionProvider {
        return DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))
    }

    /**
     * own transaction
     *
     * @return
     */
    @Bean
    open fun transactionProvider(): TransactionProvider {
        return SpringTransactionProvider()
    }

    /**
     * exception transaction.
     *
     * @return
     */
    @Bean
    open fun exceptionTranslator(): ExceptionTranslator {
        return ExceptionTranslator()
    }

    /**
     * listener
     *
     * @param exceptionTranslator
     * @return
     */
    @Bean
    open fun executeListenerProvider(exceptionTranslator: ExceptionTranslator): ExecuteListenerProvider {
        return DefaultExecuteListenerProvider(exceptionTranslator)
    }

    /**
     * joop config
     *
     * @param connectionProvider
     * @param transactionProvider
     * @param executeListenerProvider
     * @return
     */
    @Bean
    open fun jooqConfig(connectionProvider: ConnectionProvider,
                   transactionProvider: TransactionProvider, executeListenerProvider: ExecuteListenerProvider): org.jooq.Configuration {

        return DefaultConfiguration()
                .derive(connectionProvider)
                .derive(transactionProvider)
                .derive(executeListenerProvider)
                .derive(SQLDialect.valueOf(this.isyProperties!!.constants.jooqSqlDialect.toUpperCase()))
    }
}