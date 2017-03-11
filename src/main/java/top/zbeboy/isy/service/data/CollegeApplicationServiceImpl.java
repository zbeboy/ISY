package top.zbeboy.isy.service.data;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.CollegeApplication;
import top.zbeboy.isy.domain.tables.records.CollegeApplicationRecord;

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

    @Override
    public Result<CollegeApplicationRecord> findByCollegeId(int collegeId) {
        return create.selectFrom(COLLEGE_APPLICATION)
                .where(COLLEGE_APPLICATION.COLLEGE_ID.eq(collegeId))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(CollegeApplication collegeApplication) {
        create.insertInto(COLLEGE_APPLICATION)
                .set(COLLEGE_APPLICATION.COLLEGE_ID, collegeApplication.getCollegeId())
                .set(COLLEGE_APPLICATION.APPLICATION_ID, collegeApplication.getApplicationId())
                .execute();
    }

    @Override
    public void deleteByCollegeId(int collegeId) {
        create.deleteFrom(COLLEGE_APPLICATION)
                .where(COLLEGE_APPLICATION.COLLEGE_ID.eq(collegeId))
                .execute();
    }
}
