package top.zbeboy.isy.service.graduate.design

import org.jooq.Record
import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignReleaseFile

/**
 * Created by zbeboy 2018-01-15 .
 **/
interface GraduationDesignReleaseFileService {
    /**
     * 通过毕业设计发布id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 关联数据
     */
    fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): Result<Record>

    /**
     * 保存
     *
     * @param graduationDesignReleaseFile 数据
     */
    fun save(graduationDesignReleaseFile: GraduationDesignReleaseFile)

    /**
     * 通过文件id与毕业设计发布id删除
     *
     * @param fileId                    文件id
     * @param graduationDesignReleaseId 毕业设计发布id
     */
    fun deleteByFileIdAndGraduationDesignReleaseId(fileId: String, graduationDesignReleaseId: String)

    /**
     * 通过毕业设计发布id删除
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     */
    fun deleteByGraduationDesignReleaseId(graduationDesignReleaseId: String)
}