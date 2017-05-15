package top.zbeboy.isy.service.data;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.PoliticalLandscapeDao;
import top.zbeboy.isy.domain.tables.pojos.PoliticalLandscape;
import top.zbeboy.isy.domain.tables.records.PoliticalLandscapeRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.POLITICAL_LANDSCAPE;

/**
 * Created by lenovo on 2016-10-30.
 */
@Slf4j
@Service("politicalLandscapeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class PoliticalLandscapeServiceImpl extends DataTablesPlugin<PoliticalLandscape> implements PoliticalLandscapeService {

    private final DSLContext create;

    @Resource
    private PoliticalLandscapeDao politicalLandscapeDao;

    @Autowired
    public PoliticalLandscapeServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<PoliticalLandscape> findAll() {
        return politicalLandscapeDao.findAll();
    }

    @Override
    public PoliticalLandscape findById(int id) {
        return politicalLandscapeDao.findById(id);
    }

    @Override
    public List<PoliticalLandscape> findByPoliticalLandscapeName(String politicalLandscapeName) {
        return politicalLandscapeDao.fetchByPoliticalLandscapeName(politicalLandscapeName);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(PoliticalLandscape politicalLandscape) {
        politicalLandscapeDao.insert(politicalLandscape);
    }

    @Override
    public void update(PoliticalLandscape politicalLandscape) {
        politicalLandscapeDao.update(politicalLandscape);
    }

    @Override
    public Result<PoliticalLandscapeRecord> findByNationNameNePoliticalLandscapeId(String politicalLandscapeName, int politicalLandscapeId) {
        return create.selectFrom(POLITICAL_LANDSCAPE)
                .where(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.eq(politicalLandscapeName).and(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID.ne(politicalLandscapeId)))
                .fetch();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<PoliticalLandscape> dataTablesUtils) {
        return dataPagingQueryAll(dataTablesUtils, create, POLITICAL_LANDSCAPE);
    }

    @Override
    public int countAll() {
        return statisticsAll(create, POLITICAL_LANDSCAPE);
    }

    @Override
    public int countByCondition(DataTablesUtils<PoliticalLandscape> dataTablesUtils) {
        return statisticsWithCondition(dataTablesUtils, create, POLITICAL_LANDSCAPE);
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<PoliticalLandscape> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String politicalLandscapeName = StringUtils.trimWhitespace(search.getString("politicalLandscapeName"));
            if (StringUtils.hasLength(politicalLandscapeName)) {
                a = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.like(SQLQueryUtils.likeAllParam(politicalLandscapeName));
            }
        }
        return a;
    }

    /**
     * 数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<PoliticalLandscape> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("political_landscape_id".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID.asc();
                } else {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID.desc();
                }
            }

            if ("political_landscape_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.asc();
                    sortField[1] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID.asc();
                } else {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.desc();
                    sortField[1] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID.desc();
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
