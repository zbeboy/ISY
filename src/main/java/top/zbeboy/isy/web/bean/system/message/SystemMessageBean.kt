package top.zbeboy.isy.web.bean.system.message

import top.zbeboy.isy.domain.tables.pojos.SystemMessage

/**
 * Created by zbeboy 2017-11-03 .
 **/
class SystemMessageBean : SystemMessage() {

    var messageDateStr: String? = null

    /*
    一般用于发件人姓名
     */
    var realName: String? = null
}