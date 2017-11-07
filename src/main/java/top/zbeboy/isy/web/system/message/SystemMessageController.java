package top.zbeboy.isy.web.system.message;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.SystemAlert;
import top.zbeboy.isy.domain.tables.pojos.SystemAlertType;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.cache.CacheManageService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.system.SystemAlertService;
import top.zbeboy.isy.service.system.SystemMessageService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.system.message.SystemMessageBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-12-29.
 */
@Slf4j
@Controller
public class SystemMessageController {

    @Resource
    private SystemMessageService systemMessageService;

    @Resource
    private UsersService usersService;

    @Resource
    private SystemAlertService systemAlertService;

    @Resource
    private CacheManageService cacheManageService;

    /**
     * 系统消息数据
     *
     * @return 系统数据页面
     */
    @RequestMapping(value = "/anyone/message", method = RequestMethod.GET)
    public String message() {
        return "web/system/message/system_message::#page-wrapper";
    }

    /**
     * 消息详情
     *
     * @param messageId 消息id
     * @param modelMap  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/anyone/message/detail", method = RequestMethod.GET)
    public String messageDetail(@RequestParam("id") String messageId, ModelMap modelMap) {
        SystemMessageBean systemMessageBean = new SystemMessageBean();
        Users users = usersService.getUserFromSession();
        Optional<Record> record = systemMessageService.findByIdAndAcceptUsersRelation(messageId, users.getUsername());
        if (record.isPresent()) {
            systemMessageBean = record.get().into(SystemMessageBean.class);
            systemMessageBean.setMessageDateStr(DateTimeUtils.formatDate(systemMessageBean.getMessageDate(), "yyyy年MM月dd日 hh:mm:ss"));
            Byte b = 1;
            systemMessageBean.setIsSee(b);
            systemMessageService.update(systemMessageBean);
            // 若单独点击消息则需要更新提醒状态
            SystemAlertType systemAlertType = cacheManageService.findBySystemAlertTypeName(Workbook.ALERT_MESSAGE_TYPE);
            if (!ObjectUtils.isEmpty(systemAlertType)) {
                Optional<Record> systemAlertRecord = systemAlertService.findByUsernameAndLinkIdAndSystemAlertTypeId(users.getUsername(), systemMessageBean.getSystemMessageId(), systemAlertType.getSystemAlertTypeId());
                if (systemAlertRecord.isPresent()) {
                    SystemAlert systemAlert = systemAlertRecord.get().into(SystemAlert.class);
                    systemAlert.setIsSee(b);
                    systemAlertService.update(systemAlert);
                }
            }
        }
        modelMap.addAttribute("message", systemMessageBean);

        return "web/system/message/system_message_look::#page-wrapper";
    }

    /**
     * 获取系统消息数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/anyone/message/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<SystemMessageBean> messageDatas(PaginationUtils paginationUtils) {
        AjaxUtils<SystemMessageBean> ajaxUtils = AjaxUtils.of();
        SystemMessageBean systemMessageBean = new SystemMessageBean();
        Users users = usersService.getUserFromSession();
        systemMessageBean.setAcceptUsers(users.getUsername());
        Result<Record> records = systemMessageService.findAllByPage(paginationUtils, systemMessageBean);
        List<SystemMessageBean> systemMessageBeens = systemMessageService.dealData(paginationUtils, records, systemMessageBean);
        return ajaxUtils.success().msg("获取数据成功").listData(systemMessageBeens).paginationUtils(paginationUtils);
    }
}
