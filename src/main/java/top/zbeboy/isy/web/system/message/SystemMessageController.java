package top.zbeboy.isy.web.system.message;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.system.SystemMessageService;
import top.zbeboy.isy.service.platform.UsersService;
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
@Controller
public class SystemMessageController {

    private final Logger log = LoggerFactory.getLogger(SystemMessageController.class);

    @Resource
    private SystemMessageService systemMessageService;

    @Resource
    private UsersService usersService;

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
        Optional<Record> record = systemMessageService.findByIdAndAcceptUsersRelation(messageId,users.getUsername());
        if (record.isPresent()) {
            systemMessageBean = record.get().into(SystemMessageBean.class);
            systemMessageBean.setMessageDateStr(DateTimeUtils.formatDate(systemMessageBean.getMessageDate(), "yyyy年MM月dd日 hh:mm:ss"));
            Byte b = 1;
            systemMessageBean.setIsSee(b);
            systemMessageService.update(systemMessageBean);
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
