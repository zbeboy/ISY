package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType;

import java.util.List;

/**
 * Created by zbeboy on 2017/6/13.
 */
public interface GraduationDesignSubjectTypeService {

    /**
     * 查询全部
     *
     * @return 全部
     */
    List<GraduationDesignSubjectType> findAll();
}
