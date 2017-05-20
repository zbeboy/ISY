package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import top.zbeboy.isy.domain.tables.pojos.Users;

import java.util.Optional;

/**
 * Created by lenovo on 2017-05-20.
 */
public interface GraduationDesignTutorService {

    /**
     * 通过学生id与发布id查询指导教师信息
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业发布id
     * @return 指导教师信息
     */
    Optional<Record> findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(int studentId, String graduationDesignReleaseId);
}
