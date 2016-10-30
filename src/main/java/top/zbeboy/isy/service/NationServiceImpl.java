package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.NationDao;
import top.zbeboy.isy.domain.tables.pojos.Nation;

import java.util.List;

/**
 * Created by lenovo on 2016-10-30.
 */
@Service("NationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class NationServiceImpl implements NationService {

    private final Logger log = LoggerFactory.getLogger(NationServiceImpl.class);

    private final DSLContext create;

    private NationDao nationDao;

    @Autowired
    public NationServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.nationDao = new NationDao(configuration);
    }

    @Override
    public List<Nation> findAll() {
        return nationDao.findAll();
    }
}
