package top.zbeboy.isy.service.graduate.design

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType

/**
 * Created by zbeboy 2018-01-26 .
 **/
interface GraduationDesignSubjectOriginTypeService {
    /**
     * 查询全部
     *
     * @return 全部
     */
    fun findAll(): List<GraduationDesignSubjectOriginType>
}