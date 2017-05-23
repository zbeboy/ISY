package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.GraduationDesignTutorDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTutor;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;

import javax.annotation.Resource;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2017-05-20.
 */
@Slf4j
@Service("graduationDesignTutorService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignTutorServiceImpl implements GraduationDesignTutorService {

    private final DSLContext create;

    @Resource
    private GraduationDesignTutorDao graduationDesignTutorDao;

    @Autowired
    public GraduationDesignTutorServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public GraduationDesignTutor findById(String id) {
        return graduationDesignTutorDao.findById(id);
    }

    @Override
    public Optional<Record> findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(int studentId, String graduationDesignReleaseId) {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetchOptional();
    }

    @Override
    public Result<Record> findByGraduationDesignTeacherIdAndGraduationDesignReleaseIdRelationForStudent(String graduationDesignTeacherId, String graduationDesignReleaseId) {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STUDENT)
                .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .where(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId)
                        .and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetch();
    }

    @Override
    public int countNotFillStudent(GraduationDesignReleaseBean graduationDesignReleaseBean) {
        Select<Record> select = create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseBean.getGraduationDesignReleaseId()).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID)));
        Record1<Integer> count = create.selectCount()
                .from(STUDENT)
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .where(SCIENCE.SCIENCE_ID.eq(graduationDesignReleaseBean.getScienceId()).and(ORGANIZE.GRADE.eq(graduationDesignReleaseBean.getAllowGrade()))
                        .andNotExists(select))
                .fetchOne();
        return count.value1();
    }

    @Override
    public int countFillStudent(GraduationDesignReleaseBean graduationDesignReleaseBean) {
        Record1<Integer> count = create.selectCount()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseBean.getGraduationDesignReleaseId()))
                .fetchOne();
        return count.value1();
    }

    @Override
    public void deleteByGraduationDesignTeacherId(String graduationDesignTeacherId) {
        create.deleteFrom(GRADUATION_DESIGN_TUTOR)
                .where(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId))
                .execute();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignTutor graduationDesignTutor) {
        graduationDesignTutorDao.insert(graduationDesignTutor);
    }

    @Override
    public void update(GraduationDesignTutor graduationDesignTutor) {
        graduationDesignTutorDao.update(graduationDesignTutor);
    }
}
