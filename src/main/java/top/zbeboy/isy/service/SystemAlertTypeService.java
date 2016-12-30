package top.zbeboy.isy.service;

import top.zbeboy.isy.domain.tables.pojos.SystemAlertType;

/**
 * Created by lenovo on 2016-12-24.
 */
public interface SystemAlertTypeService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 消息类型
     */
    SystemAlertType findById(int id);
}
