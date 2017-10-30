package top.zbeboy.isy.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * 配置高性能数据库链接池
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
@Configuration
open class DatasourceConfiguration {

    /**
     * HarkiCp datasource
     *
     * @return datasource
     */
    @Bean
    @ConfigurationProperties(prefix = "datasource.mine")
    open fun dataSource(): DataSource {
        return DataSourceBuilder.create()
                .type(HikariDataSource::class.java).build()
    }
}