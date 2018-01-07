package top.zbeboy.isy.service.platform

import org.jooq.DSLContext
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.domain.Tables.USERS_UNIQUE_INFO
import top.zbeboy.isy.domain.tables.daos.UsersUniqueInfoDao
import top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo
import top.zbeboy.isy.domain.tables.records.UsersUniqueInfoRecord
import top.zbeboy.isy.service.common.DesService
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-06 .
 **/
@Service("usersUniqueInfoService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class UsersUniqueInfoServiceImpl @Autowired constructor(dslContext: DSLContext) : UsersUniqueInfoService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var usersUniqueInfoDao: UsersUniqueInfoDao

    @Autowired
    lateinit open var isyProperties: ISYProperties

    @Resource
    open lateinit var desService: DesService

    override fun findByUsername(username: String): UsersUniqueInfo? {
        val id = desService.encrypt(username, isyProperties.getSecurity().desDefaultKey!!)
        return usersUniqueInfoDao.findById(id)
    }

    override fun findByIdCardNeUsername(username: String, idCard: String): Result<UsersUniqueInfoRecord> {
        val id = desService.encrypt(username, isyProperties.getSecurity().desDefaultKey!!)
        val card = desService.encrypt(idCard, isyProperties.getSecurity().desDefaultKey!!)
        return create.selectFrom(USERS_UNIQUE_INFO)
                .where(USERS_UNIQUE_INFO.ID_CARD.eq(card).and(USERS_UNIQUE_INFO.USERNAME.ne(id))).fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(usersUniqueInfo: UsersUniqueInfo) {
        usersUniqueInfoDao.insert(usersUniqueInfo)
    }

    override fun update(usersUniqueInfo: UsersUniqueInfo) {
        usersUniqueInfoDao.update(usersUniqueInfo)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun saveOrUpdate(usersUniqueInfo: UsersUniqueInfo) {
        create.insertInto(USERS_UNIQUE_INFO,
                USERS_UNIQUE_INFO.USERNAME,
                USERS_UNIQUE_INFO.ID_CARD)
                .values(usersUniqueInfo.username,
                        usersUniqueInfo.idCard)
                .onDuplicateKeyUpdate()
                .set(USERS_UNIQUE_INFO.ID_CARD, usersUniqueInfo.idCard)
                .execute()
    }

    override fun deleteByUsername(username: String) {
        val id = desService.encrypt(username, isyProperties.getSecurity().desDefaultKey!!)
        usersUniqueInfoDao.deleteById(id)
    }
}