package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.records.InternshipReleaseScienceRecord;

import java.util.List;
import java.util.Optional;

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
    Result<Record> findByInternshipReleaseIdRelation(String internshipReleaseId);

    /**
     * 通过实习发布id查询
     *
     * @param internshipReleaseId 实习发布id
     * @return 数据
     */
    Result<InternshipReleaseScienceRecord> findByInternshipReleaseId(String internshipReleaseId);

    /**
     * 通过年级与专业id集合查询 注：不等于该实习id
     *
     * @param grade               年级
     * @param scienceIds          专业id集合
     * @param internshipReleaseId 实习发布id
     * @return 数据
     */
    Result<Record> findInScienceIdAndGradeNeInternshipReleaseId(String grade, List<Integer> scienceIds, String internshipReleaseId);

    /**
     * 通过实习发布id与专业id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param scienceId           专业id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndScienceId(String internshipReleaseId, int scienceId);
}
