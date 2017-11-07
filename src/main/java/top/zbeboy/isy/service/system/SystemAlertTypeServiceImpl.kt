package top.zbeboy.isy.service.system

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.Tables.SYSTEM_ALERT_TYPE
import top.zbeboy.isy.domain.tables.daos.SystemAlertTypeDao
import top.zbeboy.isy.domain.tables.pojos.SystemAlertType
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-11-07 .
 **/
@Service("systemAlertTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class SystemAlertTypeServiceImpl : SystemAlertTypeService {

    @Resource
    open lateinit var systemAlertTypeDao: SystemAlertTypeDao

    override fun findById(id: Int): SystemAlertType {
        return systemAlertTypeDao.findById(id)
    }

    override fun findByType(type: String): SystemAlertType {
        return systemAlertTypeDao.fetchOne(SYSTEM_ALERT_TYPE.NAME, type)
    }
}