package top.zbeboy.isy.service.data;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.CollegeRole;
import top.zbeboy.isy.domain.tables.records.CollegeRoleRecord;

import java.util.List;

import static top.zbeboy.isy.domain.Tables.COLLEGE_ROLE;

/**
 * Created by lenovo on 2016-10-12.
 */
@Service("collegeRoleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CollegeRoleServiceImpl implements CollegeRoleService {

    private final Logger log = LoggerFactory.getLogger(CollegeRoleServiceImpl.class);

    private final DSLContext create;

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

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(CollegeRole collegeRole) {
        create.insertInto(COLLEGE_ROLE)
                .set(COLLEGE_ROLE.ROLE_ID, collegeRole.getRoleId())
                .set(COLLEGE_ROLE.COLLEGE_ID, collegeRole.getCollegeId())
                .execute();
    }

    @Override
    public void deleteByRoleId(int roleId) {
        create.deleteFrom(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.ROLE_ID.eq(roleId))
                .execute();
    }
}
