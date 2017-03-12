package top.zbeboy.isy.web.system.mailbox;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.service.system.SystemMailboxService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.system.mailbox.SystemMailboxBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
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
    private SystemMailboxService systemMailboxService;

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
        Result<Record> records = systemMailboxService.findAllByPage(dataTablesUtils);
        List<SystemMailboxBean> systemMailboxes = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            systemMailboxes = records.into(SystemMailboxBean.class);
            systemMailboxes.forEach(s -> {
                Date date = DateTimeUtils.timestampToDate(s.getSendTime());
                s.setSendTimeNew(DateTimeUtils.formatDate(date));
            });
        }
        dataTablesUtils.setData(systemMailboxes);
        dataTablesUtils.setiTotalRecords(systemMailboxService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(systemMailboxService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }
}
