package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.DefenseGroup
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean
import java.util.*

/**
 * Created by zbeboy 2018-02-06 .
 **/
interface DefenseGroupService {
    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 组
     */
    fun findById(id: String): DefenseGroup

    /**
     * 根据主键关联查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findByIdRelation(id: String): Optional<Record>

    /**
     * 通过毕业设计安排id查询
     *
     * @param defenseArrangementId 毕业设计安排id
     * @return 数据
     */
    fun findByDefenseArrangementId(defenseArrangementId: String): List<DefenseGroupBean>

    /**
     * 通过毕业设计发布id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): Result<Record>

    /**
     * 通过毕业设计发布id关联查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    fun findByGraduationDesignReleaseIdRelation(graduationDesignReleaseId: String): List<DefenseGroupBean>

    /**
     * 保存
     *
     * @param defenseGroup 组
     */
    fun save(defenseGroup: DefenseGroup)

    /**
     * 更新
     *
     * @param defenseGroup 组
     */
    fun update(defenseGroup: DefenseGroup)

    /**
     * 通过主键删除
     *
     * @param id 主键
     */
    fun deleteById(id: String)
}