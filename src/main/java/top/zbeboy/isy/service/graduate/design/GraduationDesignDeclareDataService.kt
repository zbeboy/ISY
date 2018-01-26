package top.zbeboy.isy.service.graduate.design

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclareData

/**
 * Created by zbeboy 2018-01-26 .
 **/
interface GraduationDesignDeclareDataService {
    /**
     * 根据毕业设计发布id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): GraduationDesignDeclareData?

    /**
     * 根据毕业设计发布id删除
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     */
    fun deleteByGraduationDesignReleaseId(graduationDesignReleaseId: String)

    /**
     * 保存
     *
     * @param graduationDesignDeclareData 数据
     */
    fun save(graduationDesignDeclareData: GraduationDesignDeclareData)
}