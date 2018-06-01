package top.zbeboy.isy.service.graduate.design

import top.zbeboy.isy.domain.tables.pojos.DefenseRate
import top.zbeboy.isy.domain.tables.records.DefenseRateRecord
import top.zbeboy.isy.web.bean.graduate.design.reorder.DefenseRateBean

/**
 * Created by zbeboy 2018-02-06 .
 **/
interface DefenseRateService {
    /**
     * 通过顺序id与毕业设计指导教师id查询
     *
     * @param defenseOrderId            毕业设计顺序id
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @return 数据
     */
    fun findByDefenseOrderIdAndGraduationDesignTeacherId(defenseOrderId: String, graduationDesignTeacherId: String): DefenseRateRecord

    /**
     * 保存
     *
     * @param defenseRate 数据
     */
    fun saveOrUpdate(defenseRate: DefenseRate)

    /**
     * 通过毕业答辩顺序id与毕业答辩组id查询教师打分信息
     *
     * @param defenseOrderId 毕业答辩顺序id
     * @param defenseGroupId 毕业答辩组id
     * @return 数据
     */
    fun findByDefenseOrderIdAndDefenseGroupId(defenseOrderId: String, defenseGroupId: String): List<DefenseRateBean>

    /**
     * 通过答辩顺序id删除
     *
     * @param defenseOrderId 答辩顺序id
     */
    fun deleteByDefenseOrderId(defenseOrderId: String)
}