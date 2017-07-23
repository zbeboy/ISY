package top.zbeboy.isy.service.data;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.ScoreTypeDao;
import top.zbeboy.isy.domain.tables.pojos.ScoreType;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lenovo on 2017-07-23.
 */
@Slf4j
@Service("scoreTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ScoreTypeServiceImpl implements ScoreTypeService {

    private final DSLContext create;

    @Resource
    private ScoreTypeDao scoreTypeDao;

    @Autowired
    public ScoreTypeServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<ScoreType> findAll() {
        return scoreTypeDao.findAll();
    }
}
