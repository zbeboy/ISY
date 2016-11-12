package top.zbeboy.isy.service;

import top.zbeboy.isy.domain.tables.pojos.InternshipType;

import java.util.List;

/**
 * Created by lenovo on 2016-11-12.
 */
public interface InternshipTypeService {

    /**
     * 查询全部类型
     *
     * @return 全部类型
     */
    List<InternshipType> findAll();
}
