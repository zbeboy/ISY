package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.ApplicationDao;
import top.zbeboy.isy.domain.tables.daos.ApplicationTypeDao;
import top.zbeboy.isy.domain.tables.pojos.ApplicationType;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lenovo on 2016-10-04.
 */
@Service("applicationTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ApplicationTypeServiceImpl implements ApplicationTypeService {

    private final Logger log = LoggerFactory.getLogger(ApplicationTypeServiceImpl.class);

    private final DSLContext create;

    private ApplicationTypeDao applicationTypeDao;

    @Autowired
    public ApplicationTypeServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.applicationTypeDao = new ApplicationTypeDao(configuration);
    }

    @Override
    public List<ApplicationType> findAll() {
        return applicationTypeDao.findAll();
    }
}
