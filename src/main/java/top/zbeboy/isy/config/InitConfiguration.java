package top.zbeboy.isy.config;

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
@Component
public class InitConfiguration implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(InitConfiguration.class);

    private final CacheManager cacheManager;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public InitConfiguration(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(String... strings) throws Exception {
        logger.info("\n\n" + "=========================================================\n"
                + "Using cache manager: " + this.cacheManager.getClass().getName() + "\n"
                + "=========================================================\n\n");
        initUrlMapping();
    }

    /**
     * 初始化url mapping
     */
    private void initUrlMapping() {
        try {
            String filePath = Workbook.URL_MAPPING_FILE_PATH;
            String resourcesPath = Workbook.SETTINGS_PATH;
            File file = new File(resourcesPath);
            boolean mkdirsSuccess = true;
            if (!file.exists()) {
                mkdirsSuccess = file.mkdirs();
            }
            if (mkdirsSuccess) {
                PrintWriter printWriter = new PrintWriter(filePath);
                final String[] url = {""};
                Map<RequestMappingInfo, HandlerMethod> map = this.handlerMapping.getHandlerMethods();
                map.forEach((key, value) -> {
                    url[0] = key.toString();
                    url[0] = url[0].split(",")[0];
                    int i1 = url[0].indexOf("[") + 1;
                    int i2 = url[0].lastIndexOf("]");
                    url[0] = url[0].substring(i1, i2);
                    printWriter.println(url[0]);
                });
                printWriter.close();
                logger.info("Init url mapping to {} finish!", filePath);
            } else {
                logger.error("Init url mapping error,is create mkdirs fail.");
            }
        } catch (IOException e) {
            logger.error("Init url mapping error : {}", e);
        }
    }
}
