package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType;

import java.util.List;

/**
 * Created by zbeboy on 2017/6/13.
 */
public interface GraduationDesignSubjectOriginTypeService {

    /**
     * 查询全部
     *
     * @return 全部
     */
    List<GraduationDesignSubjectOriginType> findAll();
}
