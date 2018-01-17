package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2018-01-17 .
 **/
interface GraduationDesignTeacherService {
    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findById(id: String): GraduationDesignTeacher

    /**
     * 通过毕业设计发布id与教职工id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param staffId                   教职工id
     * @return 数据
     */
    fun findByGraduationDesignReleaseIdAndStaffId(graduationDesignReleaseId: String, staffId: Int): Optional<Record>

    /**
     * 根据毕业设计发布id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): List<GraduationDesignTeacher>

    /**
     * 根据毕业设计发布id关联查询 教职工
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    fun findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId: String): List<GraduationDesignTeacherBean>

    /**
     * 根据毕业设计发布id删除
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     */
    fun deleteByGraduationDesignReleaseId(graduationDesignReleaseId: String)

    /**
     * 保存
     *
     * @param graduationDesignTeacher 数据
     */
    fun save(graduationDesignTeacher: GraduationDesignTeacher)

    /**
     * 更新
     *
     * @param graduationDesignTeacher 数据
     */
    fun update(graduationDesignTeacher: GraduationDesignTeacher)

    /**
     * 批量更新
     *
     * @param graduationDesignTeachers 数据
     */
    fun update(graduationDesignTeachers: List<GraduationDesignTeacher>)

    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignTeacherBean>, graduationDesignTeacherBean: GraduationDesignTeacherBean): List<GraduationDesignTeacherBean>

    /**
     * 数据 总数
     *
     * @return 总数
     */
    fun countAll(graduationDesignTeacherBean: GraduationDesignTeacherBean): Int

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignTeacherBean>, graduationDesignTeacherBean: GraduationDesignTeacherBean): Int
}