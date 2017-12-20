package top.zbeboy.isy.service.internship

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import top.zbeboy.isy.domain.Tables.FILES
import top.zbeboy.isy.domain.Tables.INTERNSHIP_FILE
import top.zbeboy.isy.domain.tables.pojos.InternshipFile
import top.zbeboy.isy.domain.tables.records.InternshipFileRecord

/**
 * Created by zbeboy 2017-12-20 .
 **/
@Service("internshipFileService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class InternshipFileServiceImpl @Autowired constructor(dslContext: DSLContext) : InternshipFileService{

    private val create: DSLContext = dslContext


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(internshipFile: InternshipFile) {
        create.insertInto<InternshipFileRecord>(INTERNSHIP_FILE)
                .set(INTERNSHIP_FILE.FILE_ID, internshipFile.fileId)
                .set(INTERNSHIP_FILE.INTERNSHIP_RELEASE_ID, internshipFile.internshipReleaseId)
                .execute()
    }

    override fun findByInternshipReleaseId(internshipReleaseId: String): Result<Record> {
        return create.select()
                .from(INTERNSHIP_FILE)
                .join(FILES)
                .on(INTERNSHIP_FILE.FILE_ID.eq(FILES.FILE_ID))
                .where(INTERNSHIP_FILE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetch()
    }

    override fun deleteByInternshipReleaseId(internshipReleaseId: String) {
        create.deleteFrom<InternshipFileRecord>(INTERNSHIP_FILE)
                .where(INTERNSHIP_FILE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .execute()
    }

    override fun deleteByFileIdAndInternshipReleaseId(fileId: String, internshipReleaseId: String) {
        create.deleteFrom<InternshipFileRecord>(INTERNSHIP_FILE)
                .where(INTERNSHIP_FILE.FILE_ID.eq(fileId).and(INTERNSHIP_FILE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)))
                .execute()
    }
}