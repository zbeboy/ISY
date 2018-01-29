package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatum
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*

/**
 * Created by zbeboy 2018-01-29 .
 **/
interface GraduationDesignDatumService {
    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 毕业设计资料
     */
    fun findById(id: String): GraduationDesignDatum

    /**
     * 通过主键关联查询
     *
     * @param id 主键
     * @return 毕业设计资料
     */
    fun findByIdRelation(id: String): Optional<Record>

    /**
     * 通过教职工与学生关联表id与文件类型id查询
     *
     * @param graduationDesignTutorId     教职工与学生关联表id
     * @param graduationDesignDatumTypeId 文件类型id
     * @return 资料
     */
    fun findByGraduationDesignTutorIdAndGraduationDesignDatumTypeId(graduationDesignTutorId: String, graduationDesignDatumTypeId: Int): Optional<Record>

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignDatumBean>, graduationDesignDatumBean: GraduationDesignDatumBean): Result<Record>

    /**
     * 总数
     *
     * @return 总数
     */
    fun countAll(graduationDesignDatumBean: GraduationDesignDatumBean): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDatumBean>, graduationDesignDatumBean: GraduationDesignDatumBean): Int

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    fun findTeamAllByPage(dataTablesUtils: DataTablesUtils<GraduationDesignDatumBean>, graduationDesignDatumBean: GraduationDesignDatumBean): Result<Record>

    /**
     * 总数
     *
     * @return 总数
     */
    fun countTeamAll(graduationDesignDatumBean: GraduationDesignDatumBean): Int

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    fun countTeamByCondition(dataTablesUtils: DataTablesUtils<GraduationDesignDatumBean>, graduationDesignDatumBean: GraduationDesignDatumBean): Int

    /**
     * 更新
     *
     * @param graduationDesignDatum 数据
     */
    fun update(graduationDesignDatum: GraduationDesignDatum)

    /**
     * 保存
     *
     * @param graduationDesignDatum 数据
     */
    fun save(graduationDesignDatum: GraduationDesignDatum)

    /**
     * 通过主键删除
     *
     * @param id 主键
     */
    fun deleteById(id: String)
}