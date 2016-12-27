package top.zbeboy.isy.service;

import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.SystemLogDao;
import top.zbeboy.isy.domain.tables.daos.SystemMessageDao;

import javax.annotation.Resource;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-12-24.
 */
@Service("systemMessageService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemMessageServiceImpl implements SystemMessageService {

    private final Logger log = LoggerFactory.getLogger(SystemMessageServiceImpl.class);

    private final DSLContext create;

    @Resource
    private SystemMessageDao systemMessageDao;

    @Autowired
    public SystemMessageServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<Record> findAllByPageForShow(int pageNum, int pageSize, String username, boolean isSee) {
        Byte b = 0;
        if(isSee){
            b = 1;
        }
        return create.select()
                .from(SYSTEM_MESSAGE)
                .join(USERS)
                .on(SYSTEM_MESSAGE.SEND_USERS.eq(USERS.USERNAME))
                .where(SYSTEM_MESSAGE.ACCEPT_USERS.eq(username).and(SYSTEM_MESSAGE.IS_SEE.eq(b)))
                .orderBy(SYSTEM_MESSAGE.MESSAGE_DATE.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();
    }

    @Override
    public int countAllForShow(String username, boolean isSee) {
        Byte b = 0;
        if(isSee){
            b = 1;
        }
        Record1<Integer> record = create.selectCount()
                .from(SYSTEM_MESSAGE)
                .where(SYSTEM_MESSAGE.ACCEPT_USERS.eq(username).and(SYSTEM_MESSAGE.IS_SEE.eq(b)))
                .fetchOne();
        return record.value1();
    }
}
