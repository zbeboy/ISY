package top.zbeboy.isy.service.platform

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.tables.daos.UsersUniqueInfoDao
import top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-06 .
 **/
@Service("usersUniqueInfoService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class UsersUniqueInfoServiceImpl : UsersUniqueInfoService {

    @Resource
    open lateinit var usersUniqueInfoDao: UsersUniqueInfoDao

    override fun findByUsername(username: String): UsersUniqueInfo {
        return usersUniqueInfoDao.findById(username)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(usersUniqueInfo: UsersUniqueInfo) {
        usersUniqueInfoDao.insert(usersUniqueInfo)
    }

    override fun update(usersUniqueInfo: UsersUniqueInfo) {
        usersUniqueInfoDao.update(usersUniqueInfo)
    }

    override fun deleteByUsername(username: String) {
        usersUniqueInfoDao.deleteById(username)
    }
}