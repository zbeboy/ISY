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
import top.zbeboy.isy.domain.tables.daos.AcademicTitleDao;
import top.zbeboy.isy.domain.tables.pojos.AcademicTitle;
import top.zbeboy.isy.domain.tables.records.AcademicTitleRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.ACADEMIC_TITLE;

/**
 * Created by zbeboy on 2017/4/21.
 */
@Slf4j
@Service("academicTitleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AcademicTitleServiceImpl extends DataTablesPlugin<AcademicTitle> implements AcademicTitleService {

    private final DSLContext create;

    @Resource
    private AcademicTitleDao academicTitleDao;

    @Autowired
    public AcademicTitleServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<AcademicTitle> findAll() {
        return academicTitleDao.findAll();
    }

    @Override
    public AcademicTitle findById(int id) {
        return academicTitleDao.findById(id);
    }

    @Override
    public List<AcademicTitle> findByAcademicTitleName(String academicTitleName) {
        return academicTitleDao.fetchByAcademicTitleName(academicTitleName);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(AcademicTitle academicTitle) {
        academicTitleDao.insert(academicTitle);
    }

    @Override
    public void update(AcademicTitle academicTitle) {
        academicTitleDao.update(academicTitle);
    }

    @Override
    public Result<AcademicTitleRecord> findByAcademicTitleNameNeAcademicTitleId(String academicTitleName, int academicTitleId) {
        return create.selectFrom(ACADEMIC_TITLE)
                .where(ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.eq(academicTitleName).and(ACADEMIC_TITLE.ACADEMIC_TITLE_ID.ne(academicTitleId)))
                .fetch();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<AcademicTitle> dataTablesUtils) {
        return dataPagingQueryAll(dataTablesUtils, create, ACADEMIC_TITLE);
    }

    @Override
    public int countAll() {
        return statisticsAll(create, ACADEMIC_TITLE);
    }

    @Override
    public int countByCondition(DataTablesUtils<AcademicTitle> dataTablesUtils) {
        return statisticsWithCondition(dataTablesUtils, create, ACADEMIC_TITLE);
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<AcademicTitle> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String academicTitleName = StringUtils.trimWhitespace(search.getString("academicTitleName"));
            if (StringUtils.hasLength(academicTitleName)) {
                a = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.like(SQLQueryUtils.likeAllParam(academicTitleName));
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
    public void sortCondition(DataTablesUtils<AcademicTitle> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("academic_title_id".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_ID.asc();
                } else {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_ID.desc();
                }
            }

            if ("academic_title_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.asc();
                } else {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.desc();
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
