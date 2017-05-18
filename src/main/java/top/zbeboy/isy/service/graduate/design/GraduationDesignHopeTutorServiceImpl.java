package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignHopeTutor;
import top.zbeboy.isy.domain.tables.records.GraduationDesignHopeTutorRecord;

import static top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_HOPE_TUTOR;

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
    public int countByStudentId(int studentId) {
        Record1<Integer> count = create.selectCount()
                .from(GRADUATION_DESIGN_HOPE_TUTOR)
                .where(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID.eq(studentId))
                .fetchOne();
        return count.value1();
    }

    @Override
    public Result<GraduationDesignHopeTutorRecord> findByStudentId(int studentId) {
        return create.selectFrom(GRADUATION_DESIGN_HOPE_TUTOR)
                .where(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID.eq(studentId))
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
