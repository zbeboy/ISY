package top.zbeboy.isy.service.system;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.SystemAlertTypeDao;
import top.zbeboy.isy.domain.tables.pojos.SystemAlertType;

import javax.annotation.Resource;

import static top.zbeboy.isy.domain.Tables.SYSTEM_ALERT_TYPE;


/**
 * Created by lenovo on 2016-12-24.
 */
@Slf4j
@Service("systemAlertTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemAlertTypeServiceImpl implements SystemAlertTypeService {

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

    @Override
    public SystemAlertType findByType(String type) {
        return systemAlertTypeDao.fetchOne(SYSTEM_ALERT_TYPE.NAME, type);
    }
}
