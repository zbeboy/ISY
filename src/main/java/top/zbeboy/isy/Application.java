package top.zbeboy.isy;
/*
 * Copyright 2016 the original isy team.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import top.zbeboy.isy.config.ISYProperties;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({ISYProperties.class})
public class Application {

    private final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
