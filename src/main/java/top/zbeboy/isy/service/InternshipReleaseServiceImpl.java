package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.InternshipReleaseDao;
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease;

import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-11-12.
 */
@Service("internshipReleaseService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipReleaseServiceImpl implements InternshipReleaseService {

    private final Logger log = LoggerFactory.getLogger(InternshipReleaseServiceImpl.class);

    private final DSLContext create;

    private InternshipReleaseDao internshipTypeDao;

    @Autowired
    public InternshipReleaseServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.internshipTypeDao = new InternshipReleaseDao(configuration);
    }

    @Override
    public List<InternshipRelease> findByReleaseTitle(String releaseTitle) {
        return internshipTypeDao.fetchByInternshipTitle(releaseTitle);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(InternshipRelease internshipRelease) {
        internshipTypeDao.insert(internshipRelease);
    }

    @Override
    public Result<Record> findAllByPage() {
        return create.select()
                .from(INTERNSHIP_RELEASE)
                .join(USERS)
                .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                .join(DEPARTMENT)
                .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                .fetch();

    }
}
