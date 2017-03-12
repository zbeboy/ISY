package top.zbeboy.isy.service.internship;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.InternshipChangeCompanyHistoryDao;
import top.zbeboy.isy.domain.tables.pojos.InternshipChangeCompanyHistory;

import javax.annotation.Resource;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-12-12.
 */
@Service("internshipChangeCompanyHistoryService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipChangeCompanyHistoryServiceImpl implements InternshipChangeCompanyHistoryService {

    private final Logger log = LoggerFactory.getLogger(InternshipChangeCompanyHistoryServiceImpl.class);

    private final DSLContext create;

    @Resource
    private InternshipChangeCompanyHistoryDao internshipChangeCompanyHistoryDao;

    @Autowired
    public InternshipChangeCompanyHistoryServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(InternshipChangeCompanyHistory internshipChangeCompanyHistory) {
        internshipChangeCompanyHistoryDao.insert(internshipChangeCompanyHistory);
    }

    @Override
    public void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        create.deleteFrom(INTERNSHIP_CHANGE_COMPANY_HISTORY)
                .where(INTERNSHIP_CHANGE_COMPANY_HISTORY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_CHANGE_COMPANY_HISTORY.STUDENT_ID.eq(studentId)))
                .execute();
    }

    @Override
    public Result<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(INTERNSHIP_CHANGE_COMPANY_HISTORY)
                .join(STUDENT)
                .on(INTERNSHIP_CHANGE_COMPANY_HISTORY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(INTERNSHIP_RELEASE)
                .on(INTERNSHIP_CHANGE_COMPANY_HISTORY.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                .where(INTERNSHIP_CHANGE_COMPANY_HISTORY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_CHANGE_COMPANY_HISTORY.STUDENT_ID.eq(studentId)))
                .orderBy(INTERNSHIP_CHANGE_COMPANY_HISTORY.CHANGE_TIME.desc())
                .fetch();
    }
}
