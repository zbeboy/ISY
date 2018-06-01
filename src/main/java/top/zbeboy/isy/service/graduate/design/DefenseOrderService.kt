package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.DefenseOrder
import top.zbeboy.isy.domain.tables.records.DefenseOrderRecord
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseOrderBean

/**
 * Created by zbeboy 2018-02-06 .
 **/
interface DefenseOrderService {
    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 用户
     */
    fun findById(id: String): DefenseOrder

    /**
     * 通过组id查询
     *
     * @param defenseGroupId 组id
     * @return 组数据
     */
    fun findByDefenseGroupId(defenseGroupId: String): List<DefenseOrder>

    /**
     * 查询数据
     *
     * @param condition 查询条件
     * @return 数据
     */
    fun findAll(condition: DefenseOrderBean): Result<Record>

    /**
     * 通过序号与组id查询
     *
     * @param sortNum        序号
     * @param defenseGroupId 组id
     * @return 数据
     */
    fun findBySortNumAndDefenseGroupId(sortNum: Int, defenseGroupId: String): DefenseOrderRecord

    /**
     * 保存
     *
     * @param defenseOrders 顺序
     */
    fun save(defenseOrders: List<DefenseOrder>)

    /**
     * 通过组id删除
     *
     * @param defenseGroupId 组id
     */
    fun deleteByDefenseGroupId(defenseGroupId: String)

    /**
     * 更新
     *
     * @param defenseOrder 数据
     */
    fun update(defenseOrder: DefenseOrder)
}