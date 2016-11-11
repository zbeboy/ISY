package top.zbeboy.isy.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by zbeboy on 2016/11/11.
 * 配置高性能数据库链接池
 */
@Configuration
public class DatasourceConfiguration {
    @Bean
    @ConfigurationProperties(prefix="datasource.mine")
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}
