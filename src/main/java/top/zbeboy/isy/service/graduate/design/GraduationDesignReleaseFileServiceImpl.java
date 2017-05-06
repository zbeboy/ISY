package top.zbeboy.isy.service.graduate.design;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignReleaseFile;

import static top.zbeboy.isy.domain.Tables.FILES;
import static top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_RELEASE_FILE;

/**
 * Created by zbeboy on 2017/5/5.
 */
@Service("graduationDesignReleaseFileService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignReleaseFileServiceImpl implements GraduationDesignReleaseFileService {

    private final Logger log = LoggerFactory.getLogger(GraduationDesignReleaseFileServiceImpl.class);

    private final DSLContext create;

    @Autowired
    public GraduationDesignReleaseFileServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<Record> findByGraduationDesignReleaseId(String graduationDesignReleaseId) {
        return create.select()
                .from(GRADUATION_DESIGN_RELEASE_FILE)
                .join(FILES)
                .on(GRADUATION_DESIGN_RELEASE_FILE.FILE_ID.eq(FILES.FILE_ID))
                .where(GRADUATION_DESIGN_RELEASE_FILE.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignReleaseFile graduationDesignReleaseFile) {
        create.insertInto(GRADUATION_DESIGN_RELEASE_FILE)
                .set(GRADUATION_DESIGN_RELEASE_FILE.FILE_ID, graduationDesignReleaseFile.getFileId())
                .set(GRADUATION_DESIGN_RELEASE_FILE.GRADUATION_DESIGN_RELEASE_ID, graduationDesignReleaseFile.getGraduationDesignReleaseId())
                .execute();
    }

    @Override
    public void deleteByFileIdAndGraduationDesignReleaseId(String fileId, String graduationDesignReleaseId) {
        create.deleteFrom(GRADUATION_DESIGN_RELEASE_FILE)
                .where(GRADUATION_DESIGN_RELEASE_FILE.FILE_ID.eq(fileId)
                        .and(GRADUATION_DESIGN_RELEASE_FILE.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .execute();
    }

    @Override
    public void deleteByGraduationDesignReleaseId(String graduationDesignReleaseId) {
        create.deleteFrom(GRADUATION_DESIGN_RELEASE_FILE)
                .where(GRADUATION_DESIGN_RELEASE_FILE.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .execute();
    }
}
