package top.zbeboy.isy.web.system.health

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Created by zbeboy 2017-11-11 .
 **/
@Controller
@RequestMapping("/web")
open class SystemHealthController {

    /**
     * 系统状况
     *
     * @return 系统状况页面
     */
    @RequestMapping("/menu/system/health")
    fun systemLog(): String {
        return "web/system/health/system_health::#page-wrapper"
    }
}