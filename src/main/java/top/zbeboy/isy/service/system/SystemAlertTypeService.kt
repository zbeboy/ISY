package top.zbeboy.isy.service.system

import top.zbeboy.isy.domain.tables.pojos.SystemAlertType

/**
 * Created by zbeboy 2017-11-07 .
 **/
interface SystemAlertTypeService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 消息类型
     */
    fun findById(id: Int): SystemAlertType

    /**
     * 通过类型名查询
     *
     * @param type 类型名
     * @return 消息类型
     */
    fun findByType(type: String): SystemAlertType
}