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
import top.zbeboy.isy.domain.tables.daos.GraduationPracticeCollegeDao;
import top.zbeboy.isy.domain.tables.daos.InternshipCollegeDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege;

import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-11-27.
 */
@Service("graduationPracticeCollegeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationPracticeCollegeServiceImpl implements GraduationPracticeCollegeService {

    private final Logger log = LoggerFactory.getLogger(GraduationPracticeCollegeServiceImpl.class);

    private final DSLContext create;

    private GraduationPracticeCollegeDao graduationPracticeCollegeDao;

    @Autowired
    public GraduationPracticeCollegeServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.graduationPracticeCollegeDao = new GraduationPracticeCollegeDao(configuration);
    }

    @Override
    public GraduationPracticeCollege findById(String id) {
        return graduationPracticeCollegeDao.findById(id);
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(GRADUATION_PRACTICE_COLLEGE)
                .where(GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_COLLEGE.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }

    @Override
    public Result<Record> findByInternshipReleaseIdAndStudentIdRelation(String internshipReleaseId, int studentId) {
        return create.select()
                .from(GRADUATION_PRACTICE_COLLEGE)
                .join(STUDENT)
                .on(GRADUATION_PRACTICE_COLLEGE.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_COLLEGE.STUDENT_ID.eq(studentId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationPracticeCollege graduationPracticeCollege) {
        graduationPracticeCollegeDao.insert(graduationPracticeCollege);
    }

    @Override
    public void update(GraduationPracticeCollege graduationPracticeCollege) {
        graduationPracticeCollegeDao.update(graduationPracticeCollege);
    }

    @Override
    public void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        create.deleteFrom(GRADUATION_PRACTICE_COLLEGE)
                .where(GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_COLLEGE.STUDENT_ID.eq(studentId)))
                .execute();
    }
}
