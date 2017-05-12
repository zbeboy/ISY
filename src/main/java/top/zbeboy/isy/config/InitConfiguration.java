package top.zbeboy.isy.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

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
