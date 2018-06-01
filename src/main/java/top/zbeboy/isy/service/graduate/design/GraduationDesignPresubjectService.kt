package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPresubject
import top.zbeboy.isy.domain.tables.records.GraduationDesignPresubjectRecord
import top.zbeboy.isy.web.bean.graduate.design.subject.GraduationDesignPresubjectBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2018-01-26 .
 **/
interface GraduationDesignPresubjectService {
    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findById(id: String): GraduationDesignPresubject

    /**
     * 通过题目查询
     *
     * @param presubjectTitle 题目
     * @return 数据
     */
    fun findByPresubjectTitle(presubjectTitle: String): List<GraduationDesignPresubject>

    /**
     * 编辑时题目校验 注意:不等于主键
     *
     * @param presubjectTitle              题目
     * @param graduationDesignPresubjectId 主键
     * @return 数据
     */
    fun findByPresubjectTitleNeId(presubjectTitle: String, graduationDesignPresubjectId: String): Result<GraduationDesignPresubjectRecord>

    /**
     * 根据主键关联查询
     *
     * @param id 主键
     * @return 数据
     */
    fun findByIdRelation(id: String): Optional<Record>

    /**
     * 通过学生id与毕业设计发布id查询
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    fun findByStudentIdAndGraduationDesignReleaseId(studentId: Int, graduationDesignReleaseId: String): GraduationDesignPresubjectRecord

    /**
     * 通过毕业设计发布id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): Result<GraduationDesignPresubjectRecord>

    /**
     * 保存
     *
     * @param graduationDesignPresubject 数据
     */
    fun save(graduationDesignPresubject: GraduationDesignPresubject)

    /**
     * 更新
     *
     * @param graduationDesignPresubject 数据
     */
    fun update(graduationDesignPresubject: GraduationDesignPresubject)

    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignPresubjectBean>, graduationDesignPresubjectBean: GraduationDesignPresubjectBean): Result<Record>

    /**
     * 数据 总数
     *
     * @return 总数
     */
    fun countAll(graduationDesignPresubjectBean: GraduationDesignPresubjectBean): Int

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignPresubjectBean>, graduationDesignPresubjectBean: GraduationDesignPresubjectBean): Int
}