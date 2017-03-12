package top.zbeboy.isy.web.system.alert;

import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.SystemAlert;
import top.zbeboy.isy.domain.tables.pojos.SystemAlertType;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.system.SystemAlertService;
import top.zbeboy.isy.service.system.SystemAlertTypeService;
import top.zbeboy.isy.service.system.SystemMessageService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-12-30.
 */
@Controller
public class SystemAlertController {

    private final Logger log = LoggerFactory.getLogger(SystemAlertController.class);

    @Resource
    private UsersService usersService;

    @Resource
    private SystemAlertService systemAlertService;

    @Resource
    private SystemAlertTypeService systemAlertTypeService;

    @Resource
    private SystemMessageService systemMessageService;

    /**
     * 系统提醒数据
     *
     * @return 系统数据页面
     */
    @RequestMapping(value = "/anyone/alert", method = RequestMethod.GET)
    public String alert() {
        return "web/system/alert/system_alert::#page-wrapper";
    }

    /**
     * 提醒详情
     *
     * @param linkId 链接id
     * @param type   类型
     * @return 转发页
     */
    @RequestMapping(value = "/anyone/alert/detail", method = RequestMethod.GET)
    public String alertDetail(@RequestParam("id") String linkId, @RequestParam("type") int type) {
        String page = "";
        Users users = usersService.getUserFromSession();
        Optional<Record> record = systemAlertService.findByUsernameAndLinkId(users.getUsername(),linkId);
        if(record.isPresent()){
            SystemAlertType systemAlertType = systemAlertTypeService.findById(type);
            if (!ObjectUtils.isEmpty(systemAlertType)) {
                if (systemAlertType.getName().equals(Workbook.ALERT_MESSAGE_TYPE)) {
                    page = "redirect:/anyone/message/detail?id=" + linkId;
                }
            }
            SystemAlert systemAlert = record.get().into(SystemAlert.class);
            Byte b = 1;
            systemAlert.setIsSee(b);
            systemAlertService.update(systemAlert);
        }
        return page;
    }

    /**
     * 获取系统提醒数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/anyone/alert/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<SystemAlertBean> alertDatas(PaginationUtils paginationUtils) {
        SystemAlertBean systemAlertBean = new SystemAlertBean();
        Users users = usersService.getUserFromSession();
        systemAlertBean.setUsername(users.getUsername());
        Result<Record> records = systemAlertService.findAllByPage(paginationUtils, systemAlertBean);
        List<SystemAlertBean> systemAlertBeans = systemAlertService.dealData(paginationUtils, records, systemAlertBean);
        return new AjaxUtils<SystemAlertBean>().success().msg("获取数据成功").listData(systemAlertBeans).paginationUtils(paginationUtils);
    }
}
