package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumType;

import java.util.List;

/**
 * Created by zbeboy on 2017/6/23.
 */
public interface GraduationDesignDatumTypeService {
    /**
     * 查询全部类型
     *
     * @return 全部
     */
    List<GraduationDesignDatumType> findAll();
}
