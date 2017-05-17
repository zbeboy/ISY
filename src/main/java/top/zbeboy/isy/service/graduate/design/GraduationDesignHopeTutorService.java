package top.zbeboy.isy.service.graduate.design;

import org.jooq.Result;
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
}
