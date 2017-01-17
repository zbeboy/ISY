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
import top.zbeboy.isy.domain.tables.daos.SchoolDao;
import top.zbeboy.isy.domain.tables.pojos.School;
import top.zbeboy.isy.domain.tables.records.SchoolRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.SCHOOL;

/**
 * Created by lenovo on 2016-08-21.
 */
@Service("schoolService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SchoolServiceImpl extends DataTablesPlugin<School> implements SchoolService {

    private final Logger log = LoggerFactory.getLogger(SchoolServiceImpl.class);

    private final DSLContext create;

    @Resource
    private SchoolDao schoolDao;

    @Autowired
    public SchoolServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<SchoolRecord> findAll() {
        Byte isDel = 0;
        return create.selectFrom(SCHOOL)
                .where(SCHOOL.SCHOOL_IS_DEL.eq(isDel))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(School school) {
        schoolDao.insert(school);
    }

    @Override
    public void update(School school) {
        schoolDao.update(school);
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<School> dataTablesUtils) {
        return dataPagingQueryAll(dataTablesUtils, create, SCHOOL);
    }

    @Override
    public int countAll() {
        return statisticsAll(create, SCHOOL);
    }

    @Override
    public int countByCondition(DataTablesUtils<School> dataTablesUtils) {
        return statisticsWithCondition(dataTablesUtils, create, SCHOOL);
    }

    @Override
    public List<School> findBySchoolName(String schoolName) {
        return schoolDao.fetchBySchoolName(schoolName);
    }

    @Override
    public Result<SchoolRecord> findBySchoolNameNeSchoolId(String schoolName, int schoolId) {
        return create.selectFrom(SCHOOL)
                .where(SCHOOL.SCHOOL_NAME.eq(schoolName).and(SCHOOL.SCHOOL_ID.ne(schoolId)))
                .fetch();
    }

    @Override
    public void updateIsDel(List<Integer> ids, Byte isDel) {
        for (int id : ids) {
            create.update(SCHOOL).set(SCHOOL.SCHOOL_IS_DEL, isDel).where(SCHOOL.SCHOOL_ID.eq(id)).execute();
        }
    }

    @Override
    public School findById(int id) {
        return schoolDao.findById(id);
    }

    /**
     * 学校数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<School> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String schoolName = StringUtils.trimWhitespace(search.getString("schoolName"));
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName));
            }
        }
        return a;
    }

    /**
     * 学校数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<School> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("school_id".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = SCHOOL.SCHOOL_ID.asc();
                } else {
                    sortField = SCHOOL.SCHOOL_ID.desc();
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = SCHOOL.SCHOOL_NAME.asc();
                } else {
                    sortField = SCHOOL.SCHOOL_NAME.desc();
                }
            }

            if ("school_is_del".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = SCHOOL.SCHOOL_IS_DEL.asc();
                } else {
                    sortField = SCHOOL.SCHOOL_IS_DEL.desc();
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
