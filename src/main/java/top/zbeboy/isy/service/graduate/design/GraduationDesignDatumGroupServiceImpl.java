package top.zbeboy.isy.service.graduate.design;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.GraduationDesignDatumGroupDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumGroupBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;

import static top.zbeboy.isy.domain.Tables.*;

@Slf4j
@Service("graduationDesignDatumGroupService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignDatumGroupServiceImpl extends DataTablesPlugin<GraduationDesignDatumGroupBean> implements GraduationDesignDatumGroupService {

    private final DSLContext create;

    @Resource
    private GraduationDesignDatumGroupDao graduationDesignDatumGroupDao;

    @Autowired
    public GraduationDesignDatumGroupServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public GraduationDesignDatumGroup findById(String id) {
        return graduationDesignDatumGroupDao.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignDatumGroup graduationDesignDatumGroup) {
        graduationDesignDatumGroupDao.insert(graduationDesignDatumGroup);
    }

    @Override
    public void delete(GraduationDesignDatumGroup graduationDesignDatumGroup) {
        graduationDesignDatumGroupDao.delete(graduationDesignDatumGroup);
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<GraduationDesignDatumGroupBean> dataTablesUtils, GraduationDesignDatumGroupBean graduationDesignDatumGroupBean) {
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDatumGroupBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record> selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.FILE_ID.eq(FILES.FILE_ID));
            sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            records = selectJoinStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .where(a);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        return records;
    }

    @Override
    public int countAll(GraduationDesignDatumGroupBean graduationDesignDatumGroupBean) {
        Record1<Integer> count;
        Condition a = otherCondition(null, graduationDesignDatumGroupBean);
        if (ObjectUtils.isEmpty(a)) {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
                    .fetchOne();
        } else {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .where(a)
                    .fetchOne();
        }
        return count.value1();
    }

    @Override
    public int countByCondition(DataTablesUtils<GraduationDesignDatumGroupBean> dataTablesUtils, GraduationDesignDatumGroupBean graduationDesignDatumGroupBean) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDatumGroupBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM_GROUP);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM_GROUP)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    /**
     * 其它条件
     *
     * @param graduationDesignDatumGroupBean 条件
     * @return 条件
     */
    public Condition otherCondition(Condition a, GraduationDesignDatumGroupBean graduationDesignDatumGroupBean) {
        if (!ObjectUtils.isEmpty(graduationDesignDatumGroupBean)) {

            if (StringUtils.hasLength(graduationDesignDatumGroupBean.getGraduationDesignReleaseId())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDatumGroupBean.getGraduationDesignReleaseId()));
                } else {
                    a = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDatumGroupBean.getGraduationDesignReleaseId());
                }
            }

            if (StringUtils.hasLength(graduationDesignDatumGroupBean.getGraduationDesignTeacherId())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignDatumGroupBean.getGraduationDesignTeacherId()));
                } else {
                    a = GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignDatumGroupBean.getGraduationDesignTeacherId());
                }
            }
        }
        return a;
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<GraduationDesignDatumGroupBean> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String originalFileName = StringUtils.trimWhitespace(search.getString("originalFileName"));
            if (StringUtils.hasLength(originalFileName)) {
                a = FILES.ORIGINAL_FILE_NAME.like(SQLQueryUtils.likeAllParam(originalFileName));
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
    public void sortCondition(DataTablesUtils<GraduationDesignDatumGroupBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {

            if ("original_file_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = FILES.ORIGINAL_FILE_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_DATUM_GROUP_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.desc();
                    sortField[1] = GRADUATION_DESIGN_DATUM_GROUP.GRADUATION_DESIGN_DATUM_GROUP_ID.desc();
                }
            }

            if ("upload_time".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DATUM_GROUP.UPLOAD_TIME.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DATUM_GROUP.UPLOAD_TIME.desc();
                }
            }
        }
        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
