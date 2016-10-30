package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.PoliticalLandscapeDao;
import top.zbeboy.isy.domain.tables.pojos.PoliticalLandscape;

import java.util.List;

/**
 * Created by lenovo on 2016-10-30.
 */
@Service("PoliticalLandscapeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class PoliticalLandscapeServiceImpl implements PoliticalLandscapeService {

    private final Logger log = LoggerFactory.getLogger(PoliticalLandscapeServiceImpl.class);

    private final DSLContext create;

    private PoliticalLandscapeDao politicalLandscapeDao;

    @Autowired
    public PoliticalLandscapeServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.politicalLandscapeDao = new PoliticalLandscapeDao(configuration);
    }

    @Override
    public List<PoliticalLandscape> findAll() {
        return politicalLandscapeDao.findAll();
    }
}
