package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignHopeTutor;

/**
 * Created by zbeboy on 2017/5/17.
 */
public interface GraduationDesignHopeTutorService {

    /**
     * 通过学生id与毕业设计发布id统计
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业设计发id
     * @return 数量
     */
    int countByStudentIdAndGraduationDesignReleaseId(int studentId, String graduationDesignReleaseId);

    /**
     * 根据学生id与毕业设计发布id查询
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    Result<Record> findByStudentIdAndGraduationDesignReleaseId(int studentId, String graduationDesignReleaseId);

    /**
     * 根据学生id与毕业设计发布id查询教师信息
     *
     * @param studentId                 学生id
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @return 教师信息
     */
    Result<Record> findByStudentIdAndGraduationDesignTeacherIdRelationForStaff(int studentId, String graduationDesignTeacherId);

    /**
     * 保存
     *
     * @param graduationDesignHopeTutor 数据
     */
    void save(GraduationDesignHopeTutor graduationDesignHopeTutor);

    /**
     * 删除
     *
     * @param graduationDesignHopeTutor 数据
     */
    void delete(GraduationDesignHopeTutor graduationDesignHopeTutor);
}
