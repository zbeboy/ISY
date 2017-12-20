package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.records.InternshipReleaseScienceRecord
import java.util.*

/**
 * Created by zbeboy 2017-12-20 .
 **/
interface InternshipReleaseScienceService {
    /**
     * 保存
     *
     * @param internshipReleaseId 实习发布id
     * @param scienceId           专业id
     */
    fun save(internshipReleaseId: String, scienceId: Int)

    /**
     * 通过实习发布id查询专业信息
     *
     * @param internshipReleaseId 实习发布id
     * @return 专业数据
     */
    fun findByInternshipReleaseIdRelation(internshipReleaseId: String): Result<Record>

    /**
     * 通过实习发布id查询
     *
     * @param internshipReleaseId 实习发布id
     * @return 数据
     */
    fun findByInternshipReleaseId(internshipReleaseId: String): Result<InternshipReleaseScienceRecord>

    /**
     * 通过年级与专业id集合查询 注：不等于该实习id
     *
     * @param grade               年级
     * @param scienceIds          专业id集合
     * @param internshipReleaseId 实习发布id
     * @return 数据
     */
    fun findInScienceIdAndGradeNeInternshipReleaseId(grade: String, scienceIds: List<Int>, internshipReleaseId: String): Result<Record>

    /**
     * 通过实习发布id与专业id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param scienceId           专业id
     * @return 数据
     */
    fun findByInternshipReleaseIdAndScienceId(internshipReleaseId: String, scienceId: Int): Optional<Record>
}