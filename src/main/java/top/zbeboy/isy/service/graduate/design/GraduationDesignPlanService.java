package top.zbeboy.isy.service.graduate.design;

import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPlanRecord;

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
}
