package top.zbeboy.isy.web.system.alert;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.system.SystemAlertService;
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by lenovo on 2016-12-30.
 */
@Slf4j
@Controller
public class SystemAlertController {

    @Resource
    private UsersService usersService;

    @Resource
    private SystemAlertService systemAlertService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

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
     * @param systemAlertId 提醒id
     * @return 转发页
     */
    @RequestMapping(value = "/anyone/alert/detail", method = RequestMethod.GET)
    public String alertDetail(@RequestParam("id") String systemAlertId, ModelMap modelMap) {
        String page;
        Users users = usersService.getUserFromSession();
        Optional<Record> record = systemAlertService.findByUsernameAndId(users.getUsername(), systemAlertId);
        if (record.isPresent()) {
            SystemAlertBean systemAlertBean = record.get().into(SystemAlertBean.class);
            if (Objects.equals(systemAlertBean.getName(), Workbook.ALERT_MESSAGE_TYPE)) {
                page = "redirect:/anyone/message/detail?id=" + systemAlertBean.getLinkId();
            } else {
                page = commonControllerMethodService.showTip(modelMap, "未查询到相关类型提醒");
            }
        } else {
            page = commonControllerMethodService.showTip(modelMap, "未查询到相关提醒");
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
        AjaxUtils<SystemAlertBean> ajaxUtils = AjaxUtils.of();
        SystemAlertBean systemAlertBean = new SystemAlertBean();
        Users users = usersService.getUserFromSession();
        systemAlertBean.setUsername(users.getUsername());
        Result<Record> records = systemAlertService.findAllByPage(paginationUtils, systemAlertBean);
        List<SystemAlertBean> systemAlertBeans = systemAlertService.dealData(paginationUtils, records, systemAlertBean);
        return ajaxUtils.success().msg("获取数据成功").listData(systemAlertBeans).paginationUtils(paginationUtils);
    }
}
