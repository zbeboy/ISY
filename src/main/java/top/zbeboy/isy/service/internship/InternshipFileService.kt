package top.zbeboy.isy.service.internship

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.InternshipFile

/**
 * Created by zbeboy 2017-12-20 .
 **/
interface InternshipFileService {
    /**
     * 保存
     *
     * @param internshipFile 实习文件
     */
    fun save(internshipFile: InternshipFile)

    /**
     * 通过实习id查询
     *
     * @param internshipReleaseId 实习id
     * @return 文件信息
     */
    fun findByInternshipReleaseId(internshipReleaseId: String): Result<Record>

    /**
     * 通过实习id删除
     *
     * @param internshipReleaseId 实习id
     */
    fun deleteByInternshipReleaseId(internshipReleaseId: String)

    /**
     * 通过文件id与实习id删除
     *
     * @param fileId              文件id
     * @param internshipReleaseId 实习id
     */
    fun deleteByFileIdAndInternshipReleaseId(fileId: String, internshipReleaseId: String)
}