package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.domain.tables.pojos.DefenseArrangement;

/**
 * Created by lenovo on 2017-07-09.
 */
public interface DefenseArrangementService {

    /**
     * 保存
     *
     * @param defenseArrangement 数据
     */
    void save(DefenseArrangement defenseArrangement);

    /**
     * 更新
     *
     * @param defenseArrangement 数据
     */
    void update(DefenseArrangement defenseArrangement);
}
