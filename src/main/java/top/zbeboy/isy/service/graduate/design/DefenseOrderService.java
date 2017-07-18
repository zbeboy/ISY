package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.domain.tables.pojos.DefenseOrder;

import java.util.List;

/**
 * Created by zbeboy on 2017/7/12.
 */
public interface DefenseOrderService {

    /**
     * 保存
     *
     * @param defenseOrders 顺序
     */
    void save(List<DefenseOrder> defenseOrders);

    /**
     * 通过组id删除
     *
     * @param defenseGroupId 组id
     */
    void deleteByDefenseGroupId(String defenseGroupId);
}
