package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignHopeTutor;
import top.zbeboy.isy.domain.tables.records.GraduationDesignHopeTutorRecord;

/**
 * Created by zbeboy on 2017/5/17.
 */
public interface GraduationDesignHopeTutorService {

    /**
     * 通过学生id统计
     *
     * @param studentId 学生id
     * @return 数量
     */
    int countByStudentId(int studentId);

    /**
     * 根据学生id查询
     *
     * @param studentId 学生id
     * @return 数据
     */
    Result<GraduationDesignHopeTutorRecord> findByStudentId(int studentId);

    /**
     * 根据学生id查询教师信息
     *
     * @param studentId 学生id
     * @return 教师信息
     */
    Result<Record> findByStudentIdRelationForStaff(int studentId);

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
