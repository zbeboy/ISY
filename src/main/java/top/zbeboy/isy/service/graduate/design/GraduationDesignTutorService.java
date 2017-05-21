package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;

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

    /**
     * 统计未填报学生数
     *
     * @param graduationDesignReleaseBean 毕业发布
     * @return 学生数
     */
    int countNotFillStudent(GraduationDesignReleaseBean graduationDesignReleaseBean);

    /**
     * 统计填报学生数
     *
     * @param graduationDesignReleaseBean 毕业发布
     * @return 学生数
     */
    int countFillStudent(GraduationDesignReleaseBean graduationDesignReleaseBean);
}
