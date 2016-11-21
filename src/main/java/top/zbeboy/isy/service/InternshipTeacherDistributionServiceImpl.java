package top.zbeboy.isy.service;

import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import static top.zbeboy.isy.domain.Tables.COLLEGE;
import static top.zbeboy.isy.domain.Tables.SCHOOL;

/**
 * Created by zbeboy on 2016/11/21.
 */
@Service("internshipTeacherDistributionService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipTeacherDistributionServiceImpl extends DataTablesPlugin<InternshipTeacherDistributionBean> implements InternshipTeacherDistributionService {

    private final Logger log = LoggerFactory.getLogger(InternshipTeacherDistributionServiceImpl.class);

    private final DSLContext create;

    @Autowired
    public InternshipTeacherDistributionServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils) {
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record> selectJoinStep = create.select()
                    .from(COLLEGE)
                    .join(SCHOOL)
                    .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID));
            sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            records = selectJoinStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(COLLEGE)
                    .join(SCHOOL)
                    .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                    .where(a);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        return records;
    }

    @Override
    public int countAll() {
        return 0;
    }

    @Override
    public int countByCondition(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils) {
        return 0;
    }
}
