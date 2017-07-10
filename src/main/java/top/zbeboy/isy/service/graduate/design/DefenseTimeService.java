package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.DefenseTime;

/**
 * Created by lenovo on 2017-07-09.
 */
public interface DefenseTimeService {

    /**
     * 通过毕业答辩安排id查询
     *
     * @param defenseArrangementId 毕业答辩安排id
     * @return 数据
     */
    Result<Record> findByDefenseArrangementId(String defenseArrangementId);

    /**
     * 保存
     *
     * @param defenseTime 数据
     */
    void save(DefenseTime defenseTime);

    /**
     * 通过毕业答辩安排id删除
     *
     * @param defenseArrangementId 毕业答辩安排id
     */
    void deleteByDefenseArrangementId(String defenseArrangementId);
}
