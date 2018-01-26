package top.zbeboy.isy.service.graduate.design

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_DECLARE_DATA
import top.zbeboy.isy.domain.tables.daos.GraduationDesignDeclareDataDao
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclareData
import top.zbeboy.isy.domain.tables.records.GraduationDesignDeclareDataRecord
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-26 .
 **/
@Service("graduationDesignDeclareDataService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignDeclareDataServiceImpl @Autowired constructor(dslContext: DSLContext) : GraduationDesignDeclareDataService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var graduationDesignDeclareDataDao: GraduationDesignDeclareDataDao

    override fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): GraduationDesignDeclareData? {
        return graduationDesignDeclareDataDao.fetchOne(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DESIGN_RELEASE_ID, graduationDesignReleaseId)
    }

    override fun deleteByGraduationDesignReleaseId(graduationDesignReleaseId: String) {
        create.deleteFrom<GraduationDesignDeclareDataRecord>(GRADUATION_DESIGN_DECLARE_DATA)
                .where(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .execute()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignDeclareData: GraduationDesignDeclareData) {
        graduationDesignDeclareDataDao.insert(graduationDesignDeclareData)
    }
}