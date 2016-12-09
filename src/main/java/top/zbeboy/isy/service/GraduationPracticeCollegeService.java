package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege;

import java.util.Optional;

/**
 * Created by lenovo on 2016-11-27.
 */
public interface GraduationPracticeCollegeService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 毕业实习(校内)
     */
    GraduationPracticeCollege findById(String id);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 通过实习发布id与学生id 关联查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Result<Record> findByInternshipReleaseIdAndStudentIdRelation(String internshipReleaseId, int studentId);

    /**
     * 保存
     *
     * @param graduationPracticeCollege 毕业实习(校内)
     */
    void save(GraduationPracticeCollege graduationPracticeCollege);

    /**
     * 更新
     *
     * @param graduationPracticeCollege 毕业实习(校内)
     */
    void update(GraduationPracticeCollege graduationPracticeCollege);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);
}
