package top.zbeboy.isy.service.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.config.Workbook
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-11-30 .
 **/
@Component
open class RequestUtils {

    companion object {

        /**
         * 根据开发环境不同取不同路径
         *
         * @param request 请求
         * @return 路径
         */
        @JvmStatic
        fun getBaseUrl(request: HttpServletRequest): String {
            return request.scheme + "://" + request.serverName + ":" + request.serverPort + request.contextPath
        }

        /**
         * 获取客服端ip地址
         *
         * @param request 请求
         * @return ip
         */
        @JvmStatic
        fun getIpAddress(request: HttpServletRequest): String? {
            var ip: String? = request.getHeader("x-forwarded-for")
            if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.getHeader("Proxy-Client-IP")
            }
            if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.getHeader("WL-Proxy-Client-IP")
            }
            if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
                ip = request.remoteAddr
            }
            return ip
        }

        /**
         * 获取realPath
         *
         * @param request 请求
         * @return real path.
         */
        @JvmStatic
        fun getRealPath(request: HttpServletRequest): String {
            return request.session.servletContext.getRealPath(Workbook.DIRECTORY_SPLIT) + Workbook.DIRECTORY_SPLIT
        }
    }
}