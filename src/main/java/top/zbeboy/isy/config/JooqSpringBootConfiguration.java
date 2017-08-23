package top.zbeboy.isy.config;

import org.jooq.*;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import top.zbeboy.isy.exception.ExceptionTranslator;

import javax.sql.DataSource;

/**
 * @author zbeboy
 */
@Configuration
public class JooqSpringBootConfiguration {

    @Autowired
    private ISYProperties isyProperties;

    /**
     * datasource transaction
     *
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * use jooq config
     *
     * @param config
     * @return
     */
    @Bean
    public DSLContext dsl(org.jooq.Configuration config) {
        return new DefaultDSLContext(config);
    }

    @Bean
    public ConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    /**
     * own transaction
     *
     * @return
     */
    @Bean
    public TransactionProvider transactionProvider() {
        return new SpringTransactionProvider();
    }

    /**
     * exception transaction.
     *
     * @return
     */
    @Bean
    public ExceptionTranslator exceptionTranslator() {
        return new ExceptionTranslator();
    }

    /**
     * listener
     *
     * @param exceptionTranslator
     * @return
     */
    @Bean
    public ExecuteListenerProvider executeListenerProvider(ExceptionTranslator exceptionTranslator) {
        return new DefaultExecuteListenerProvider(exceptionTranslator);
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
    public org.jooq.Configuration jooqConfig(ConnectionProvider connectionProvider,
                                             TransactionProvider transactionProvider, ExecuteListenerProvider executeListenerProvider) {

        return new DefaultConfiguration()
                .derive(connectionProvider)
                .derive(transactionProvider)
                .derive(executeListenerProvider)
                .derive(SQLDialect.valueOf(this.isyProperties.getConstants().getJooqSqlDialect().toUpperCase()));
    }
}
