package top.zbeboy.isy.service.data

import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.tables.daos.CollegeRoleDao
import javax.annotation.Resource

import top.zbeboy.isy.domain.Tables.COLLEGE_ROLE
import top.zbeboy.isy.domain.tables.pojos.CollegeRole
import top.zbeboy.isy.domain.tables.records.CollegeRoleRecord
import java.util.*

/**
 * Created by zbeboy 2017-12-02 .
 **/
@Service("collegeRoleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class CollegeRoleServiceImpl @Autowired constructor(dslContext: DSLContext) : CollegeRoleService{

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var collegeRoleDao: CollegeRoleDao

    override fun findByCollegeId(collegeId: Int): List<CollegeRoleRecord> {
        return create.selectFrom(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId))
                .fetch()
    }

    override fun findByCollegeIdAndAllowAgent(collegeId: Int, allowAgent: Byte?): List<CollegeRoleRecord> {
        return create.selectFrom(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId).and(COLLEGE_ROLE.ALLOW_AGENT.eq(allowAgent)))
                .fetch()
    }

    override fun findByRoleId(roleId: String): Optional<Record> {
        return create.select()
                .from(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.ROLE_ID.eq(roleId))
                .fetchOptional()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(collegeRole: CollegeRole) {
        collegeRoleDao.insert(collegeRole)
    }

    override fun update(collegeRole: CollegeRole) {
        collegeRoleDao.update(collegeRole)
    }

    override fun deleteByRoleId(roleId: String) {
        create.deleteFrom(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.ROLE_ID.eq(roleId))
                .execute()
    }

}