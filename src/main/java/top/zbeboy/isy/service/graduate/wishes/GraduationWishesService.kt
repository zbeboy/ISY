package top.zbeboy.isy.service.graduate.wishes

import top.zbeboy.isy.elastic.pojo.GraduationWishesElastic

/**
 * Created by zbeboy 2018-05-23 .
 **/
interface GraduationWishesService {

    /**
     * 查询全部
     */
    fun findAll(): MutableIterable<GraduationWishesElastic>?

    /**
     * 保存
     *
     * @param graduationWishesElastic 数据
     */
    fun save(graduationWishesElastic: GraduationWishesElastic)
}