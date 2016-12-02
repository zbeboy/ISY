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
import top.zbeboy.isy.domain.tables.pojos.InternshipCompany;

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
    public InternshipCompany findById(String id) {
        return internshipCompanyDao.findById(id);
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(INTERNSHIP_COMPANY)
                .where(INTERNSHIP_COMPANY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_COMPANY.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(InternshipCompany internshipCompany) {
        internshipCompanyDao.insert(internshipCompany);
    }

    @Override
    public void update(InternshipCompany internshipCompany) {
        internshipCompanyDao.update(internshipCompany);
    }

    @Override
    public void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        create.deleteFrom(INTERNSHIP_COMPANY)
                .where(INTERNSHIP_COMPANY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_COMPANY.STUDENT_ID.eq(studentId)))
                .execute();
    }
}
