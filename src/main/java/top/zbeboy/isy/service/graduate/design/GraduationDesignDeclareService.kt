package top.zbeboy.isy.service.graduate.design

import org.jooq.Record1
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclare
import top.zbeboy.isy.domain.tables.records.GraduationDesignDeclareRecord
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2018-01-26 .
 **/
interface GraduationDesignDeclareService {
    /**
     * 根据毕业设计学生题目表查询
     *
     * @param graduationDesignPresubjectId 毕业设计学生题目表id
     * @return 申报数据
     */
    fun findByGraduationDesignPresubjectId(graduationDesignPresubjectId: String): GraduationDesignDeclareRecord

    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): List<GraduationDesignDeclareBean>

    /**
     * 数据 总数
     *
     * @return 总数
     */
    fun countAll(graduationDesignDeclareBean: GraduationDesignDeclareBean): Int

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): Int

    /**
     * 导出
     *
     * @param dataTablesUtils             datatables工具类
     * @param graduationDesignDeclareBean 条件
     * @return 导出数据
     */
    fun exportData(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): List<GraduationDesignDeclareBean>

    /**
     * 保存 或 更新 根据题目id判断
     *
     * @param graduationDesignDeclare 数据
     */
    fun saveOrUpdate(graduationDesignDeclare: GraduationDesignDeclare)

    /**
     * 保存 或 更新 状态
     *
     * @param graduationDesignDeclare 数据
     */
    fun saveOrUpdateState(graduationDesignDeclare: GraduationDesignDeclare)

    /**
     * 查询该指导教师下所有未申报的题目id
     *
     * @param staffId                   教职工 id
     * @param graduationDesignReleaseId 发布id
     * @return 数据
     */
    fun findByStaffIdRelationNeIsOkApply(staffId: Int, graduationDesignReleaseId: String): Result<Record1<String>>
}