package top.zbeboy.isy.service;

import org.jooq.Record;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeUnify;

import java.util.Optional;

/**
 * Created by lenovo on 2016-11-27.
 */
public interface GraduationPracticeUnifyService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 毕业实习(学校统一组织校外实习)
     */
    GraduationPracticeUnify findById(String id);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 保存
     *
     * @param graduationPracticeUnify 毕业实习(学校统一组织校外实习)
     */
    void save(GraduationPracticeUnify graduationPracticeUnify);

    /**
     * 更新
     *
     * @param graduationPracticeUnify 毕业实习(学校统一组织校外实习)
     */
    void update(GraduationPracticeUnify graduationPracticeUnify);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);
}
