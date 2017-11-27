package top.zbeboy.isy.web.system.mailbox

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.glue.system.SystemMailboxGlue
import top.zbeboy.isy.web.bean.system.mailbox.SystemMailboxBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.ArrayList
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-11-11 .
 **/
@Controller
@RequestMapping("/web")
open class SystemMailboxController {

    @Resource
    open lateinit var systemMailboxGlue: SystemMailboxGlue

    /**
     * 系统邮件
     *
     * @return 系统邮件页面
     */
    @RequestMapping("/menu/system/mailbox")
    fun systemMailbox(): String {
        return "web/system/mailbox/system_mailbox::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/system/mailbox/data", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun systemMailboxes(request: HttpServletRequest): DataTablesUtils<SystemMailboxBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("accept_mail")
        headers.add("send_time")
        headers.add("send_condition")
        val dataTablesUtils = DataTablesUtils<SystemMailboxBean>(request, headers)
        val resultUtils = systemMailboxGlue.findAllByPage(dataTablesUtils)
        dataTablesUtils.data = resultUtils.getData()
        dataTablesUtils.setiTotalRecords(systemMailboxGlue.countAll())
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements())
        return dataTablesUtils
    }
}