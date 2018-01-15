package top.zbeboy.isy.service.graduate.design

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import top.zbeboy.isy.domain.Tables.FILES
import top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_RELEASE_FILE
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignReleaseFile
import top.zbeboy.isy.domain.tables.records.GraduationDesignReleaseFileRecord

/**
 * Created by zbeboy 2018-01-15 .
 **/
@Service("graduationDesignReleaseFileService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignReleaseFileServiceImpl @Autowired constructor(dslContext: DSLContext) : GraduationDesignReleaseFileService {

    private val create: DSLContext = dslContext


    override fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): Result<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_RELEASE_FILE)
                .join(FILES)
                .on(GRADUATION_DESIGN_RELEASE_FILE.FILE_ID.eq(FILES.FILE_ID))
                .where(GRADUATION_DESIGN_RELEASE_FILE.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignReleaseFile: GraduationDesignReleaseFile) {
        create.insertInto<GraduationDesignReleaseFileRecord>(GRADUATION_DESIGN_RELEASE_FILE)
                .set(GRADUATION_DESIGN_RELEASE_FILE.FILE_ID, graduationDesignReleaseFile.fileId)
                .set(GRADUATION_DESIGN_RELEASE_FILE.GRADUATION_DESIGN_RELEASE_ID, graduationDesignReleaseFile.graduationDesignReleaseId)
                .execute()
    }

    override fun deleteByFileIdAndGraduationDesignReleaseId(fileId: String, graduationDesignReleaseId: String) {
        create.deleteFrom<GraduationDesignReleaseFileRecord>(GRADUATION_DESIGN_RELEASE_FILE)
                .where(GRADUATION_DESIGN_RELEASE_FILE.FILE_ID.eq(fileId)
                        .and(GRADUATION_DESIGN_RELEASE_FILE.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .execute()
    }

    override fun deleteByGraduationDesignReleaseId(graduationDesignReleaseId: String) {
        create.deleteFrom<GraduationDesignReleaseFileRecord>(GRADUATION_DESIGN_RELEASE_FILE)
                .where(GRADUATION_DESIGN_RELEASE_FILE.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .execute()
    }
}