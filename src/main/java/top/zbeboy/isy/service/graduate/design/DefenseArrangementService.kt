package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import top.zbeboy.isy.domain.tables.pojos.DefenseArrangement
import java.util.*

/**
 * Created by zbeboy 2018-02-06 .
 **/
interface DefenseArrangementService {
    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findById(id: String): DefenseArrangement

    /**
     * 通过毕业设计发布id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): Optional<Record>

    /**
     * 保存
     *
     * @param defenseArrangement 数据
     */
    fun save(defenseArrangement: DefenseArrangement)

    /**
     * 更新
     *
     * @param defenseArrangement 数据
     */
    fun update(defenseArrangement: DefenseArrangement)
}