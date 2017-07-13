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
import top.zbeboy.isy.service.util.SQLQueryUtils;
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

            graduationDesignTeacherBeens.add(graduationDesignTeacherBean);
        }
        return graduationDesignTeacherBeens;
    }

    @Override
    public void deleteByDefenseGroupId(String defenseGroupId) {
        create.deleteFrom(DEFENSE_GROUP_MEMBER)
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .execute();
    }

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
