package top.zbeboy.isy.service.graduate.design

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignHopeTutor

/**
 * Created by zbeboy 2018-01-19 .
 **/
@Service("graduationDesignHopeTutorService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignHopeTutorServiceImpl @Autowired constructor(dslContext: DSLContext) : GraduationDesignHopeTutorService {

    private val create: DSLContext = dslContext


    override fun countByStudentIdAndGraduationDesignReleaseId(studentId: Int, graduationDesignReleaseId: String): Int {
        val count = create.selectCount()
                .from(GRADUATION_DESIGN_HOPE_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetchOne()
        return count.value1()
    }

    override fun findByStudentIdAndGraduationDesignReleaseId(studentId: Int, graduationDesignReleaseId: String): Result<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_HOPE_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetch()
    }

    override fun findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(studentId: Int, graduationDesignReleaseId: String): Result<Record> {
        return create.select()
                .from(GRADUATION_DESIGN_HOPE_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetch()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(graduationDesignHopeTutor: GraduationDesignHopeTutor) {
        create.insertInto(GRADUATION_DESIGN_HOPE_TUTOR)
                .set(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID, graduationDesignHopeTutor.graduationDesignTeacherId)
                .set(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID, graduationDesignHopeTutor.studentId)
                .execute()
    }

    override fun delete(graduationDesignHopeTutor: GraduationDesignHopeTutor) {
        create.deleteFrom(GRADUATION_DESIGN_HOPE_TUTOR)
                .where(GRADUATION_DESIGN_HOPE_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignHopeTutor.graduationDesignTeacherId)
                        .and(GRADUATION_DESIGN_HOPE_TUTOR.STUDENT_ID.eq(graduationDesignHopeTutor.studentId)))
                .execute()
    }
}