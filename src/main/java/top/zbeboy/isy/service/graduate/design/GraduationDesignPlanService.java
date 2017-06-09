package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/5/27.
 */
public interface GraduationDesignPlanService {
    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 数据
     */
    GraduationDesignPlan findById(String id);

    /**
     * 通过主键关联查询
     *
     * @param id 主键
     * @return 数据
     */
    Optional<Record> findByIdRelation(String id);

    /**
     * 根据教师id查询
     *
     * @param graduationDesignReleaseId 发布id
     * @param staffId                   教职工id
     * @return 数据
     */
    Result<Record> findByGraduationDesignReleaseIdAndStaffIdOrderByAddTime(String graduationDesignReleaseId, int staffId);

    /**
     * 查询最近一条记录
     *
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @param addTime                   添加时间
     * @return 数据
     */
    Record findByGraduationDesignTeacherIdAndLessThanAddTime(String graduationDesignTeacherId, Timestamp addTime);

    /**
     * 保存
     *
     * @param graduationDesignPlan 数据
     */
    void save(GraduationDesignPlan graduationDesignPlan);

    /**
     * 更新
     *
     * @param graduationDesignPlan 数据
     */
    void update(GraduationDesignPlan graduationDesignPlan);

    /**
     * 通过主键删除
     *
     * @param id 主键
     */
    void deleteById(List<String> id);
}
