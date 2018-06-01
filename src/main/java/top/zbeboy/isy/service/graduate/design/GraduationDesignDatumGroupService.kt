package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumGroupBean
import top.zbeboy.isy.web.util.DataTablesUtils

/**
 * Created by zbeboy 2018-01-29 .
 **/
interface GraduationDesignDatumGroupService {
    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 组内资料
     */
    fun findById(id: String): GraduationDesignDatumGroup

    /**
     * 保存
     *
     * @param graduationDesignDatumGroup 数据
     */
    fun save(graduationDesignDatumGroup: GraduationDesignDatumGroup)

    /**
     * 删除
     *
     * @param graduationDesignDatumGroup 数据
     */
    fun delete(graduationDesignDatumGroup: GraduationDesignDatumGroup)

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignDatumGroupBean>, graduationDesignDatumGroupBean: GraduationDesignDatumGroupBean): Result<Record>

    /**
     * 总数
     *
     * @return 总数
     */
    fun countAll(graduationDesignDatumGroupBean: GraduationDesignDatumGroupBean): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDatumGroupBean>, graduationDesignDatumGroupBean: GraduationDesignDatumGroupBean): Int
}