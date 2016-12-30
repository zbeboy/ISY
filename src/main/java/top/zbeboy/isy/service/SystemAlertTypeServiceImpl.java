package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.SystemAlertTypeDao;
import top.zbeboy.isy.domain.tables.daos.SystemLogDao;
import top.zbeboy.isy.domain.tables.pojos.SystemAlertType;

import javax.annotation.Resource;

/**
 * Created by lenovo on 2016-12-24.
 */
@Service("systemAlertTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemAlertTypeServiceImpl implements SystemAlertTypeService {

    private final Logger log = LoggerFactory.getLogger(SystemAlertTypeServiceImpl.class);

    private final DSLContext create;

    @Resource
    private SystemAlertTypeDao systemAlertTypeDao;

    @Autowired
    public SystemAlertTypeServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public SystemAlertType findById(int id) {
        return systemAlertTypeDao.findById(id);
    }
}
