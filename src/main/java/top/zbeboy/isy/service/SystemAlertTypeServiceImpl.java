package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import top.zbeboy.isy.domain.tables.daos.SystemAlertTypeDao;
import top.zbeboy.isy.domain.tables.daos.SystemLogDao;

/**
 * Created by lenovo on 2016-12-24.
 */
public class SystemAlertTypeServiceImpl implements SystemAlertTypeService {

    private final Logger log = LoggerFactory.getLogger(SystemAlertTypeServiceImpl.class);

    private final DSLContext create;

    private SystemAlertTypeDao systemAlertTypeDao;

    @Autowired
    public SystemAlertTypeServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.systemAlertTypeDao = new SystemAlertTypeDao(configuration);
    }
}
