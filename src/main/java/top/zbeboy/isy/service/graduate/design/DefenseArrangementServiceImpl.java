package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.DefenseArrangementDao;
import top.zbeboy.isy.domain.tables.pojos.DefenseArrangement;

import javax.annotation.Resource;

/**
 * Created by lenovo on 2017-07-09.
 */
@Slf4j
@Service("defenseArrangementService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DefenseArrangementServiceImpl implements DefenseArrangementService {

    private final DSLContext create;

    @Resource
    private DefenseArrangementDao defenseArrangementDao;

    @Autowired
    public DefenseArrangementServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(DefenseArrangement defenseArrangement) {
        defenseArrangementDao.insert(defenseArrangement);
    }

    @Override
    public void update(DefenseArrangement defenseArrangement) {
        defenseArrangementDao.update(defenseArrangement);
    }
}
