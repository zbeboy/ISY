package top.zbeboy.isy.service.platform

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.domain.tables.daos.UsersKeyDao
import top.zbeboy.isy.domain.tables.pojos.UsersKey
import top.zbeboy.isy.service.common.DesService
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-06 .
 **/
@Service("usersKeyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class UsersKeyServiceImpl : UsersKeyService {

    @Resource
    open lateinit var usersKeyDao: UsersKeyDao

    @Autowired
    lateinit open var isyProperties: ISYProperties

    @Resource
    open lateinit var desService: DesService

    override fun findByUsername(username: String): UsersKey {
        val id = desService.encrypt(username, isyProperties.getSecurity().desDefaultKey!!)
        return usersKeyDao.findById(id)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(usersKey: UsersKey) {
        usersKeyDao.insert(usersKey)
    }

    override fun deleteByUsername(username: String) {
        val id = desService.encrypt(username, isyProperties.getSecurity().desDefaultKey!!)
        usersKeyDao.deleteById(id)
    }
}