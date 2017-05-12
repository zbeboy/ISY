package top.zbeboy.isy.web;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import top.zbeboy.isy.service.system.SystemAlertService;
import top.zbeboy.isy.service.system.SystemMessageService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean;
import top.zbeboy.isy.web.bean.system.message.SystemMessageBean;
import top.zbeboy.isy.web.util.AjaxUtils;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016-12-24.
 */
@Controller
public class MessageController {

    private final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Resource
    private SystemAlertService systemAlertService;

    @Resource
    private SystemMessageService systemMessageService;

    /**
     * 推送到一个用户 的系统信息
     *
     * @param principal 用户账号
     * @return 消息
     * @throws InterruptedException 异常
     */
    @MessageMapping("/remind")
    @SendToUser(destinations = "/topic/reminds", broadcast = false)
    public AjaxUtils reminds(Principal principal) throws InterruptedException {
        Thread.sleep(3000);
        String username = principal.getName();
        Map<String, Object> data = new HashMap<>();
        int pageNum = 1;
        int pageSize = 5;

        // 提醒
        List<SystemAlertBean> systemAlertBeens = new ArrayList<>();
        Result<Record> systemAlertRecord = systemAlertService.findAllByPageForShow(pageNum, pageSize, username, false);
        if (systemAlertRecord.isNotEmpty()) {
            systemAlertBeens = systemAlertRecord.into(SystemAlertBean.class);
            systemAlertBeens.forEach(i ->
                    i.setAlertDateStr(DateTimeUtils.formatDate(i.getAlertDate(), "yyyyMMddHHmmss"))
            );
        }
        data.put("alerts", systemAlertBeens);
        data.put("alertsCount", systemAlertService.countAllForShow(username, false));

        // 消息
        List<SystemMessageBean> systemMessageBeens = new ArrayList<>();
        Result<Record> systemMessageRecord = systemMessageService.findAllByPageForShow(pageNum, pageSize, username, false);
        if (systemMessageRecord.isNotEmpty()) {
            systemMessageBeens = systemMessageRecord.into(SystemMessageBean.class);
            systemMessageBeens.forEach(i ->
                    i.setMessageDateStr(DateTimeUtils.formatDate(i.getMessageDate(), "yyyyMMddHHmmss"))
            );
        }
        data.put("messages", systemMessageBeens);
        data.put("messagesCount", systemMessageService.countAllForShow(username, false));
        return AjaxUtils.of().success().msg("获取数据成功").mapData(data);
    }
}
