package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease
import top.zbeboy.isy.domain.tables.records.GraduationDesignReleaseRecord
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.util.*

/**
 * Created by zbeboy 2018-01-14 .
 **/
interface GraduationDesignReleaseService {
    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 毕业设计发布
     */
    fun findById(id: String): GraduationDesignRelease

    /**
     * 通过主键关联查询
     *
     * @param id 主键
     * @return 关联数据
     */
    fun findByIdRelation(id: String): Optional<Record>

    /**
     * 通过标题查询
     *
     * @param graduationDesignTitle 毕业设计标题
     * @return 毕业设计
     */
    fun findByGraduationDesignTitle(graduationDesignTitle: String): List<GraduationDesignRelease>

    /**
     * 通过标题查询
     *
     * @param graduationDesignTitle     毕业设计标题
     * @param graduationDesignReleaseId 毕业设计id
     * @return 毕业设计
     */
    fun findByGraduationDesignTitleNeGraduationDesignReleaseId(graduationDesignTitle: String, graduationDesignReleaseId: String): Result<GraduationDesignReleaseRecord>

    /**
     * 分页查询全部
     *
     * @param paginationUtils             分页工具
     * @param graduationDesignReleaseBean 额外参数
     * @return 分页数据
     */
    fun findAllByPage(paginationUtils: PaginationUtils, graduationDesignReleaseBean: GraduationDesignReleaseBean): Result<Record>

    /**
     * 处理实习返回数据
     *
     * @param paginationUtils             分页工具
     * @param records                     数据
     * @param graduationDesignReleaseBean 额外参数
     * @return 处理后的数据
     */
    fun dealData(paginationUtils: PaginationUtils, records: Result<Record>, graduationDesignReleaseBean: GraduationDesignReleaseBean): List<GraduationDesignReleaseBean>

    /**
     * 保存
     *
     * @param graduationDesignRelease 数据
     */
    fun save(graduationDesignRelease: GraduationDesignRelease)

    /**
     * 更新
     *
     * @param graduationDesignRelease 数据
     */
    fun update(graduationDesignRelease: GraduationDesignRelease)
}