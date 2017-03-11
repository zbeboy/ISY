package top.zbeboy.isy.service.data;

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

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.COLLEGE;
import static top.zbeboy.isy.domain.Tables.SCHOOL;

/**
 * Created by lenovo on 2016-08-21.
 */
@Service("collegeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CollegeServiceImpl extends DataTablesPlugin<CollegeBean> implements CollegeService {

    private final Logger log = LoggerFactory.getLogger(CollegeServiceImpl.class);

    private final DSLContext create;

    @Resource
    private CollegeDao collegeDao;

    @Autowired
    public CollegeServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
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
        return statisticsAll(create, COLLEGE);
    }

    @Override
    public int countByCondition(DataTablesUtils<CollegeBean> dataTablesUtils) {
        Record1<Integer> count;
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
    public Optional<Record> findByIdRelation(int collegeId) {
        return create.select()
                .from(COLLEGE)
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(COLLEGE.COLLEGE_ID.eq(collegeId))
                .fetchOptional();
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
     * @param dataTablesUtils datatable工具类
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
     * @param dataTablesUtils     datatable工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<CollegeBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("college_id".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_ID.asc();
                } else {
                    sortField[0] = COLLEGE.COLLEGE_ID.desc();
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc();
                    sortField[1] = COLLEGE.COLLEGE_ID.asc();
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc();
                    sortField[1] = COLLEGE.COLLEGE_ID.desc();
                }
            }

            if ("college_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc();
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc();
                }
            }

            if ("college_is_del".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_IS_DEL.asc();
                    sortField[1] = COLLEGE.COLLEGE_ID.asc();
                } else {
                    sortField[0] = COLLEGE.COLLEGE_IS_DEL.desc();
                    sortField[1] = COLLEGE.COLLEGE_ID.desc();
                }
            }
        }
        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
