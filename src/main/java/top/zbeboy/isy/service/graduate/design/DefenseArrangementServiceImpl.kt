package top.zbeboy.isy.service.graduate.design

import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import top.zbeboy.isy.domain.Tables.DEFENSE_ARRANGEMENT
import top.zbeboy.isy.domain.tables.daos.DefenseArrangementDao
import top.zbeboy.isy.domain.tables.pojos.DefenseArrangement
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-02-06 .
 **/
@Service("defenseArrangementService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class DefenseArrangementServiceImpl @Autowired constructor(dslContext: DSLContext) : DefenseArrangementService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var defenseArrangementDao: DefenseArrangementDao

    override fun findById(id: String): DefenseArrangement {
        return defenseArrangementDao.findById(id)
    }

    override fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): Optional<Record> {
        return create.select()
                .from(DEFENSE_ARRANGEMENT)
                .where(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .fetchOptional()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(defenseArrangement: DefenseArrangement) {
        defenseArrangementDao.insert(defenseArrangement)
    }

    override fun update(defenseArrangement: DefenseArrangement) {
        defenseArrangementDao.update(defenseArrangement)
    }
}