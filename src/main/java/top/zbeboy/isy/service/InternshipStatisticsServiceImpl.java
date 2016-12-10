package top.zbeboy.isy.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.InternshipApplyDao;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.department.DepartmentBean;
import top.zbeboy.isy.web.bean.internship.statistics.InternshipStatisticsBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-12-10.
 */
@Service("internshipStatisticsService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipStatisticsServiceImpl implements InternshipStatisticsService {

    private final Logger log = LoggerFactory.getLogger(InternshipStatisticsServiceImpl.class);

    private final DSLContext create;

    private InternshipApplyDao internshipApplyDao;

    @Autowired
    public InternshipStatisticsServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.internshipApplyDao = new InternshipApplyDao(configuration);
    }

    @Override
    public Result<Record> submittedFindAllByPage(DataTablesUtils<InternshipStatisticsBean> dataTablesUtils, InternshipStatisticsBean internshipStatisticsBean) {
        Result<Record> records;
        Condition a = submittedSearchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(INTERNSHIP_APPLY)
                    .join(STUDENT)
                    .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipStatisticsBean.getInternshipReleaseId()));
            submittedSortCondition(dataTablesUtils, selectConditionStep);
            submittedPagination(dataTablesUtils, selectConditionStep);
            records = selectConditionStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(INTERNSHIP_APPLY)
                    .join(STUDENT)
                    .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipStatisticsBean.getInternshipReleaseId())).and(a);
            submittedSortCondition(dataTablesUtils, selectConditionStep);
            submittedPagination(dataTablesUtils, selectConditionStep);
            records = selectConditionStep.fetch();
        }
        return records;
    }

    @Override
    public int submittedCountAll(InternshipStatisticsBean internshipStatisticsBean) {
        Record1<Integer> count = create.selectCount()
                .from(INTERNSHIP_APPLY)
                .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipStatisticsBean.getInternshipReleaseId()))
                .fetchOne();
        return count.value1();
    }

    @Override
    public int submittedCountByCondition(DataTablesUtils<InternshipStatisticsBean> dataTablesUtils, InternshipStatisticsBean internshipStatisticsBean) {
        Record1<Integer> count;
        Condition a = submittedSearchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_APPLY)
                    .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipStatisticsBean.getInternshipReleaseId()));
            count = selectConditionStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_APPLY)
                    .join(STUDENT)
                    .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipStatisticsBean.getInternshipReleaseId())).and(a);
            count = selectConditionStep.fetchOne();
        }
        if (!ObjectUtils.isEmpty(count)) {
            return count.value1();
        }
        return 0;
    }

    /**
     * 已提交列表全局搜索条件
     *
     * @param dataTablesUtils
     * @return 搜索条件
     */
    public Condition submittedSearchCondition(DataTablesUtils<InternshipStatisticsBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String scienceName = StringUtils.trimWhitespace(search.getString("scienceName"));
            String organizeName = StringUtils.trimWhitespace(search.getString("organizeName"));
            String internshipApplyState = StringUtils.trimWhitespace(search.getString("internshipApplyState"));
            if (StringUtils.hasLength(studentName)) {
                a = USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName));
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                }
            }

            if (StringUtils.hasLength(scienceName)) {
                int scienceId = NumberUtils.toInt(scienceName);
                if(scienceId>0){
                    if (ObjectUtils.isEmpty(a)) {
                        a = SCIENCE.SCIENCE_ID.eq(scienceId);
                    } else {
                        a = a.and(SCIENCE.SCIENCE_ID.eq(scienceId));
                    }
                }
            }

            if (StringUtils.hasLength(organizeName)) {
                int organizeId = NumberUtils.toInt(organizeName);
                if(organizeId>0){
                    if (ObjectUtils.isEmpty(a)) {
                        a = ORGANIZE.ORGANIZE_ID.eq(organizeId);
                    } else {
                        a = a.and(ORGANIZE.ORGANIZE_ID.eq(organizeId));
                    }
                }
            }

            if (StringUtils.hasLength(internshipApplyState)) {
                int internshipApplyStateNum = NumberUtils.toInt(internshipApplyState);
                if(internshipApplyStateNum>-1){
                    if (ObjectUtils.isEmpty(a)) {
                        a = INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(internshipApplyStateNum);
                    } else {
                        a = a.and(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(internshipApplyStateNum));
                    }
                }
            }
        }
        return a;
    }

    /**
     * 已提交列表数据排序
     *
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    public void submittedSortCondition(DataTablesUtils<InternshipStatisticsBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField<Integer> a = null;
        SortField<String> b = null;
        SortField<Byte> c = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    b = USERS.REAL_NAME.asc();
                } else {
                    b = USERS.REAL_NAME.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    b = STUDENT.STUDENT_NUMBER.asc();
                } else {
                    b = STUDENT.STUDENT_NUMBER.desc();
                }
            }

            if ("science_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    b = SCIENCE.SCIENCE_NAME.asc();
                } else {
                    b = SCIENCE.SCIENCE_NAME.desc();
                }
            }

            if ("organize_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    b = ORGANIZE.ORGANIZE_NAME.asc();
                } else {
                    b = ORGANIZE.ORGANIZE_NAME.desc();
                }
            }

            if ("internship_apply_state".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    a = INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.asc();
                } else {
                    a = INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.desc();
                }
            }

        }

        if (!ObjectUtils.isEmpty(a)) {
            selectConditionStep.orderBy(a);
        } else if (!ObjectUtils.isEmpty(b)) {
            selectConditionStep.orderBy(b);
        } else if (!ObjectUtils.isEmpty(c)) {
            selectConditionStep.orderBy(c);
        } else {
            selectConditionStep.orderBy(INTERNSHIP_APPLY.APPLY_TIME.desc());
        }
    }

    /**
     * 已提交列表分页方式
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件1
     */
    public void submittedPagination(DataTablesUtils<InternshipStatisticsBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        int start = dataTablesUtils.getStart();
        int length = dataTablesUtils.getLength();
        selectConditionStep.limit(start, length);
    }
}
