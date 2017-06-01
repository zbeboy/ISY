package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPlanRecord;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by zbeboy on 2017/5/27.
 */
public interface GraduationDesignPlanService {

    /**
     * 根据毕业设计指导教师id查询
     *
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @return 数据
     */
    Result<GraduationDesignPlanRecord> findByGraduationDesignTeacherIdOrderByAddTime(String graduationDesignTeacherId);

    /**
     * 查询最近一条记录
     *
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @param addTime                   添加时间
     * @return 数据
     */
    Record findByGraduationDesignTeacherIdAndLeAddTime(String graduationDesignTeacherId, Timestamp addTime);
}
