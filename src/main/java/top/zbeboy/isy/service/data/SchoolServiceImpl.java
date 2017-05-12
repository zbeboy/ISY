package top.zbeboy.isy.service.data;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
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
import top.zbeboy.isy.elastic.pojo.OrganizeElastic;
import top.zbeboy.isy.elastic.repository.OrganizeElasticRepository;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.SCHOOL;

/**
 * Created by lenovo on 2016-08-21.
 */
@Slf4j
@Service("schoolService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SchoolServiceImpl extends DataTablesPlugin<School> implements SchoolService {

    private final DSLContext create;

    @Resource
    private SchoolDao schoolDao;

    @Resource
    private OrganizeService organizeService;

    @Resource
    private OrganizeElasticRepository organizeElasticRepository;

    @Autowired
    public SchoolServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<SchoolRecord> findByIsDel(Byte b) {
        return create.selectFrom(SCHOOL)
                .where(SCHOOL.SCHOOL_IS_DEL.eq(b))
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
        List<OrganizeElastic> records = organizeElasticRepository.findBySchoolId(school.getSchoolId());
        records.forEach(organizeElastic -> {
            organizeElastic.setSchoolId(school.getSchoolId());
            organizeElastic.setSchoolName(school.getSchoolName());
            organizeElasticRepository.delete(organizeElastic);
            organizeElasticRepository.save(organizeElastic);
        });
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
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("school_id".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_ID.asc();
                } else {
                    sortField[0] = SCHOOL.SCHOOL_ID.desc();
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc();
                    sortField[1] = SCHOOL.SCHOOL_ID.asc();
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc();
                    sortField[1] = SCHOOL.SCHOOL_ID.desc();
                }
            }

            if ("school_is_del".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_IS_DEL.asc();
                    sortField[1] = SCHOOL.SCHOOL_ID.asc();
                } else {
                    sortField[0] = SCHOOL.SCHOOL_IS_DEL.desc();
                    sortField[1] = SCHOOL.SCHOOL_ID.desc();
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
