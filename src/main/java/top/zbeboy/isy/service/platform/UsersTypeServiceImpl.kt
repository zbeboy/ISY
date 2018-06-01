package top.zbeboy.isy.service.platform

import org.jooq.DSLContext
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.tables.daos.UsersTypeDao
import top.zbeboy.isy.domain.tables.records.UsersTypeRecord
import javax.annotation.Resource

import top.zbeboy.isy.domain.tables.UsersType.USERS_TYPE
/**
 * Created by zbeboy 2017-11-16 .
 **/
@Service("usersTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class UsersTypeServiceImpl @Autowired constructor(dslContext: DSLContext) : UsersTypeService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var usersTypeDao: UsersTypeDao

    @Resource
    open lateinit var usersService: UsersService

    override fun findAll(): Result<UsersTypeRecord> {
        return create.selectFrom<UsersTypeRecord>(USERS_TYPE).fetch()
    }

    override fun isCurrentUsersTypeName(usersTypeName: String): Boolean {
        val users = usersService.getUserFromSession()
        val usersType = usersTypeDao.fetchOneByUsersTypeId(users!!.usersTypeId).usersTypeName
        return usersTypeName == usersType
    }
}