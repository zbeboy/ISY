package top.zbeboy.isy.service;

import top.zbeboy.isy.domain.tables.pojos.ApplicationType;

import java.util.List;

/**
 * Created by lenovo on 2016-10-04.
 */
public interface ApplicationTypeService {

    /**
     * 查询全部类型
     * @return 全部
     */
    List<ApplicationType> findAll();
}
