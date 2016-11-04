package top.zbeboy.isy.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import top.zbeboy.isy.config.ISYProperties;
import top.zbeboy.isy.config.Workbook;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by lenovo on 2016-09-11.
 * 处理请求数据工具类
 */
@Component
public class RequestUtils {

    @Autowired
    private ISYProperties isyProperties;

    @Autowired
    private Environment env;

    /**
     * 获取客服端ip地址
     *
     * @param request 请求
     * @return ip
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 根据开发环境不同取不同路径
     *
     * @param request 请求
     * @return 路径
     */
    public String getBaseUrl(HttpServletRequest request) {
        if (env.acceptsProfiles(Workbook.SPRING_PROFILE_DEVELOPMENT)) {
            return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        } else {
            // 因nginx代理只能在此进行转换下
            return request.getScheme() + "://" + isyProperties.getConstants().getServerName() + request.getContextPath();
        }
    }

    /**
     * 获取realPath
     *
     * @param request 请求
     * @return real path.
     */
    public static String getRealPath(HttpServletRequest request) {
        return request.getSession().getServletContext().getRealPath("/") + "/";
    }
}
