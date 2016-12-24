package top.zbeboy.isy.web;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.SystemAlertService;
import top.zbeboy.isy.service.UsersService;
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016-12-24.
 */
@Controller
public class MessageController {

    private final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Resource
    private UsersService usersService;

    @Resource
    private SystemAlertService systemAlertService;

    @MessageMapping("/alert")
    @SendTo("/topic/alerts")
    public AjaxUtils<SystemAlertBean> alerts(String username) throws InterruptedException {
        Thread.sleep(3000);
        List<SystemAlertBean> systemAlertBeans = new ArrayList<>();
        PaginationUtils paginationUtils = new PaginationUtils();
        if(StringUtils.hasLength(username)){
            SystemAlertBean otherCondition = new SystemAlertBean();
            otherCondition.setUsername(username);
            paginationUtils.setPageNum(0);
            paginationUtils.setPageSize(5);
            Result<Record> records = systemAlertService.findAllByPage(paginationUtils, otherCondition);
            systemAlertBeans = systemAlertService.dealData(paginationUtils, records, otherCondition);
        }
        return new AjaxUtils<SystemAlertBean>().success().msg("获取数据成功").listData(systemAlertBeans).paginationUtils(paginationUtils);
    }
}
