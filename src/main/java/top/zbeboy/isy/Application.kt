package top.zbeboy.isy

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import top.zbeboy.isy.config.ISYProperties

/*
 * Copyright 2017 the original isy team.
 */
@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(ISYProperties::class)
open class Application {
    companion object {
        /**
         * web start .
         *
         * @param args others params.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}