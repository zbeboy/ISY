package top.zbeboy.isy.service;

import top.zbeboy.isy.domain.tables.pojos.Nation;

import java.util.List;

/**
 * Created by lenovo on 2016-10-30.
 */
public interface NationService {

    /**
     * 查询全部民族
     *
     * @return 全部民族
     */
    List<Nation> findAll();
}
