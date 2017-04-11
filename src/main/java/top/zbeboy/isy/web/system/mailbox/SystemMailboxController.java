package top.zbeboy.isy.web.system.mailbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.glue.system.SystemMailboxGlue;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.web.bean.system.mailbox.SystemMailboxBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016-09-17.
 * 系统邮件模块
 */
@Controller
@RequestMapping("/web")
public class SystemMailboxController {

    private final Logger log = LoggerFactory.getLogger(SystemMailboxController.class);

    @Resource
    private SystemMailboxGlue systemMailboxGlue;

    /**
     * 系统邮件
     *
     * @return 系统邮件页面
     */
    @RequestMapping("/menu/system/mailbox")
    public String systemMailbox() {
        return "web/system/mailbox/system_mailbox::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/system/mailbox/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<SystemMailboxBean> systemMailboxes(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("accept_mail");
        headers.add("send_time");
        headers.add("send_condition");
        DataTablesUtils<SystemMailboxBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        ResultUtils<List<SystemMailboxBean>> resultUtils = systemMailboxGlue.findAllByPage(dataTablesUtils);
        dataTablesUtils.setData(resultUtils.getData());
        dataTablesUtils.setiTotalRecords(systemMailboxGlue.countAll());
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements());
        return dataTablesUtils;
    }
}
