package top.zbeboy.isy.service;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.RoleApplication;
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord;

import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.ROLE_APPLICATION;

/**
 * Created by lenovo on 2016/9/29.
 */
@Service("roleApplicationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RoleApplicationServiceImpl implements RoleApplicationService {

    private final Logger log = LoggerFactory.getLogger(RoleApplicationServiceImpl.class);

    private final DSLContext create;

    @Autowired
    public RoleApplicationServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Cacheable(cacheNames = "userRoleId", key = "#username")
    @Override
    public List<RoleApplication> findInRoleIdsWithUsername(List<Integer> roleIds, String username) {
        List<RoleApplication> roleApplications = new ArrayList<>();
        Result<RoleApplicationRecord> roleApplicationRecords = create.selectFrom(ROLE_APPLICATION)
                .where(ROLE_APPLICATION.ROLE_ID.in(roleIds))
                .fetch();
        if(roleApplicationRecords.isNotEmpty()){
            roleApplications = roleApplicationRecords.into(RoleApplication.class);
        }
        return roleApplications;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(RoleApplication roleApplication) {
        create.insertInto(ROLE_APPLICATION)
                .set(ROLE_APPLICATION.ROLE_ID, roleApplication.getRoleId())
                .set(ROLE_APPLICATION.APPLICATION_ID, roleApplication.getApplicationId())
                .execute();
    }

    @Override
    public void deleteByApplicationId(int applicationId) {
        create.deleteFrom(ROLE_APPLICATION)
                .where(ROLE_APPLICATION.APPLICATION_ID.eq(applicationId))
                .execute();
    }

    @Override
    public void deleteByRoleId(int roleId) {
        create.deleteFrom(ROLE_APPLICATION)
                .where(ROLE_APPLICATION.ROLE_ID.in(roleId))
                .execute();
    }

    @Override
    public Result<RoleApplicationRecord> findByRoleId(int roleId) {
        return create.selectFrom(ROLE_APPLICATION)
                .where(ROLE_APPLICATION.ROLE_ID.eq(roleId))
                .fetch();
    }
}
