package top.zbeboy.isy.service.graduate.design

import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember
import top.zbeboy.isy.domain.tables.records.DefenseGroupMemberRecord
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupMemberBean
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean
import java.util.*

/**
 * Created by zbeboy 2018-02-06 .
 **/
@Service("defenseGroupMemberService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class DefenseGroupMemberServiceImpl @Autowired constructor(dslContext: DSLContext) : DefenseGroupMemberService {

    private val create: DSLContext = dslContext

    override fun findByGraduationDesignReleaseIdRelationForStaff(condition: GraduationDesignTeacherBean): List<GraduationDesignTeacherBean> {
        val graduationDesignTeacherBeens = ArrayList<GraduationDesignTeacherBean>()
        val records: Result<Record>
        var a = searchCondition(condition)
        a = otherCondition(a, condition)
        records = if (ObjectUtils.isEmpty(a)) {
            create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(DEFENSE_GROUP_MEMBER)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID))
                    .leftJoin(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID))
                    .orderBy(STAFF.STAFF_NUMBER)
                    .fetch()
        } else {
            create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .leftJoin(DEFENSE_GROUP_MEMBER)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID))
                    .leftJoin(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID))
                    .where(a)
                    .orderBy(STAFF.STAFF_NUMBER)
                    .fetch()
        }

        for (r in records) {
            val graduationDesignTeacherBean = GraduationDesignTeacherBean()
            graduationDesignTeacherBean.graduationDesignTeacherId = r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID)
            graduationDesignTeacherBean.staffRealName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)
            graduationDesignTeacherBean.staffUsername = r.getValue(USERS.USERNAME)
            graduationDesignTeacherBean.staffNumber = r.getValue(STAFF.STAFF_NUMBER)
            graduationDesignTeacherBean.staffMobile = r.getValue(USERS.MOBILE)
            graduationDesignTeacherBean.defenseGroupId = r.getValue(DEFENSE_GROUP.DEFENSE_GROUP_ID)
            graduationDesignTeacherBean.defenseGroupName = r.getValue(DEFENSE_GROUP.DEFENSE_GROUP_NAME)
            graduationDesignTeacherBean.leaderId = r.getValue(DEFENSE_GROUP.LEADER_ID)
            graduationDesignTeacherBean.note = r.getValue(DEFENSE_GROUP_MEMBER.NOTE)

            graduationDesignTeacherBeens.add(graduationDesignTeacherBean)
        }
        return graduationDesignTeacherBeens
    }

    override fun findByGraduationDesignTeacherId(graduationDesignTeacherId: String): DefenseGroupMemberRecord {
        return create.selectFrom(DEFENSE_GROUP_MEMBER)
                .where(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId))
                .fetchOne()
    }

    override fun findByDefenseGroupIdAndGraduationDesignReleaseIdForStudent(defenseGroupId: String, graduationDesignReleaseId: String): List<DefenseGroupMemberBean> {
        val defenseGroupMemberBeans = ArrayList<DefenseGroupMemberBean>()
        val records = create.select()
                .from(DEFENSE_GROUP_MEMBER)
                .join(GRADUATION_DESIGN_TUTOR)
                .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                .join(STUDENT)
                .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .leftJoin(GRADUATION_DESIGN_PRESUBJECT)
                .on(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID.eq(STUDENT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .fetch()
        for (r in records) {
            val defenseGroupMemberBean = DefenseGroupMemberBean()
            defenseGroupMemberBean.defenseGroupId = r.getValue(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID)
            defenseGroupMemberBean.graduationDesignTeacherId = r.getValue(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID)
            defenseGroupMemberBean.note = r.getValue(DEFENSE_GROUP_MEMBER.NOTE)
            defenseGroupMemberBean.studentNumber = r.getValue(STUDENT.STUDENT_NUMBER)
            defenseGroupMemberBean.studentName = r.getValue(USERS.REAL_NAME)
            defenseGroupMemberBean.studentMobile = r.getValue(USERS.MOBILE)
            defenseGroupMemberBean.subject = r.getValue(GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE)
            defenseGroupMemberBean.staffName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)
            defenseGroupMemberBean.studentId = r.getValue(STUDENT.STUDENT_ID)

            defenseGroupMemberBeans.add(defenseGroupMemberBean)
        }
        return defenseGroupMemberBeans
    }

    override fun findByDefenseGroupIdForStaff(defenseGroupId: String): List<DefenseGroupMemberBean> {
        val defenseGroupMemberBeans = ArrayList<DefenseGroupMemberBean>()
        val records = create.select()
                .from(DEFENSE_GROUP_MEMBER)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .fetch()
        for (r in records) {
            val defenseGroupMemberBean = DefenseGroupMemberBean()
            defenseGroupMemberBean.defenseGroupId = r.getValue(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID)
            defenseGroupMemberBean.graduationDesignTeacherId = r.getValue(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID)
            defenseGroupMemberBean.note = r.getValue(DEFENSE_GROUP_MEMBER.NOTE)
            defenseGroupMemberBean.staffName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)

            defenseGroupMemberBeans.add(defenseGroupMemberBean)
        }
        return defenseGroupMemberBeans
    }

    override fun findByDefenseGroupIdAndGraduationDesignTeacherId(defenseGroupId: String, graduationDesignTeacherId: String): Optional<Record> {
        return create.select()
                .from(DEFENSE_GROUP_MEMBER)
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId).and(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId)))
                .fetchOptional()
    }

    override fun deleteByDefenseGroupId(defenseGroupId: String) {
        create.deleteFrom(DEFENSE_GROUP_MEMBER)
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .execute()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun saveOrUpdate(defenseGroupMember: DefenseGroupMember) {
        create.insertInto(DEFENSE_GROUP_MEMBER,
                DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID,
                DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID,
                DEFENSE_GROUP_MEMBER.NOTE)
                .values(defenseGroupMember.graduationDesignTeacherId,
                        defenseGroupMember.defenseGroupId,
                        defenseGroupMember.note)
                .onDuplicateKeyUpdate()
                .set(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID, defenseGroupMember.defenseGroupId)
                .set(DEFENSE_GROUP_MEMBER.NOTE, defenseGroupMember.note)
                .execute()
    }

    /**
     * 搜索条件
     *
     * @param condition 条件
     * @return 条件
     */
    fun searchCondition(condition: GraduationDesignTeacherBean): Condition? {
        var a: Condition? = null
        val realName = StringUtils.trimWhitespace(condition.staffRealName)
        val defenseGroupId = StringUtils.trimWhitespace(condition.defenseGroupId!!)
        if (StringUtils.hasLength(realName)) {
            a = GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME.like(SQLQueryUtils.likeAllParam(realName))
        }

        if (StringUtils.hasLength(defenseGroupId)) {
            a = if (!ObjectUtils.isEmpty(a)) {
                a!!.and(DEFENSE_GROUP.DEFENSE_GROUP_ID.eq(defenseGroupId))
            } else {
                DEFENSE_GROUP.DEFENSE_GROUP_ID.eq(defenseGroupId)
            }
        }
        return a
    }

    /**
     * 其它条件参数
     *
     * @param a                           搜索条件
     * @param graduationDesignTeacherBean 额外参数
     */
    private fun otherCondition(a: Condition?, graduationDesignTeacherBean: GraduationDesignTeacherBean): Condition? {
        var tempCondition = a
        if (!ObjectUtils.isEmpty(graduationDesignTeacherBean)) {
            if (StringUtils.hasLength(graduationDesignTeacherBean.graduationDesignReleaseId)) {
                if (!ObjectUtils.isEmpty(tempCondition)) {
                    tempCondition = tempCondition!!.and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.graduationDesignReleaseId))
                } else {
                    tempCondition = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.graduationDesignReleaseId)
                }
            }
        }
        return tempCondition
    }
}