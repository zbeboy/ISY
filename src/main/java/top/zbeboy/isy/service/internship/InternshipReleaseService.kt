package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease
import top.zbeboy.isy.domain.tables.records.InternshipReleaseRecord
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean
import top.zbeboy.isy.web.util.PaginationUtils
import java.sql.Timestamp
import java.util.*

/**
 * Created by zbeboy 2017-12-19 .
 **/
interface InternshipReleaseService {
    /**
     * 通过实习结束时间查询
     *
     * @param endTime 实习结束时间
     * @return 实习
     */
    fun findByEndTime(endTime: Timestamp): Result<InternshipReleaseRecord>

    /**
     * 通过id查询
     *
     * @param internshipReleaseId 实习id
     * @return 实习
     */
    fun findById(internshipReleaseId: String): InternshipRelease

    /**
     * 通过id关联查询查询
     *
     * @param internshipReleaseId 实习id
     * @return 实习
     */
    fun findByIdRelation(internshipReleaseId: String): Optional<Record>

    /**
     * 通过标题查询
     *
     * @param releaseTitle 实习标题
     * @return 实习
     */
    fun findByReleaseTitle(releaseTitle: String): List<InternshipRelease>

    /**
     * 通过标题查询
     *
     * @param releaseTitle        实习标题
     * @param internshipReleaseId 实习id
     * @return 实习
     */
    fun findByReleaseTitleNeInternshipReleaseId(releaseTitle: String, internshipReleaseId: String): Result<InternshipReleaseRecord>

    /**
     * 保存
     *
     * @param internshipRelease 实习
     */
    fun save(internshipRelease: InternshipRelease)

    /**
     * 更新
     *
     * @param internshipRelease 实习
     */
    fun update(internshipRelease: InternshipRelease)

    /**
     * 分页查询全部
     *
     * @param paginationUtils       分页工具
     * @param internshipReleaseBean 额外参数
     * @return 分页数据
     */
    fun findAllByPage(paginationUtils: PaginationUtils, internshipReleaseBean: InternshipReleaseBean): Result<Record>

    /**
     * 处理实习返回数据
     *
     * @param paginationUtils       分页工具
     * @param records               数据
     * @param internshipReleaseBean 额外参数
     * @return 处理后的数据
     */
    fun dealData(paginationUtils: PaginationUtils, records: Result<Record>, internshipReleaseBean: InternshipReleaseBean): List<InternshipReleaseBean>

    /**
     * 根据条件统计
     *
     * @param paginationUtils       分页工具
     * @param internshipReleaseBean 额外参数
     * @return 统计
     */
    fun countByCondition(paginationUtils: PaginationUtils, internshipReleaseBean: InternshipReleaseBean): Int
}