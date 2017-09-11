package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.DefenseOrder;
import top.zbeboy.isy.domain.tables.records.DefenseOrderRecord;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseOrderBean;

import java.util.List;

/**
 * Created by zbeboy on 2017/7/12.
 */
public interface DefenseOrderService {

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 用户
     */
    DefenseOrder findById(String id);

    /**
     * 通过组id查询
     *
     * @param defenseGroupId 组id
     * @return 组数据
     */
    List<DefenseOrder> findByDefenseGroupId(String defenseGroupId);

    /**
     * 查询数据
     *
     * @param condition 查询条件
     * @return 数据
     */
    Result<Record> findAll(DefenseOrderBean condition);

    /**
     * 通过序号与组id查询
     *
     * @param sortNum        序号
     * @param defenseGroupId 组id
     * @return 数据
     */
    DefenseOrderRecord findBySortNumAndDefenseGroupId(int sortNum, String defenseGroupId);

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

    /**
     * 更新
     *
     * @param defenseOrder 数据
     */
    void update(DefenseOrder defenseOrder);
}
