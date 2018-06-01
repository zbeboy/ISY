package top.zbeboy.isy.service.data

import top.zbeboy.isy.domain.tables.pojos.ScoreType

/**
 * Created by zbeboy 2017-12-12 .
 **/
interface ScoreTypeService {
    /**
     * 查询全部类型
     *
     * @return 全部
     */
    fun findAll(): List<ScoreType>
}