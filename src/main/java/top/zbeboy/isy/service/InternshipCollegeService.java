package top.zbeboy.isy.service;

import org.jooq.Record;
import top.zbeboy.isy.domain.tables.pojos.InternshipCollege;

import java.util.Optional;

/**
 * Created by lenovo on 2016-11-27.
 */
public interface InternshipCollegeService {

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);
}
