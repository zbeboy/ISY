package top.zbeboy.isy.service.internship;

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
import top.zbeboy.isy.domain.tables.daos.InternshipRegulateDao;
import top.zbeboy.isy.domain.tables.pojos.InternshipRegulate;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.internship.regulate.InternshipRegulateBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.INTERNSHIP_REGULATE;

/**
 * Created by zbeboy on 2016/12/23.
 */
@Service("internshipRegulateService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipRegulateServiceImpl extends DataTablesPlugin<InternshipRegulateBean> implements InternshipRegulateService {

    private final Logger log = LoggerFactory.getLogger(InternshipRegulateServiceImpl.class);

    private final DSLContext create;

    @Resource
    private InternshipRegulateDao internshipRegulateDao;

    @Autowired
    public InternshipRegulateServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public InternshipRegulate findById(String id) {
        return internshipRegulateDao.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(InternshipRegulate internshipRegulate) {
        internshipRegulateDao.insert(internshipRegulate);
    }

    @Override
    public void update(InternshipRegulate internshipRegulate) {
        internshipRegulateDao.update(internshipRegulate);
    }

    @Override
    public void batchDelete(List<String> ids) {
        internshipRegulateDao.deleteById(ids);
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<InternshipRegulateBean> dataTablesUtils, InternshipRegulateBean internshipRegulateBean) {
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, INTERNSHIP_REGULATE, extraCondition(internshipRegulateBean));
    }

    @Override
    public int countAll(InternshipRegulateBean internshipRegulateBean) {
        return statisticsAllWithCondition(create, INTERNSHIP_REGULATE, extraCondition(internshipRegulateBean));
    }

    @Override
    public int countByCondition(DataTablesUtils<InternshipRegulateBean> dataTablesUtils, InternshipRegulateBean internshipRegulateBean) {
        return statisticsWithCondition(dataTablesUtils, create, INTERNSHIP_REGULATE, extraCondition(internshipRegulateBean));
    }

    @Override
    public Result<Record> exportData(DataTablesUtils<InternshipRegulateBean> dataTablesUtils, InternshipRegulateBean internshipRegulateBean) {
        return dataPagingQueryAllWithConditionNoPage(dataTablesUtils, create, INTERNSHIP_REGULATE, INTERNSHIP_REGULATE.INTERNSHIP_RELEASE_ID.eq(internshipRegulateBean.getInternshipReleaseId()));
    }

    /**
     * 额外参数条件
     *
     * @param internshipRegulateBean 条件
     * @return 条件语句
     */
    private Condition extraCondition(InternshipRegulateBean internshipRegulateBean) {
        Condition extraCondition = INTERNSHIP_REGULATE.INTERNSHIP_RELEASE_ID.eq(internshipRegulateBean.getInternshipReleaseId());
        if (!ObjectUtils.isEmpty(internshipRegulateBean.getStaffId())) {
            extraCondition = extraCondition.and(INTERNSHIP_REGULATE.STAFF_ID.eq(internshipRegulateBean.getStaffId()));
        }
        return extraCondition;
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<InternshipRegulateBean> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String staffId = StringUtils.trimWhitespace(search.getString("staffId"));
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String studentTel = StringUtils.trimWhitespace(search.getString("studentTel"));
            String schoolGuidanceTeacher = StringUtils.trimWhitespace(search.getString("schoolGuidanceTeacher"));
            String createDate = StringUtils.trimWhitespace(search.getString("createDate"));

            if (StringUtils.hasLength(studentName)) {
                a = INTERNSHIP_REGULATE.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName));
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = INTERNSHIP_REGULATE.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(INTERNSHIP_REGULATE.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                }
            }

            if (StringUtils.hasLength(staffId)) {
                if (NumberUtils.isDigits(staffId)) {
                    int tempStaffId = NumberUtils.toInt(staffId);
                    if (tempStaffId > 0) {
                        if (ObjectUtils.isEmpty(a)) {
                            a = INTERNSHIP_REGULATE.STAFF_ID.eq(tempStaffId);
                        } else {
                            a = a.and(INTERNSHIP_REGULATE.STAFF_ID.eq(tempStaffId));
                        }
                    }
                }
            }

            if (StringUtils.hasLength(studentTel)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = INTERNSHIP_REGULATE.STUDENT_TEL.like(SQLQueryUtils.likeAllParam(studentTel));
                } else {
                    a = a.and(INTERNSHIP_REGULATE.STUDENT_TEL.like(SQLQueryUtils.likeAllParam(studentTel)));
                }
            }

            if (StringUtils.hasLength(schoolGuidanceTeacher)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher));
                } else {
                    a = a.and(INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher)));
                }
            }

            if (StringUtils.hasLength(createDate)) {
                try {
                    String format = "yyyy-MM-dd HH:mm:ss";
                    String[] createDateArr = DateTimeUtils.splitDateTime("至", createDate);
                    if (ObjectUtils.isEmpty(a)) {
                        a = INTERNSHIP_REGULATE.CREATE_DATE.ge(DateTimeUtils.formatDateToTimestamp(createDateArr[0], format)).and(INTERNSHIP_REGULATE.CREATE_DATE.le(DateTimeUtils.formatDateToTimestamp(createDateArr[1], format)));
                    } else {
                        a = a.and(INTERNSHIP_REGULATE.CREATE_DATE.ge(DateTimeUtils.formatDateToTimestamp(createDateArr[0], format))).and(INTERNSHIP_REGULATE.CREATE_DATE.le(DateTimeUtils.formatDateToTimestamp(createDateArr[1], format)));
                    }
                } catch (ParseException e) {
                    log.error("Format time error, error is {}", e);
                }

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
    public void sortCondition(DataTablesUtils<InternshipRegulateBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_NAME.asc();
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.asc();
                } else {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_NAME.desc();
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_NUMBER.asc();
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.asc();
                } else {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_NUMBER.desc();
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.desc();
                }
            }

            if ("student_tel".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_TEL.asc();
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.asc();
                } else {
                    sortField[0] = INTERNSHIP_REGULATE.STUDENT_TEL.desc();
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.desc();
                }
            }

            if ("school_guidance_teacher".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER.asc();
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.asc();
                } else {
                    sortField[0] = INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER.desc();
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.desc();
                }
            }

            if ("create_date".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = INTERNSHIP_REGULATE.CREATE_DATE.asc();
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.asc();
                } else {
                    sortField[0] = INTERNSHIP_REGULATE.CREATE_DATE.desc();
                    sortField[1] = INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID.desc();
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
