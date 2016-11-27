package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.InternshipCollegeDao;
import top.zbeboy.isy.domain.tables.daos.InternshipReleaseDao;
import top.zbeboy.isy.domain.tables.pojos.InternshipCollege;

import javax.annotation.Resource;

import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.INTERNSHIP_COLLEGE;

/**
 * Created by lenovo on 2016-11-27.
 */
@Service("internshipCollegeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipCollegeServiceImpl implements InternshipCollegeService {

    private final Logger log = LoggerFactory.getLogger(InternshipCollegeServiceImpl.class);

    private final DSLContext create;

    private InternshipCollegeDao internshipCollegeDao;

    @Autowired
    public InternshipCollegeServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.internshipCollegeDao = new InternshipCollegeDao(configuration);
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(INTERNSHIP_COLLEGE)
                .where(INTERNSHIP_COLLEGE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_COLLEGE.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }
}
