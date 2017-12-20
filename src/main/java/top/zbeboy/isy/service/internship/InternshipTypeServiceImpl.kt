package top.zbeboy.isy.service.internship

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.tables.daos.InternshipTypeDao
import top.zbeboy.isy.domain.tables.pojos.InternshipType
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-20 .
 **/
@Service("internshipTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipTypeServiceImpl @Autowired constructor(dslContext: DSLContext) : InternshipTypeService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var internshipTypeDao: InternshipTypeDao


    override fun findAll(): List<InternshipType> {
        return internshipTypeDao.findAll()
    }

    override fun findByInternshipTypeId(internshipTypeId: Int): InternshipType {
        return internshipTypeDao.findById(internshipTypeId)
    }
}