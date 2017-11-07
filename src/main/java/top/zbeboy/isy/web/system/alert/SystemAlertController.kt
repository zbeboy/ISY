package top.zbeboy.isy.web.system.alert

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.service.common.CommonControllerMethodService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.SystemAlertService
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-07 .
 **/
@Controller
open class SystemAlertController {

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var systemAlertService: SystemAlertService

    @Resource
    open lateinit var commonControllerMethodService: CommonControllerMethodService

    /**
     * 系统提醒数据
     *
     * @return 系统数据页面
     */
    @RequestMapping(value = "/anyone/alert", method = arrayOf(RequestMethod.GET))
    fun alert(): String {
        return "web/system/alert/system_alert::#page-wrapper"
    }

    /**
     * 提醒详情
     *
     * @param systemAlertId 提醒id
     * @return 转发页
     */
    @RequestMapping(value = "/anyone/alert/detail", method = arrayOf(RequestMethod.GET))
    fun alertDetail(@RequestParam("id") systemAlertId: String, modelMap: ModelMap): String {
        val page: String
        val users = usersService.userFromSession
        val record = systemAlertService.findByUsernameAndId(users.username, systemAlertId)
        page = if (record.isPresent) {
            val systemAlertBean = record.get().into(SystemAlertBean::class.java)
            if (systemAlertBean.name == Workbook.ALERT_MESSAGE_TYPE) {
                "redirect:/anyone/message/detail?id=" + systemAlertBean.linkId
            } else {
                commonControllerMethodService.showTip(modelMap, "未查询到相关类型提醒")
            }
        } else {
            commonControllerMethodService.showTip(modelMap, "未查询到相关提醒")
        }
        return page
    }

    /**
     * 获取系统提醒数据
     *
     * @return 数据
     */
    @RequestMapping(value = "/anyone/alert/data", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun alertDatas(paginationUtils: PaginationUtils): AjaxUtils<SystemAlertBean> {
        val ajaxUtils = AjaxUtils.of<SystemAlertBean>()
        val systemAlertBean = SystemAlertBean()
        val users = usersService.userFromSession
        systemAlertBean.username = users.username
        val records = systemAlertService.findAllByPage(paginationUtils, systemAlertBean)
        val systemAlertBeans = systemAlertService.dealData(paginationUtils, records, systemAlertBean)
        return ajaxUtils.success().msg("获取数据成功").listData(systemAlertBeans).paginationUtils(paginationUtils)
    }
}