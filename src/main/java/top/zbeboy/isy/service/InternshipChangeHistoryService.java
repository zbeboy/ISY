package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.InternshipChangeHistory;

/**
 * Created by lenovo on 2016-12-12.
 */
public interface InternshipChangeHistoryService {

    /**
     * 保存
     *
     * @param internshipChangeHistory 数据
     */
    void save(InternshipChangeHistory internshipChangeHistory);

    /**
     * 通过实习发布id与学生id删除
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Result<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);
}
