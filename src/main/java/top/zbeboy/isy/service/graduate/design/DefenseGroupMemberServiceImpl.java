package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember;
import top.zbeboy.isy.domain.tables.records.DefenseGroupMemberRecord;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupMemberBean;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;

import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zbeboy on 2017/7/12.
 */
@Slf4j
@Service("defenseGroupMemberService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DefenseGroupMemberServiceImpl implements DefenseGroupMemberService {

    private final DSLContext create;

    @Autowired
    public DefenseGroupMemberServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<GraduationDesignTeacherBean> findByGraduationDesignReleaseIdRelationForStaff(GraduationDesignTeacherBean condition) {
        List<GraduationDesignTeacherBean> graduationDesignTeacherBeens = new ArrayList<>();
        Result<Record> records;
        Condition a = searchCondition(condition);
        a = otherCondition(a, condition);
        if (ObjectUtils.isEmpty(a)) {
            records = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .leftJoin(DEFENSE_GROUP_MEMBER)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID))
                    .leftJoin(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID))
                    .orderBy(STAFF.STAFF_NUMBER)
                    .fetch();
        } else {
            records = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(STAFF)
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .leftJoin(DEFENSE_GROUP_MEMBER)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID))
                    .leftJoin(DEFENSE_GROUP)
                    .on(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID))
                    .where(a)
                    .orderBy(STAFF.STAFF_NUMBER)
                    .fetch();
        }

        for (Record r : records) {
            GraduationDesignTeacherBean graduationDesignTeacherBean = new GraduationDesignTeacherBean();
            graduationDesignTeacherBean.setGraduationDesignTeacherId(r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID));
            graduationDesignTeacherBean.setRealName(r.getValue(USERS.REAL_NAME));
            graduationDesignTeacherBean.setStaffUsername(r.getValue(USERS.USERNAME));
            graduationDesignTeacherBean.setStaffNumber(r.getValue(STAFF.STAFF_NUMBER));
            graduationDesignTeacherBean.setStaffMobile(r.getValue(USERS.MOBILE));
            graduationDesignTeacherBean.setDefenseGroupId(r.getValue(DEFENSE_GROUP.DEFENSE_GROUP_ID));
            graduationDesignTeacherBean.setDefenseGroupName(r.getValue(DEFENSE_GROUP.DEFENSE_GROUP_NAME));
            graduationDesignTeacherBean.setLeaderId(r.getValue(DEFENSE_GROUP.LEADER_ID));
            graduationDesignTeacherBean.setNote(r.getValue(DEFENSE_GROUP_MEMBER.NOTE));

            graduationDesignTeacherBeens.add(graduationDesignTeacherBean);
        }
        return graduationDesignTeacherBeens;
    }

    @Override
    public DefenseGroupMemberRecord findByGraduationDesignTeacherId(String graduationDesignTeacherId) {
        return create.selectFrom(DEFENSE_GROUP_MEMBER)
                .where(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId))
                .fetchOne();
    }

    @Override
    public List<DefenseGroupMemberBean> findByDefenseGroupIdAndGraduationDesignReleaseIdForStudent(String defenseGroupId, String graduationDesignReleaseId) {
        List<DefenseGroupMemberBean> defenseGroupMemberBeans = new ArrayList<>();
        Result<Record> records = create.select()
                .from(DEFENSE_GROUP_MEMBER)
                .join(GRADUATION_DESIGN_TUTOR)
                .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                .join(STUDENT.join(USERS.as("S")).on(STUDENT.USERNAME.eq(USERS.as("S").USERNAME)))
                .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .leftJoin(GRADUATION_DESIGN_PRESUBJECT)
                .on(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID.eq(STUDENT.STUDENT_ID).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .fetch();
        for (Record r : records) {
            DefenseGroupMemberBean defenseGroupMemberBean = new DefenseGroupMemberBean();
            defenseGroupMemberBean.setDefenseGroupId(r.getValue(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID));
            defenseGroupMemberBean.setGraduationDesignTeacherId(r.getValue(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID));
            defenseGroupMemberBean.setNote(r.getValue(DEFENSE_GROUP_MEMBER.NOTE));
            defenseGroupMemberBean.setStudentNumber(r.getValue(STUDENT.STUDENT_NUMBER));
            defenseGroupMemberBean.setStudentName(r.getValue(USERS.as("S").REAL_NAME));
            defenseGroupMemberBean.setSubject(r.getValue(GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE));
            defenseGroupMemberBean.setStaffName(r.getValue(USERS.as("T").REAL_NAME));
            defenseGroupMemberBean.setStudentId(r.getValue(STUDENT.STUDENT_ID));

            defenseGroupMemberBeans.add(defenseGroupMemberBean);
        }
        return defenseGroupMemberBeans;
    }

    @Override
    public List<DefenseGroupMemberBean> findByDefenseGroupIdForStaff(String defenseGroupId) {
        List<DefenseGroupMemberBean> defenseGroupMemberBeans = new ArrayList<>();
        Result<Record> records = create.select()
                .from(DEFENSE_GROUP_MEMBER)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .fetch();
        for (Record r : records) {
            DefenseGroupMemberBean defenseGroupMemberBean = new DefenseGroupMemberBean();
            defenseGroupMemberBean.setDefenseGroupId(r.getValue(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID));
            defenseGroupMemberBean.setGraduationDesignTeacherId(r.getValue(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID));
            defenseGroupMemberBean.setNote(r.getValue(DEFENSE_GROUP_MEMBER.NOTE));
            defenseGroupMemberBean.setStaffName(r.getValue(USERS.REAL_NAME));

            defenseGroupMemberBeans.add(defenseGroupMemberBean);
        }
        return defenseGroupMemberBeans;
    }

    @Override
    public void deleteByDefenseGroupId(String defenseGroupId) {
        create.deleteFrom(DEFENSE_GROUP_MEMBER)
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .execute();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(DefenseGroupMember defenseGroupMember) {
        create.insertInto(DEFENSE_GROUP_MEMBER,
                DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID,
                DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID,
                DEFENSE_GROUP_MEMBER.NOTE)
                .values(defenseGroupMember.getGraduationDesignTeacherId(),
                        defenseGroupMember.getDefenseGroupId(),
                        defenseGroupMember.getNote())
                .onDuplicateKeyUpdate()
                .set(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID, defenseGroupMember.getDefenseGroupId())
                .set(DEFENSE_GROUP_MEMBER.NOTE, defenseGroupMember.getNote())
                .execute();
    }

    /**
     * 搜索条件
     *
     * @param condition 条件
     * @return 条件
     */
    public Condition searchCondition(GraduationDesignTeacherBean condition) {
        Condition a = null;
        String realName = StringUtils.trimWhitespace(condition.getRealName());
        String defenseGroupId = StringUtils.trimWhitespace(condition.getDefenseGroupId());
        if (StringUtils.hasLength(realName)) {
            a = USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(realName));
        }

        if (StringUtils.hasLength(defenseGroupId)) {
            if (!ObjectUtils.isEmpty(a)) {
                a = a.and(DEFENSE_GROUP.DEFENSE_GROUP_ID.eq(defenseGroupId));
            } else {
                a = DEFENSE_GROUP.DEFENSE_GROUP_ID.eq(defenseGroupId);
            }
        }
        return a;
    }

    /**
     * 其它条件参数
     *
     * @param a                           搜索条件
     * @param graduationDesignTeacherBean 额外参数
     */
    private Condition otherCondition(Condition a, GraduationDesignTeacherBean graduationDesignTeacherBean) {
        if (!ObjectUtils.isEmpty(graduationDesignTeacherBean)) {
            if (StringUtils.hasLength(graduationDesignTeacherBean.getGraduationDesignReleaseId())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.getGraduationDesignReleaseId()));
                } else {
                    a = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.getGraduationDesignReleaseId());
                }
            }
        }
        return a;
    }
}
