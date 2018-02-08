package top.zbeboy.isy.service.graduate.design

import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2018-02-08 .
 **/
interface GraduationDesignManifestService {
    /**
     * 毕业设计清单分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllManifestByPage(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): List<GraduationDesignDeclareBean>

    /**
     * 毕业设计清单数据 总数
     *
     * @return 总数
     */
    fun countAllManifest(graduationDesignDeclareBean: GraduationDesignDeclareBean): Int

    /**
     * 毕业设计清单根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    fun countManifestByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): Int

    /**
     * 导出毕业设计清单
     *
     * @param dataTablesUtils             datatables工具类
     * @param graduationDesignDeclareBean 条件
     * @return 导出数据
     */
    fun exportManifestData(dataTablesUtils: DataTablesUtils<GraduationDesignDeclareBean>, graduationDesignDeclareBean: GraduationDesignDeclareBean): List<GraduationDesignDeclareBean>

}