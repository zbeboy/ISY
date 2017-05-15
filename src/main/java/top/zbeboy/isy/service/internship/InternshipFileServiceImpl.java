package top.zbeboy.isy.service.internship;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.InternshipFile;

import static top.zbeboy.isy.domain.Tables.FILES;
import static top.zbeboy.isy.domain.Tables.INTERNSHIP_FILE;

/**
 * Created by lenovo on 2016-11-13.
 */
@Slf4j
@Service("internshipFileService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipFileServiceImpl implements InternshipFileService {

    private final DSLContext create;

    @Autowired
    public InternshipFileServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(InternshipFile internshipFile) {
        create.insertInto(INTERNSHIP_FILE)
                .set(INTERNSHIP_FILE.FILE_ID, internshipFile.getFileId())
                .set(INTERNSHIP_FILE.INTERNSHIP_RELEASE_ID, internshipFile.getInternshipReleaseId())
                .execute();
    }

    @Override
    public Result<Record> findByInternshipReleaseId(String internshipReleaseId) {
        return create.select()
                .from(INTERNSHIP_FILE)
                .join(FILES)
                .on(INTERNSHIP_FILE.FILE_ID.eq(FILES.FILE_ID))
                .where(INTERNSHIP_FILE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetch();
    }

    @Override
    public void deleteByInternshipReleaseId(String internshipReleaseId) {
        create.deleteFrom(INTERNSHIP_FILE)
                .where(INTERNSHIP_FILE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .execute();
    }

    @Override
    public void deleteByFileIdAndInternshipReleaseId(String fileId, String internshipReleaseId) {
        create.deleteFrom(INTERNSHIP_FILE)
                .where(INTERNSHIP_FILE.FILE_ID.eq(fileId).and(INTERNSHIP_FILE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)))
                .execute();
    }
}
