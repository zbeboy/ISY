package top.zbeboy.isy.service.graduate.design

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignArchives
import top.zbeboy.isy.domain.tables.records.GraduationDesignArchivesRecord
import top.zbeboy.isy.web.bean.graduate.design.archives.GraduationDesignArchivesBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2018-02-08 .
 **/
interface GraduationDesignArchivesService {
    /**
     * 通过毕业设计题目id查询
     *
     * @param graduationDesignPresubjectId 毕业设计题目id
     * @return 档案
     */
    fun findByGraduationDesignPresubjectId(graduationDesignPresubjectId: String): GraduationDesignArchivesRecord

    /**
     * 通过档案号查询
     *
     * @param archiveNumber 档案号
     * @return 档案
     */
    fun findByArchiveNumber(archiveNumber: String): GraduationDesignArchivesRecord

    /**
     * 毕业设计归档分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignArchivesBean>, graduateArchivesBean: GraduationDesignArchivesBean): List<GraduationDesignArchivesBean>

    /**
     * 毕业设计归档数据 总数
     *
     * @return 总数
     */
    fun countAll(graduateArchivesBean: GraduationDesignArchivesBean): Int

    /**
     * 毕业设计归档根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignArchivesBean>, graduateArchivesBean: GraduationDesignArchivesBean): Int

    /**
     * 导出
     *
     * @param dataTablesUtils              datatables工具类
     * @param graduationDesignArchivesBean 条件
     * @return 导出数据
     */
    fun exportData(dataTablesUtils: DataTablesUtils<GraduationDesignArchivesBean>, graduationDesignArchivesBean: GraduationDesignArchivesBean): List<GraduationDesignArchivesBean>

    /**
     * 保存 忽略
     *
     * @param graduationDesignArchives 数据
     */
    fun saveAndIgnore(graduationDesignArchives: GraduationDesignArchives)

    /**
     * 保存
     *
     * @param graduationDesignArchives 数据
     */
    fun save(graduationDesignArchives: GraduationDesignArchives)

    /**
     * 保存
     *
     * @param graduationDesignArchives 数据
     */
    fun update(graduationDesignArchives: GraduationDesignArchives)
}