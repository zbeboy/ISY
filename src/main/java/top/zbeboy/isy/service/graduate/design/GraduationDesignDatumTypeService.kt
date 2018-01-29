package top.zbeboy.isy.service.graduate.design

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumType

/**
 * Created by zbeboy 2018-01-29 .
 **/
interface GraduationDesignDatumTypeService {
    /**
     * 查询全部类型
     *
     * @return 全部
     */
    fun findAll(): List<GraduationDesignDatumType>
}