package top.zbeboy.isy.service;

import com.alibaba.fastjson.JSONObject;
import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.CollegeDao;
import top.zbeboy.isy.domain.tables.pojos.College;
import top.zbeboy.isy.domain.tables.records.CollegeRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.college.CollegeBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

import static top.zbeboy.isy.domain.tables.College.COLLEGE;
import static top.zbeboy.isy.domain.tables.School.SCHOOL;

/**
 * Created by lenovo on 2016-08-21.
 */
@Service("collegeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CollegeServiceImpl extends DataTablesPlugin<CollegeBean> implements CollegeService {

    private final Logger log = LoggerFactory.getLogger(CollegeServiceImpl.class);

    private final DSLContext create;

    private CollegeDao collegeDao;

    @Autowired
    public CollegeServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.collegeDao = new CollegeDao(configuration);
    }

    @Override
    public Result<CollegeRecord> findBySchoolId(int schoolId) {
        Byte isDel = 0;
        return create.selectFrom(COLLEGE)
                .where(COLLEGE.COLLEGE_IS_DEL.eq(isDel).and(COLLEGE.SCHOOL_ID.eq(schoolId)))
                .fetch();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<CollegeBean> dataTablesUtils) {
        Result<Record> records = null;
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
        return statisticsAll(create, COLLEGE);
    }

    @Override
    public int countByCondition(DataTablesUtils<CollegeBean> dataTablesUtils) {
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(COLLEGE);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(COLLEGE)
                    .join(SCHOOL)
                    .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    @Override
    public Result<CollegeRecord> findByCollegeNameAndSchoolId(String collegeName, int schoolId) {
        return create.selectFrom(COLLEGE)
                .where(COLLEGE.COLLEGE_NAME.eq(collegeName).and(COLLEGE.SCHOOL_ID.eq(schoolId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(College college) {
        collegeDao.insert(college);
    }

    @Override
    public void update(College college) {
        collegeDao.update(college);
    }

    @Override
    public void updateIsDel(List<Integer> ids, Byte isDel) {
        for (int id : ids) {
            create.update(COLLEGE).set(COLLEGE.COLLEGE_IS_DEL, isDel).where(COLLEGE.COLLEGE_ID.eq(id)).execute();
        }
    }

    @Override
    public College findById(int id) {
        return collegeDao.findById(id);
    }

    @Override
    public Result<CollegeRecord> findByCollegeNameAndSchoolIdNeCollegeId(String collegeName, int collegeId, int schoolId) {
        return create.selectFrom(COLLEGE)
                .where(COLLEGE.COLLEGE_NAME.eq(collegeName).and(COLLEGE.COLLEGE_ID.ne(collegeId)).and(COLLEGE.SCHOOL_ID.eq(schoolId)))
                .fetch();
    }

    /**
     * 院数据全局搜索条件
     *
     * @param dataTablesUtils
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<CollegeBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String schoolName = StringUtils.trimWhitespace(search.getString("schoolName"));
            String collegeName = StringUtils.trimWhitespace(search.getString("collegeName"));
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName));
            }

            if (StringUtils.hasLength(collegeName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName));
                } else {
                    a = a.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName)));
                }
            }

        }
        return a;
    }

    /**
     * 院数据排序
     *
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    @Override
    public void sortCondition(DataTablesUtils<CollegeBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = orderDir.equalsIgnoreCase("asc");
        SortField<Integer> a = null;
        SortField<String> b = null;
        SortField<Byte> c = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if (orderColumnName.equalsIgnoreCase("college_id")) {
                if (isAsc) {
                    a = COLLEGE.COLLEGE_ID.asc();
                } else {
                    a = COLLEGE.COLLEGE_ID.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("school_name")) {
                if (isAsc) {
                    b = SCHOOL.SCHOOL_NAME.asc();
                } else {
                    b = SCHOOL.SCHOOL_NAME.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("college_name")) {
                if (isAsc) {
                    b = COLLEGE.COLLEGE_NAME.asc();
                } else {
                    b = COLLEGE.COLLEGE_NAME.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("college_is_del")) {
                if (isAsc) {
                    c = COLLEGE.COLLEGE_IS_DEL.asc();
                } else {
                    c = COLLEGE.COLLEGE_IS_DEL.desc();
                }
            }

        }

        if (!ObjectUtils.isEmpty(a)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(a);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(a);
            }

        } else if (!ObjectUtils.isEmpty(b)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(b);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(b);
            }
        } else if (!ObjectUtils.isEmpty(c)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(c);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(c);
            }
        } else {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(COLLEGE.COLLEGE_ID.desc());
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(COLLEGE.COLLEGE_ID.desc());
            }
        }
    }
}
