package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.DefenseArrangementDao;
import top.zbeboy.isy.domain.tables.pojos.DefenseArrangement;

import javax.annotation.Resource;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.DEFENSE_ARRANGEMENT;

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

    @Override
    public DefenseArrangement findById(String id) {
        return defenseArrangementDao.findById(id);
    }

    @Override
    public Optional<Record> findByGraduationDesignReleaseId(String graduationDesignReleaseId) {
        return create.select()
                .from(DEFENSE_ARRANGEMENT)
                .where(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .fetchOptional();
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
