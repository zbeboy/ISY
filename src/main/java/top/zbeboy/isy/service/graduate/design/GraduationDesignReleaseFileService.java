package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignReleaseFile;

/**
 * Created by zbeboy on 2017/5/5.
 */
public interface GraduationDesignReleaseFileService {

    /**
     * 通过毕业设计发布id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 关联数据
     */
    Result<Record> findByGraduationDesignReleaseId(String graduationDesignReleaseId);

    /**
     * 保存
     *
     * @param graduationDesignReleaseFile 数据
     */
    void save(GraduationDesignReleaseFile graduationDesignReleaseFile);

    /**
     * 通过文件id与毕业设计发布id删除
     *
     * @param fileId                    文件id
     * @param graduationDesignReleaseId 毕业设计发布id
     */
    void deleteByFileIdAndGraduationDesignReleaseId(String fileId, String graduationDesignReleaseId);

    /**
     * 通过毕业设计发布id删除
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     */
    void deleteByGraduationDesignReleaseId(String graduationDesignReleaseId);
}
