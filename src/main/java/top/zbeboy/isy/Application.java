package top.zbeboy.isy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import top.zbeboy.isy.config.ISYProperties;

/*
 * Copyright 2016 the original isy team.
 */
@Slf4j
@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({ISYProperties.class})
public class Application {

    /**
     * web start .
     *
     * @param args others params.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
