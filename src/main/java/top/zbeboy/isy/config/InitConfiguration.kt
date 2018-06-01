package top.zbeboy.isy.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component

/**
 * 初始化配置.
 *
 * @author zbeboy
 * @version 1.1
 * @since 1.0
 */
@Component
class InitConfiguration @Autowired constructor(private val cacheManager: CacheManager) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(InitConfiguration::class.java)

    override fun run(vararg p0: String?) {
        log.info("\n\n" + "=========================================================\n"
                + "Using cache manager: " + this.cacheManager.javaClass.name + "\n"
                + "=========================================================\n\n")
    }
}