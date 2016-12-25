package top.zbeboy.isy.web;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.SystemAlertService;
import top.zbeboy.isy.service.UsersService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
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

    @MessageMapping("/remind")
    @SendTo("/topic/reminds")
    public AjaxUtils reminds(String username) throws InterruptedException {
        Thread.sleep(3000);
        Map<String,Object> data = new HashMap<>();
        int pageNum = 1;
        int pageSize = 5;
        List<SystemAlertBean> systemAlertBeens = new ArrayList<>();
        Result<Record> systemAlertRecord = systemAlertService.findAllByPageForShow(pageNum,pageSize,username,false);
        if(systemAlertRecord.isNotEmpty()){
            systemAlertBeens = systemAlertRecord.into(SystemAlertBean.class);
            systemAlertBeens.forEach(i->{
                i.setAlertDateStr(DateTimeUtils.formatDate(i.getAlertDate(),"yyyyMMddhhmmss"));
            });
        }
        data.put("alerts",systemAlertBeens);
        data.put("alertsCount",systemAlertService.countAllForShow(username,false));
        return new AjaxUtils().success().msg("获取数据成功").mapData(data);
    }
}
