package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.InternshipChangeHistory

/**
 * Created by zbeboy 2017-12-27 .
 **/
interface InternshipChangeHistoryService {
    /**
     * 保存
     *
     * @param internshipChangeHistory 数据
     */
    fun save(internshipChangeHistory: InternshipChangeHistory)

    /**
     * 通过实习发布id与学生id删除
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int)

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Result<Record>
}