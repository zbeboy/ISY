package top.zbeboy.isy.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 配置高性能数据库链接池
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class DatasourceConfiguration {
    /**
     * HarkiCp datasource
     *
     * @return datasource
     */
    @Bean
    @ConfigurationProperties(prefix = "datasource.mine")
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}
