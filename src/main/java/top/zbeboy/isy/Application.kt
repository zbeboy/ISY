package top.zbeboy.isy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import top.zbeboy.isy.config.ISYProperties

/*
 * Copyright 2017 the original isy team.
 */
@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(ISYProperties::class)
open class Application

/**
 * web start .
 *
 * @param args others params.
 */
fun main(args: Array<String>) {
    /* A fix netty with elasticsearch ,wait spring boot */
    System.setProperty("es.set.netty.runtime.available.processors", "false")
    runApplication<Application>(*args)
}

