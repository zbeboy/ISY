package top.zbeboy.isy.web.system.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.glue.system.SystemLogGlue;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.service.system.SystemLogService;
import top.zbeboy.isy.web.bean.system.log.SystemLogBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016-09-13.
 * 系统日志模块
 */
@Controller
@RequestMapping("/web")
public class SystemLogController {

    private final Logger log = LoggerFactory.getLogger(SystemLogController.class);

    @Resource
    private SystemLogService systemLogService;

    @Resource
    private SystemLogGlue systemLogGlue;

    /**
     * 系统日志
     *
     * @return 系统日志页面
     */
    @RequestMapping("/menu/system/log")
    public String systemLog() {
        return "web/system/log/system_log::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/system/log/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<SystemLogBean> systemLogs(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("username");
        headers.add("behavior");
        headers.add("operating_time");
        headers.add("ip_address");
        DataTablesUtils<SystemLogBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        ResultUtils<List<SystemLogBean>> resultUtils = systemLogGlue.findAllByPage(dataTablesUtils);
        dataTablesUtils.setData(resultUtils.getData());
        dataTablesUtils.setiTotalRecords(systemLogGlue.countAll(dataTablesUtils));
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.isSearch() ? resultUtils.getTotalElements() : systemLogService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }
}
