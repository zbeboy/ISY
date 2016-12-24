package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.SystemLogDao;
import top.zbeboy.isy.domain.tables.daos.SystemMessageDao;

/**
 * Created by lenovo on 2016-12-24.
 */
@Service("systemMessageService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemMessageServiceImpl implements SystemMessageService {

    private final Logger log = LoggerFactory.getLogger(SystemMessageServiceImpl.class);

    private final DSLContext create;

    private SystemMessageDao systemMessageDao;

    @Autowired
    public SystemMessageServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.systemMessageDao = new SystemMessageDao(configuration);
    }
}
