package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.DefenseOrderDao;
import top.zbeboy.isy.domain.tables.pojos.DefenseOrder;

import javax.annotation.Resource;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.DEFENSE_ORDER;

/**
 * Created by zbeboy on 2017/7/12.
 */
@Slf4j
@Service("defenseOrderService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DefenseOrderServiceImpl implements DefenseOrderService {

    private final DSLContext create;

    @Resource
    private DefenseOrderDao defenseOrderDao;

    @Autowired
    public DefenseOrderServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public void save(List<DefenseOrder> defenseOrders) {
        defenseOrderDao.insert(defenseOrders);
    }

    @Override
    public void deleteByDefenseGroupId(String defenseGroupId) {
        create.deleteFrom(DEFENSE_ORDER)
                .where(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .execute();
    }
}
