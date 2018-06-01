package top.zbeboy.isy.service.graduate.design

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType

/**
 * Created by zbeboy 2018-01-26 .
 **/
interface GraduationDesignSubjectTypeService {
    /**
     * 查询全部
     *
     * @return 全部
     */
    fun findAll(): List<GraduationDesignSubjectType>
}