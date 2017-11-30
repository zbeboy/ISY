package top.zbeboy.isy.web.system.log

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.glue.system.SystemLogGlue
import top.zbeboy.isy.web.bean.system.log.SystemLogBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.ArrayList
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-11-11 .
 **/
@Controller
@RequestMapping("/web")
open class SystemLogController {

    @Resource
    open lateinit var systemLogGlue: SystemLogGlue

    /**
     * 系统日志
     *
     * @return 系统日志页面
     */
    @RequestMapping("/menu/system/log")
    fun systemLog(): String {
        return "web/system/log/system_log::#page-wrapper"
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = ["/system/log/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun systemLogs(request: HttpServletRequest): DataTablesUtils<SystemLogBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("username")
        headers.add("behavior")
        headers.add("operating_time")
        headers.add("ip_address")
        val dataTablesUtils = DataTablesUtils<SystemLogBean>(request, headers)
        val resultUtils = systemLogGlue.findAllByPage(dataTablesUtils)
        dataTablesUtils.data = resultUtils.getData()
        dataTablesUtils.setiTotalRecords(systemLogGlue.countAll())
        dataTablesUtils.setiTotalDisplayRecords(resultUtils.getTotalElements())
        return dataTablesUtils
    }
}