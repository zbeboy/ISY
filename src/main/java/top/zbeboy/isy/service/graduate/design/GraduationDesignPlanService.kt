package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan
import java.sql.Timestamp
import java.util.*

/**
 * Created by zbeboy 2018-01-22 .
 **/
interface GraduationDesignPlanService {
    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findById(id: String): GraduationDesignPlan

    /**
     * 通过主键关联查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findByIdRelation(id: String): Optional<Record>

    /**
     * 根据教师id查询
     *
     * @param graduationDesignReleaseId 发布id
     * @param staffId                   教职工id
     * @return 数据
     */
    fun findByGraduationDesignReleaseIdAndStaffIdOrderByAddTime(graduationDesignReleaseId: String, staffId: Int): Result<Record>

    /**
     * 查询最近一条记录
     *
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @param addTime                   添加时间
     * @return 数据
     */
    fun findByGraduationDesignTeacherIdAndLessThanAddTime(graduationDesignTeacherId: String, addTime: Timestamp): Record

    /**
     * 保存
     *
     * @param graduationDesignPlan 数据
     */
    fun save(graduationDesignPlan: GraduationDesignPlan)

    /**
     * 更新
     *
     * @param graduationDesignPlan 数据
     */
    fun update(graduationDesignPlan: GraduationDesignPlan)

    /**
     * 通过主键删除
     *
     * @param id 主键
     */
    fun deleteById(id: List<String>)
}