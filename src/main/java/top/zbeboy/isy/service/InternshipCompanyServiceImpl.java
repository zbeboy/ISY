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
import top.zbeboy.isy.domain.tables.daos.InternshipCompanyDao;

import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.INTERNSHIP_COMPANY;

/**
 * Created by lenovo on 2016-11-27.
 */
@Service("internshipCompanyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipCompanyServiceImpl implements InternshipCompanyService {

    private final Logger log = LoggerFactory.getLogger(InternshipCompanyServiceImpl.class);

    private final DSLContext create;

    private InternshipCompanyDao internshipCompanyDao;

    @Autowired
    public InternshipCompanyServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.internshipCompanyDao = new InternshipCompanyDao(configuration);
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(INTERNSHIP_COMPANY)
                .where(INTERNSHIP_COMPANY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_COMPANY.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }
}
