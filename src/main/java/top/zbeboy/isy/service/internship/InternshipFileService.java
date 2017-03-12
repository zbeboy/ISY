package top.zbeboy.isy.service.internship;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.InternshipFile;

/**
 * Created by lenovo on 2016-11-13.
 */
public interface InternshipFileService {

    /**
     * 保存
     *
     * @param internshipFile 实习文件
     */
    void save(InternshipFile internshipFile);

    /**
     * 通过实习id查询
     *
     * @param internshipReleaseId 实习id
     * @return 文件信息
     */
    Result<Record> findByInternshipReleaseId(String internshipReleaseId);

    /**
     * 通过实习id删除
     *
     * @param internshipReleaseId 实习id
     */
    void deleteByInternshipReleaseId(String internshipReleaseId);

    /**
     * 通过文件id与实习id删除
     *
     * @param fileId              文件id
     * @param internshipReleaseId 实习id
     */
    void deleteByFileIdAndInternshipReleaseId(String fileId, String internshipReleaseId);
}
