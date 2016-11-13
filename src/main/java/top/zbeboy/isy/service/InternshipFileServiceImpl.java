package top.zbeboy.isy.service;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.InternshipFile;

import static top.zbeboy.isy.domain.Tables.INTERNSHIP_FILE;
/**
 * Created by lenovo on 2016-11-13.
 */
@Service("internshipFileService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipFileServiceImpl implements InternshipFileService {

    private final Logger log = LoggerFactory.getLogger(InternshipFileServiceImpl.class);

    private final DSLContext create;

    @Autowired
    public InternshipFileServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(InternshipFile internshipFile) {
        create.insertInto(INTERNSHIP_FILE)
                .set(INTERNSHIP_FILE.FILE_ID,internshipFile.getFileId())
                .set(INTERNSHIP_FILE.INTERNSHIP_RELEASE_ID,internshipFile.getInternshipReleaseId())
                .execute();
    }
}
