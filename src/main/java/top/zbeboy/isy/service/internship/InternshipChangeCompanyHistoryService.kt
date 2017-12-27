package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.InternshipChangeCompanyHistory

/**
 * Created by zbeboy 2017-12-27 .
 **/
interface InternshipChangeCompanyHistoryService {
    /**
     * 保存
     *
     * @param internshipChangeCompanyHistory 数据
     */
    fun save(internshipChangeCompanyHistory: InternshipChangeCompanyHistory)

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