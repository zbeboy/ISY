package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.DefenseTime

/**
 * Created by zbeboy 2018-02-06 .
 **/
interface DefenseTimeService {
    /**
     * 通过毕业答辩安排id查询
     *
     * @param defenseArrangementId 毕业答辩安排id
     * @return 数据
     */
    fun findByDefenseArrangementId(defenseArrangementId: String): Result<Record>

    /**
     * 保存
     *
     * @param defenseTime 数据
     */
    fun save(defenseTime: DefenseTime)

    /**
     * 通过毕业答辩安排id删除
     *
     * @param defenseArrangementId 毕业答辩安排id
     */
    fun deleteByDefenseArrangementId(defenseArrangementId: String)
}