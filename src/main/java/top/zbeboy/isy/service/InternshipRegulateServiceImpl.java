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
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, INTERNSHIP_REGULATE, INTERNSHIP_REGULATE.INTERNSHIP_RELEASE_ID.eq(internshipRegulateBean.getInternshipReleaseId()));
    }

    @Override
    public int countAll(InternshipRegulateBean internshipRegulateBean) {
        return statisticsAllWithCondition(create, INTERNSHIP_REGULATE, INTERNSHIP_REGULATE.INTERNSHIP_RELEASE_ID.eq(internshipRegulateBean.getInternshipReleaseId()));
    }

    @Override
    public int countByCondition(DataTablesUtils<InternshipRegulateBean> dataTablesUtils, InternshipRegulateBean internshipRegulateBean) {
        return statisticsWithCondition(dataTablesUtils, create, INTERNSHIP_REGULATE, INTERNSHIP_REGULATE.INTERNSHIP_RELEASE_ID.eq(internshipRegulateBean.getInternshipReleaseId()));
    }

    @Override
    public Result<Record> exportData(DataTablesUtils<InternshipRegulateBean> dataTablesUtils, InternshipRegulateBean internshipRegulateBean) {
        return dataPagingQueryAllWithConditionNoPage(dataTablesUtils, create, INTERNSHIP_REGULATE, INTERNSHIP_REGULATE.INTERNSHIP_RELEASE_ID.eq(internshipRegulateBean.getInternshipReleaseId()));
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
                String format = "yyyy-MM-dd HH:mm:ss";
                String[] createDateArr = createDate.split("至");
                if (!ObjectUtils.isEmpty(createDateArr) && createDateArr.length >= 2) {
                    try {
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
        SortField sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = INTERNSHIP_REGULATE.STUDENT_NAME.asc();
                } else {
                    sortField = INTERNSHIP_REGULATE.STUDENT_NAME.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = INTERNSHIP_REGULATE.STUDENT_NUMBER.asc();
                } else {
                    sortField = INTERNSHIP_REGULATE.STUDENT_NUMBER.desc();
                }
            }

            if ("student_tel".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = INTERNSHIP_REGULATE.STUDENT_TEL.asc();
                } else {
                    sortField = INTERNSHIP_REGULATE.STUDENT_TEL.desc();
                }
            }

            if ("school_guidance_teacher".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER.asc();
                } else {
                    sortField = INTERNSHIP_REGULATE.SCHOOL_GUIDANCE_TEACHER.desc();
                }
            }

            if ("create_date".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = INTERNSHIP_REGULATE.CREATE_DATE.asc();
                } else {
                    sortField = INTERNSHIP_REGULATE.CREATE_DATE.desc();
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
