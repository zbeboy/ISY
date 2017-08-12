package top.zbeboy.isy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * 初始化配置.
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Component
public class InitConfiguration implements CommandLineRunner {

    private final CacheManager cacheManager;

    /**
     * 注入cache
     *
     * @param cacheManager
     */
    @Autowired
    public InitConfiguration(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(String... strings) throws Exception {
        log.info("\n\n" + "=========================================================\n"
                + "Using cache manager: " + this.cacheManager.getClass().getName() + "\n"
                + "=========================================================\n\n");
    }
}
