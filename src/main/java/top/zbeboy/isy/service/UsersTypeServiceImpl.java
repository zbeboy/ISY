package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.UsersTypeDao;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.pojos.UsersType;
import top.zbeboy.isy.domain.tables.records.UsersTypeRecord;

import javax.annotation.Resource;

import static top.zbeboy.isy.domain.tables.UsersType.USERS_TYPE;

/**
 * Created by lenovo on 2016-08-24.
 */
@Service("usersTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UsersTypeServiceImpl implements UsersTypeService {

    private final Logger log = LoggerFactory.getLogger(UsersTypeServiceImpl.class);

    private final DSLContext create;

    @Resource
    private UsersTypeDao usersTypeDao;

    @Resource
    private UsersService usersService;

    @Autowired
    public UsersTypeServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }


    @Override
    public UsersType findByUsersTypeName(String usersTypeName) {
        return usersTypeDao.fetchOne(USERS_TYPE.USERS_TYPE_NAME, usersTypeName);
    }

    @Override
    public UsersType findByUsersTypeId(int usersTypeId) {
        return usersTypeDao.findById(usersTypeId);
    }

    @Override
    public Result<UsersTypeRecord> findAll() {
        return create.selectFrom(USERS_TYPE).fetch();
    }

    @Override
    public boolean isCurrentUsersTypeName(String usersTypeName) {
        Users users = usersService.getUserFromSession();
        String usersType = usersTypeDao.fetchOneByUsersTypeId(users.getUsersTypeId()).getUsersTypeName();
        return usersTypeName.equals(usersType);
    }
}
