package top.zbeboy.isy.web.system.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zhaoyin on 17-8-8.
 */
@Slf4j
@Controller
@RequestMapping("/web")
public class SystemHealthController {

    /**
     * 系统状况
     *
     * @return 系统状况页面
     */
    @RequestMapping("/menu/system/health")
    public String systemLog() {
        return "web/system/health/system_health::#page-wrapper";
    }
}
