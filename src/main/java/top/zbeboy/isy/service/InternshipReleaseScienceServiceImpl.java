package top.zbeboy.isy.service;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.records.InternshipReleaseScienceRecord;

import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.INTERNSHIP_RELEASE_SCIENCE;
import static top.zbeboy.isy.domain.Tables.SCIENCE;

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

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(String internshipReleaseId, int scienceId) {
        create.insertInto(INTERNSHIP_RELEASE_SCIENCE)
                .set(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID, internshipReleaseId)
                .set(INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID, scienceId)
                .execute();

    }

    @Override
    public Result<Record> findByInternshipReleaseIdRelation(String internshipReleaseId) {
        return create.select()
                .from(INTERNSHIP_RELEASE_SCIENCE)
                .join(SCIENCE)
                .on(INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .where(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetch();
    }

    @Override
    public Result<InternshipReleaseScienceRecord> findByInternshipReleaseId(String internshipReleaseId) {
        return create.selectFrom(INTERNSHIP_RELEASE_SCIENCE)
                .where(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetch();
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndScienceId(String internshipReleaseId, int scienceId) {
        return create.select()
                .from(INTERNSHIP_RELEASE_SCIENCE)
                .where(INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId)
                        .and(INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID.eq(scienceId)))
                .fetchOptional();
    }
}