package top.zbeboy.isy.web.system.sms

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.glue.system.SystemSmsGlue
import top.zbeboy.isy.web.bean.system.sms.SystemSmsBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-11-11 .
 **/
@Controller
@RequestMapping("/web")
open class SystemSmsController {

    @Resource
    open lateinit var systemSmsGlue: SystemSmsGlue

    /**
     * 系统短信
     *
     * @return 系统短信页面
     */
    @RequestMapping("/menu/system/sms")
    fun systemSms(): String {
        return "web/system/sms/system_sms::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/system/sms/data", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun systemSmses(request: HttpServletRequest): DataTablesUtils<SystemSmsBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("accept_phone")
        headers.add("send_time")
        headers.add("send_condition")
        val dataTablesUtils = DataTablesUtils<SystemSmsBean>(request, headers)
        val resultUtils = systemSmsGlue.findAllByPage(dataTablesUtils)
        dataTablesUtils.data = resultUtils.getData()
        dataTablesUtils.setiTotalRecords(systemSmsGlue.countAll())
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements())
        return dataTablesUtils
    }
}