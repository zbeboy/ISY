package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignHopeTutor;
import top.zbeboy.isy.domain.tables.records.GraduationDesignHopeTutorRecord;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zbeboy on 2017/5/17.
 */
@Slf4j
@Service("graduationDesignHopeTutorService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignHopeTutorServiceImpl implements GraduationDesignHopeTutorService {

    private final DSLContext create;

    @Autowired
    public GraduationDesignHopeTutorServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public int countByStudentIdAndGraduationDesignReleaseId(int studentId, String graduationDesignReleaseId) {
        Record1<Integer> count = create.selectCount()
                .from(GRADUATION_DESIGN_HOPE_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetchOne();
        return count.value1();
    }

    @Override
    public Result<Record> findByStudentIdAndGraduationDesignReleaseId(int studentId, String graduationDesignReleaseId) {
        return create.select()
                .from(GRADUATION_DESIGN_HOPE_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetch();
    }

    @Override
    public Result<Record> findByStudentIdAndGraduationDesignTeacherIdRelationForStaff(int studentId, String graduationDesignTeacherId) {
        return create.select()
                .from(GRADUATION_DESIGN_HOPE_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignHopeTutor graduationDesignHopeTutor) {
        create.insertInto(GRADUATION_DESIGN_HOPE_TUTOR)
                .set(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID, graduationDesignHopeTutor.getGraduationDesignTeacherId())
                .set(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID, graduationDesignHopeTutor.getStudentId())
                .execute();
    }

    @Override
    public void delete(GraduationDesignHopeTutor graduationDesignHopeTutor) {
        create.deleteFrom(GRADUATION_DESIGN_HOPE_TUTOR)
                .where(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignHopeTutor.getGraduationDesignTeacherId())
                        .and(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID.eq(graduationDesignHopeTutor.getStudentId())))
                .execute();
    }
}
