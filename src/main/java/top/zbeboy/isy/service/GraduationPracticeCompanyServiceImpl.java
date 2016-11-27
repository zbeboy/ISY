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
import top.zbeboy.isy.domain.tables.daos.GraduationPracticeCompanyDao;
import top.zbeboy.isy.domain.tables.daos.InternshipCollegeDao;

import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.GRADUATION_PRACTICE_COMPANY;
/**
 * Created by lenovo on 2016-11-27.
 */
@Service("graduationPracticeCompanyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationPracticeCompanyServiceImpl implements GraduationPracticeCompanyService {

    private final Logger log = LoggerFactory.getLogger(GraduationPracticeCompanyServiceImpl.class);

    private final DSLContext create;

    private GraduationPracticeCompanyDao graduationPracticeCompanyDao;

    @Autowired
    public GraduationPracticeCompanyServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.graduationPracticeCompanyDao = new GraduationPracticeCompanyDao(configuration);
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(GRADUATION_PRACTICE_COMPANY)
                .where(GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_COMPANY.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }
}
