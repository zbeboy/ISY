package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.DefenseGroupDao;
import top.zbeboy.isy.domain.tables.pojos.DefenseGroup;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zbeboy on 2017/7/11.
 */
@Slf4j
@Service("defenseGroupService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DefenseGroupServiceImpl implements DefenseGroupService {

    private final DSLContext create;

    @Resource
    private DefenseGroupDao defenseGroupDao;

    @Autowired
    public DefenseGroupServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public DefenseGroup findById(String id) {
        return defenseGroupDao.findById(id);
    }

    @Override
    public Optional<Record> findByIdRelation(String id) {
        return create.select()
                .from(DEFENSE_GROUP)
                .join(SCHOOLROOM)
                .on(DEFENSE_GROUP.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                .where(DEFENSE_GROUP.DEFENSE_GROUP_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public List<DefenseGroupBean> findByDefenseArrangementId(String defenseArrangementId) {
        List<DefenseGroupBean> defenseGroupBeens = new ArrayList<>();
        Result<Record> records = create.select()
                .from(DEFENSE_GROUP)
                .join(SCHOOLROOM)
                .on(DEFENSE_GROUP.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                .leftJoin(GRADUATION_DESIGN_TEACHER)
                .on(DEFENSE_GROUP.LEADER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .leftJoin(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .leftJoin(USERS.as("S"))
                .on(USERS.as("S").USERNAME.eq(DEFENSE_GROUP.SECRETARY_ID))
                .where(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(defenseArrangementId))
                .orderBy(DEFENSE_GROUP.CREATE_TIME.desc())
                .fetch();
        for (Record r : records) {
            DefenseGroupBean defenseGroupBean = new DefenseGroupBean();
            defenseGroupBean.setDefenseGroupId(r.getValue(DEFENSE_GROUP.DEFENSE_GROUP_ID));
            defenseGroupBean.setDefenseGroupName(r.getValue(DEFENSE_GROUP.DEFENSE_GROUP_NAME));
            defenseGroupBean.setSchoolroomId(r.getValue(SCHOOLROOM.SCHOOLROOM_ID));
            defenseGroupBean.setNote(r.getValue(DEFENSE_GROUP.NOTE));
            defenseGroupBean.setCreateTime(r.getValue(DEFENSE_GROUP.CREATE_TIME));
            defenseGroupBean.setLeaderId(r.getValue(DEFENSE_GROUP.LEADER_ID));
            defenseGroupBean.setSecretaryId(r.getValue(DEFENSE_GROUP.SECRETARY_ID));
            defenseGroupBean.setDefenseArrangementId(r.getValue(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID));
            defenseGroupBean.setBuildingName(r.getValue(BUILDING.BUILDING_NAME));
            defenseGroupBean.setBuildingCode(r.getValue(SCHOOLROOM.BUILDING_CODE));
            defenseGroupBean.setStaffName(r.getValue(USERS.as("T").REAL_NAME));
            defenseGroupBean.setStudentName(r.getValue(USERS.as("S").REAL_NAME));

            defenseGroupBeens.add(defenseGroupBean);
        }
        return defenseGroupBeens;
    }

    @Override
    public Result<Record> findByGraduationDesignReleaseId(String graduationDesignReleaseId) {
        return create.select()
                .from(DEFENSE_ARRANGEMENT)
                .join(DEFENSE_GROUP)
                .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                .where(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .orderBy(DEFENSE_GROUP.CREATE_TIME.asc())
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(DefenseGroup defenseGroup) {
        defenseGroupDao.insert(defenseGroup);
    }

    @Override
    public void update(DefenseGroup defenseGroup) {
        defenseGroupDao.update(defenseGroup);
    }

    @Override
    public void deleteById(String id) {
        defenseGroupDao.deleteById(id);
    }
}
