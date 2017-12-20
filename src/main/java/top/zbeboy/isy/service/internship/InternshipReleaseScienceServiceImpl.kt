package top.zbeboy.isy.service.internship

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.records.InternshipReleaseScienceRecord
import java.util.*

/**
 * Created by zbeboy 2017-12-20 .
 **/
@Service("internshipReleaseScienceService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipReleaseScienceServiceImpl @Autowired constructor(dslContext: DSLContext) :InternshipReleaseScienceService {

    private val create: DSLContext = dslContext


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(internshipReleaseId: String, scienceId: Int) {
        create.insertInto(INTERNSHIP_RELEASE_SCIENCE)
                .set(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID, internshipReleaseId)
                .set(INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID, scienceId)
                .execute()

    }

    override fun findByInternshipReleaseIdRelation(internshipReleaseId: String): Result<Record> {
        return create.select()
                .from(INTERNSHIP_RELEASE_SCIENCE)
                .join(SCIENCE)
                .on(INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .where(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetch()
    }

    override fun findByInternshipReleaseId(internshipReleaseId: String): Result<InternshipReleaseScienceRecord> {
        return create.selectFrom(INTERNSHIP_RELEASE_SCIENCE)
                .where(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetch()
    }

    override fun findInScienceIdAndGradeNeInternshipReleaseId(grade: String, scienceIds: List<Int>, internshipReleaseId: String): Result<Record> {
        return create.select()
                .from(INTERNSHIP_RELEASE_SCIENCE)
                .join(INTERNSHIP_RELEASE)
                .on(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                .where(INTERNSHIP_RELEASE.ALLOW_GRADE.eq(grade).and(INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID.`in`(scienceIds)).and(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID.ne(internshipReleaseId)))
                .fetch()
    }

    override fun findByInternshipReleaseIdAndScienceId(internshipReleaseId: String, scienceId: Int): Optional<Record> {
        return create.select()
                .from(INTERNSHIP_RELEASE_SCIENCE)
                .where(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)
                        .and(INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID.eq(scienceId)))
                .fetchOptional()
    }
}