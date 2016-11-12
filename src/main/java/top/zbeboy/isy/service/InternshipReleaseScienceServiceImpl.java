package top.zbeboy.isy.service;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;

import java.util.List;

import static top.zbeboy.isy.domain.Tables.AUTHORITIES;
import static top.zbeboy.isy.domain.Tables.INTERNSHIP_RELEASE_SCIENCE;

/**
 * Created by lenovo on 2016-11-12.
 */
@Service("internshipReleaseScienceService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipReleaseScienceServiceImpl implements InternshipReleaseScienceService {

    private final Logger log = LoggerFactory.getLogger(InternshipReleaseScienceServiceImpl.class);

    private final DSLContext create;

    @Autowired
    public InternshipReleaseScienceServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public void save(String internshipReleaseId, int scienceId) {
        create.insertInto(INTERNSHIP_RELEASE_SCIENCE)
                .set(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID,internshipReleaseId)
                .set(INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID,scienceId)
                .execute();

    }
}
