package top.zbeboy.isy.service.data

import org.jooq.DSLContext
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.tables.pojos.CollegeApplication
import top.zbeboy.isy.domain.tables.records.CollegeApplicationRecord

import top.zbeboy.isy.domain.Tables.COLLEGE_APPLICATION
/**
 * Created by zbeboy 2017-12-02 .
 **/
@Service("collegeApplicationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class CollegeApplicationServiceImpl @Autowired constructor(dslContext: DSLContext) :CollegeApplicationService {

    private val create: DSLContext = dslContext

    override fun deleteByApplicationId(applicationId: String) {
        create.deleteFrom<CollegeApplicationRecord>(COLLEGE_APPLICATION)
                .where(COLLEGE_APPLICATION.APPLICATION_ID.eq(applicationId))
                .execute()
    }

    override fun findByCollegeId(collegeId: Int): Result<CollegeApplicationRecord> {
        return create.selectFrom<CollegeApplicationRecord>(COLLEGE_APPLICATION)
                .where(COLLEGE_APPLICATION.COLLEGE_ID.eq(collegeId))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(collegeApplication: CollegeApplication) {
        create.insertInto<CollegeApplicationRecord>(COLLEGE_APPLICATION)
                .set(COLLEGE_APPLICATION.COLLEGE_ID, collegeApplication.collegeId)
                .set(COLLEGE_APPLICATION.APPLICATION_ID, collegeApplication.applicationId)
                .execute()
    }

    override fun deleteByCollegeId(collegeId: Int) {
        create.deleteFrom<CollegeApplicationRecord>(COLLEGE_APPLICATION)
                .where(COLLEGE_APPLICATION.COLLEGE_ID.eq(collegeId))
                .execute()
    }
}