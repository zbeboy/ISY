package top.zbeboy.isy.service.data

import org.jooq.Record
import top.zbeboy.isy.domain.tables.pojos.CollegeRole
import top.zbeboy.isy.domain.tables.records.CollegeRoleRecord
import java.util.*

/**
 * Created by zbeboy 2017-12-02 .
 **/
interface CollegeRoleService {
    /**
     * 通过学院Id查询
     *
     * @param collegeId 学院id
     * @return 数据
     */
    fun findByCollegeId(collegeId: Int): List<CollegeRoleRecord>

    /**
     * 通过学院Id和允许代理字段查询
     *
     * @param collegeId  学院id
     * @param allowAgent 允许代理字段
     * @return 数据
     */
    fun findByCollegeIdAndAllowAgent(collegeId: Int, allowAgent: Byte?): List<CollegeRoleRecord>

    /**
     * 通过角色Id查询
     *
     * @param roleId 角色id
     * @return 数据
     */
    fun findByRoleId(roleId: String): Optional<Record>

    /**
     * 保存
     *
     * @param collegeRole 院与角色
     */
    fun save(collegeRole: CollegeRole)

    /**
     * 更新
     *
     * @param collegeRole 院与角色
     */
    fun update(collegeRole: CollegeRole)

    /**
     * 通过角色id删除
     *
     * @param roleId 角色id
     */
    fun deleteByRoleId(roleId: String)
}