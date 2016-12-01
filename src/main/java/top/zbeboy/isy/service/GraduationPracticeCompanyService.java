package top.zbeboy.isy.service;

import org.jooq.Record;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCompany;

import java.util.Optional;

/**
 * Created by lenovo on 2016-11-27.
 */
public interface GraduationPracticeCompanyService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 毕业实习(校外)
     */
    GraduationPracticeCompany findById(String id);

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
     * @param graduationPracticeCompany 毕业实习(校外)
     */
    void save(GraduationPracticeCompany graduationPracticeCompany);

    /**
     * 更新
     *
     * @param graduationPracticeCompany 毕业实习(校外)
     */
    void update(GraduationPracticeCompany graduationPracticeCompany);
}
