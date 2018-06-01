package top.zbeboy.isy.service.platform

import org.jooq.DSLContext
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.daos.RoleApplicationDao
import top.zbeboy.isy.domain.tables.pojos.RoleApplication
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord
import top.zbeboy.isy.web.util.SmallPropsUtils
import java.util.ArrayList
import javax.annotation.Resource

import top.zbeboy.isy.domain.Tables.ROLE_APPLICATION

/**
 * Created by zbeboy 2017-11-16 .
 **/
@Service("roleApplicationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class RoleApplicationServiceImpl @Autowired constructor(dslContext: DSLContext) : RoleApplicationService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var roleApplicationDao: RoleApplicationDao

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(roleApplication: RoleApplication) {
        create.insertInto<RoleApplicationRecord>(ROLE_APPLICATION)
                .set(ROLE_APPLICATION.ROLE_ID, roleApplication.roleId)
                .set(ROLE_APPLICATION.APPLICATION_ID, roleApplication.applicationId)
                .execute()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(roleApplication: List<RoleApplication>) {
        roleApplicationDao.insert(roleApplication)
    }

    override fun deleteByApplicationId(applicationId: String) {
        create.deleteFrom<RoleApplicationRecord>(ROLE_APPLICATION)
                .where(ROLE_APPLICATION.APPLICATION_ID.eq(applicationId))
                .execute()
    }

    override fun deleteByRoleId(roleId: String) {
        create.deleteFrom<RoleApplicationRecord>(ROLE_APPLICATION)
                .where(ROLE_APPLICATION.ROLE_ID.`in`(roleId))
                .execute()
    }

    override fun findByRoleId(roleId: String): Result<RoleApplicationRecord> {
        return create.selectFrom<RoleApplicationRecord>(ROLE_APPLICATION)
                .where(ROLE_APPLICATION.ROLE_ID.eq(roleId))
                .fetch()
    }

    override fun batchSaveRoleApplication(applicationIds: String, roleId: String) {
        if (StringUtils.hasLength(applicationIds)) {
            val ids = SmallPropsUtils.StringIdsToStringList(applicationIds)
            val roleApplications = ArrayList<RoleApplication>()
            ids.forEach { id -> roleApplications.add(RoleApplication(roleId, id)) }
            save(roleApplications)
        }
    }

}