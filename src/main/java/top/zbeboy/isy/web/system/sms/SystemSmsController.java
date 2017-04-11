package top.zbeboy.isy.web.system.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.glue.system.SystemSmsGlue;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.web.bean.system.sms.SystemSmsBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016-09-17.
 * 系统短信模块
 */
@Controller
@RequestMapping("/web")
public class SystemSmsController {

    private final Logger log = LoggerFactory.getLogger(SystemSmsController.class);

    @Resource
    private SystemSmsGlue systemSmsGlue;

    /**
     * 系统短信
     *
     * @return 系统短信页面
     */
    @RequestMapping("/menu/system/sms")
    public String systemSms() {
        return "web/system/sms/system_sms::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/system/sms/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<SystemSmsBean> systemSmses(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("accept_phone");
        headers.add("send_time");
        headers.add("send_condition");
        DataTablesUtils<SystemSmsBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        ResultUtils<List<SystemSmsBean>> resultUtils = systemSmsGlue.findAllByPage(dataTablesUtils);
        dataTablesUtils.setData(resultUtils.getData());
        dataTablesUtils.setiTotalRecords(systemSmsGlue.countAll());
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements());
        return dataTablesUtils;
    }
}
