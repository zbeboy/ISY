package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.ApplicationDao;

import javax.annotation.Resource;
import static top.zbeboy.isy.domain.Tables.COLLEGE_APPLICATION;

/**
 * Created by lenovo on 2016-10-05.
 */
@Service("collegeApplicationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CollegeApplicationServiceImpl implements CollegeApplicationService {

    private final Logger log = LoggerFactory.getLogger(CollegeApplicationServiceImpl.class);

    private final DSLContext create;

    @Autowired
    public CollegeApplicationServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public void deleteByApplicationId(int applicationId) {
        create.deleteFrom(COLLEGE_APPLICATION)
                .where(COLLEGE_APPLICATION.APPLICATION_ID.eq(applicationId))
                .execute();
    }
}
