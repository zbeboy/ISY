package top.zbeboy.isy.service.internship

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.InternshipChangeCompanyHistoryDao
import top.zbeboy.isy.domain.tables.pojos.InternshipChangeCompanyHistory
import top.zbeboy.isy.domain.tables.records.InternshipChangeCompanyHistoryRecord
import java.sql.Timestamp
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-27 .
 **/
@Service("internshipChangeCompanyHistoryService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipChangeCompanyHistoryServiceImpl @Autowired constructor(dslContext: DSLContext) : InternshipChangeCompanyHistoryService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var internshipChangeCompanyHistoryDao: InternshipChangeCompanyHistoryDao


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(internshipChangeCompanyHistory: InternshipChangeCompanyHistory) {
        internshipChangeCompanyHistoryDao.insert(internshipChangeCompanyHistory)
    }

    override fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int) {
        create.deleteFrom<InternshipChangeCompanyHistoryRecord>(INTERNSHIP_CHANGE_COMPANY_HISTORY)
                .where(INTERNSHIP_CHANGE_COMPANY_HISTORY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_CHANGE_COMPANY_HISTORY.STUDENT_ID.eq(studentId)))
                .execute()
    }

    override fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Result<Record> {
        return create.select()
                .from(INTERNSHIP_CHANGE_COMPANY_HISTORY)
                .join(STUDENT)
                .on(INTERNSHIP_CHANGE_COMPANY_HISTORY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(INTERNSHIP_RELEASE)
                .on(INTERNSHIP_CHANGE_COMPANY_HISTORY.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                .where(INTERNSHIP_CHANGE_COMPANY_HISTORY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_CHANGE_COMPANY_HISTORY.STUDENT_ID.eq(studentId)))
                .orderBy<Timestamp>(INTERNSHIP_CHANGE_COMPANY_HISTORY.CHANGE_TIME.desc())
                .fetch()
    }
}