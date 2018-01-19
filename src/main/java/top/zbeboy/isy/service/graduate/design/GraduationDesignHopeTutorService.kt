package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignHopeTutor

/**
 * Created by zbeboy 2018-01-19 .
 **/
interface GraduationDesignHopeTutorService {
    /**
     * 通过学生id与毕业设计发布id统计
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业设计发id
     * @return 数量
     */
    fun countByStudentIdAndGraduationDesignReleaseId(studentId: Int, graduationDesignReleaseId: String): Int

    /**
     * 根据学生id与毕业设计发布id查询
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    fun findByStudentIdAndGraduationDesignReleaseId(studentId: Int, graduationDesignReleaseId: String): Result<Record>

    /**
     * 根据学生id与毕业设计发布id查询教师信息
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业设计指导教师id
     * @return 教师信息
     */
    fun findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(studentId: Int, graduationDesignReleaseId: String): Result<Record>

    /**
     * 保存
     *
     * @param graduationDesignHopeTutor 数据
     */
    fun save(graduationDesignHopeTutor: GraduationDesignHopeTutor)

    /**
     * 删除
     *
     * @param graduationDesignHopeTutor 数据
     */
    fun delete(graduationDesignHopeTutor: GraduationDesignHopeTutor)
}