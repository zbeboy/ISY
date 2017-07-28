package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.domain.tables.pojos.DefenseRate;
import top.zbeboy.isy.domain.tables.records.DefenseRateRecord;

/**
 * Created by zbeboy on 2017/7/28.
 */
public interface DefenseRateService {

    /**
     * 通过顺序id与毕业设计指导教师id查询
     *
     * @param defenseOrderId            毕业设计顺序id
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @return 数据
     */
    DefenseRateRecord findByDefenseOrderIdAndGraduationDesignTeacherId(String defenseOrderId, String graduationDesignTeacherId);

    /**
     * 保存
     *
     * @param defenseRate 数据
     */
    void save(DefenseRate defenseRate);
}
