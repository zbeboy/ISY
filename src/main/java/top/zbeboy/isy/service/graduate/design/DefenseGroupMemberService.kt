package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember
import top.zbeboy.isy.domain.tables.records.DefenseGroupMemberRecord
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupMemberBean
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean
import java.util.*

/**
 * Created by zbeboy 2018-02-06 .
 **/
interface DefenseGroupMemberService {
    /**
     * 查询指导教师
     *
     * @param condition 查询条件
     * @return 数据
     */
    fun findByGraduationDesignReleaseIdRelationForStaff(condition: GraduationDesignTeacherBean): List<GraduationDesignTeacherBean>

    /**
     * 通过毕业设计指导教师id查询
     *
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @return 数据
     */
    fun findByGraduationDesignTeacherId(graduationDesignTeacherId: String): DefenseGroupMemberRecord

    /**
     * 通过组id与毕业设计发布id查询
     *
     * @param defenseGroupId            组id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    fun findByDefenseGroupIdAndGraduationDesignReleaseIdForStudent(defenseGroupId: String, graduationDesignReleaseId: String): List<DefenseGroupMemberBean>

    /**
     * 通过组id查询
     *
     * @param defenseGroupId 组id
     * @return 数据
     */
    fun findByDefenseGroupIdForStaff(defenseGroupId: String): List<DefenseGroupMemberBean>

    /**
     * 通过组id与毕业指导教师id查找
     *
     * @param defenseGroupId            组id
     * @param graduationDesignTeacherId 指导教师id
     * @return 数据
     */
    fun findByDefenseGroupIdAndGraduationDesignTeacherId(defenseGroupId: String, graduationDesignTeacherId: String): Optional<Record>

    /**
     * 通过组id删除
     *
     * @param defenseGroupId 组id
     */
    fun deleteByDefenseGroupId(defenseGroupId: String)

    /**
     * 保存
     *
     * @param defenseGroupMember 组成员
     */
    fun saveOrUpdate(defenseGroupMember: DefenseGroupMember)
}