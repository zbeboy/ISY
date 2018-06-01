package top.zbeboy.isy.service.internship

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.InternshipChangeHistoryDao
import top.zbeboy.isy.domain.tables.pojos.InternshipChangeHistory
import top.zbeboy.isy.domain.tables.records.InternshipChangeHistoryRecord
import java.sql.Timestamp
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-27 .
 **/
@Service("internshipChangeHistoryService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipChangeHistoryServiceImpl @Autowired constructor(dslContext: DSLContext) : InternshipChangeHistoryService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var internshipChangeHistoryDao: InternshipChangeHistoryDao


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(internshipChangeHistory: InternshipChangeHistory) {
        internshipChangeHistoryDao.insert(internshipChangeHistory)
    }

    override fun deleteByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int) {
        create.deleteFrom<InternshipChangeHistoryRecord>(INTERNSHIP_CHANGE_HISTORY)
                .where(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_CHANGE_HISTORY.STUDENT_ID.eq(studentId)))
                .execute()
    }

    override fun findByInternshipReleaseIdAndStudentId(internshipReleaseId: String, studentId: Int): Result<Record> {
        return create.select()
                .from(INTERNSHIP_CHANGE_HISTORY)
                .join(STUDENT)
                .on(INTERNSHIP_CHANGE_HISTORY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(INTERNSHIP_RELEASE)
                .on(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                .where(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_CHANGE_HISTORY.STUDENT_ID.eq(studentId)))
                .orderBy<Timestamp>(INTERNSHIP_CHANGE_HISTORY.APPLY_TIME.desc())
                .fetch()
    }
}