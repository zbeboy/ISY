package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;

/**
 * Created by lenovo on 2016-11-12.
 */
public interface InternshipReleaseScienceService {

    /**
     * 保存
     *
     * @param internshipReleaseId 实习发布id
     * @param scienceId           专业id
     */
    void save(String internshipReleaseId, int scienceId);

    /**
     * 通过实习发布id查询专业信息
     *
     * @param internshipReleaseId 实习发布id
     * @return 专业数据
     */
    Result<Record> findByInternshipReleaseId(String internshipReleaseId);
}
