package top.zbeboy.isy.service.data;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.CollegeRoleDao;
import top.zbeboy.isy.domain.tables.pojos.CollegeRole;
import top.zbeboy.isy.domain.tables.records.CollegeRoleRecord;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.COLLEGE_ROLE;

/**
 * Created by lenovo on 2016-10-12.
 */
@Slf4j
@Service("collegeRoleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CollegeRoleServiceImpl implements CollegeRoleService {

    private final DSLContext create;

    @Resource
    private CollegeRoleDao collegeRoleDao;

    @Autowired
    public CollegeRoleServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<CollegeRoleRecord> findByCollegeId(int collegeId) {
        return create.selectFrom(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId))
                .fetch();
    }

    @Override
    public List<CollegeRoleRecord> findByCollegeIdAndAllowAgent(int collegeId, Byte allowAgent) {
        return create.selectFrom(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId).and(COLLEGE_ROLE.ALLOW_AGENT.eq(allowAgent)))
                .fetch();
    }

    @Override
    public Optional<Record> findByRoleId(String roleId) {
        return create.select()
                .from(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.ROLE_ID.eq(roleId))
                .fetchOptional();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(CollegeRole collegeRole) {
        collegeRoleDao.insert(collegeRole);
    }

    @Override
    public void update(CollegeRole collegeRole) {
        collegeRoleDao.update(collegeRole);
    }

    @Override
    public void deleteByRoleId(String roleId) {
        create.deleteFrom(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.ROLE_ID.eq(roleId))
                .execute();
    }
}
