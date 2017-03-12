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
import top.zbeboy.isy.domain.tables.daos.InternshipJournalDao;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;
import top.zbeboy.isy.domain.tables.records.InternshipJournalRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.internship.journal.InternshipJournalBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.INTERNSHIP_JOURNAL;

/**
 * Created by zbeboy on 2016/12/14.
 */
@Service("internshipJournalService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipJournalServiceImpl extends DataTablesPlugin<InternshipJournalBean> implements InternshipJournalService {

    private final Logger log = LoggerFactory.getLogger(InternshipJournalServiceImpl.class);

    private final DSLContext create;

    @Resource
    private InternshipJournalDao internshipJournalDao;

    @Autowired
    public InternshipJournalServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public InternshipJournal findById(String id) {
        return internshipJournalDao.findById(id);
    }

    @Override
    public Result<InternshipJournalRecord> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.selectFrom(INTERNSHIP_JOURNAL)
                .where(INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_JOURNAL.STUDENT_ID.eq(studentId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(InternshipJournal internshipJournal) {
        internshipJournalDao.insert(internshipJournal);
    }

    @Override
    public void update(InternshipJournal internshipJournal) {
        internshipJournalDao.update(internshipJournal);
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<InternshipJournalBean> dataTablesUtils, InternshipJournalBean internshipJournalBean) {
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, INTERNSHIP_JOURNAL, INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipJournalBean.getInternshipReleaseId()));
    }

    @Override
    public int countAll(InternshipJournalBean internshipJournalBean) {
        return statisticsAllWithCondition(create, INTERNSHIP_JOURNAL, INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipJournalBean.getInternshipReleaseId()));
    }

    @Override
    public int countByCondition(DataTablesUtils<InternshipJournalBean> dataTablesUtils, InternshipJournalBean internshipJournalBean) {
        return statisticsWithCondition(dataTablesUtils, create, INTERNSHIP_JOURNAL, INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipJournalBean.getInternshipReleaseId()));
    }

    @Override
    public List<InternshipJournal> findInIds(String ids) {
        return internshipJournalDao.fetchByInternshipJournalId(ids);
    }

    @Override
    public void batchDelete(List<String> ids) {
        internshipJournalDao.deleteById(ids);
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<InternshipJournalBean> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String studentId = StringUtils.trimWhitespace(search.getString("studentId"));
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String organize = StringUtils.trimWhitespace(search.getString("organize"));
            String guidanceTeacher = StringUtils.trimWhitespace(search.getString("guidanceTeacher"));
            String createDate = StringUtils.trimWhitespace(search.getString("createDate"));

            if (StringUtils.hasLength(studentName)) {
                a = INTERNSHIP_JOURNAL.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName));
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = INTERNSHIP_JOURNAL.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(INTERNSHIP_JOURNAL.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                }
            }

            if (StringUtils.hasLength(studentId)) {
                if (NumberUtils.isDigits(studentId)) {
                    int tempStudentId = NumberUtils.toInt(studentId);
                    if (tempStudentId > 0) {
                        if (ObjectUtils.isEmpty(a)) {
                            a = INTERNSHIP_JOURNAL.STUDENT_ID.eq(tempStudentId);
                        } else {
                            a = a.and(INTERNSHIP_JOURNAL.STUDENT_ID.eq(tempStudentId));
                        }
                    }
                }
            }

            if (StringUtils.hasLength(organize)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = INTERNSHIP_JOURNAL.ORGANIZE.like(SQLQueryUtils.likeAllParam(organize));
                } else {
                    a = a.and(INTERNSHIP_JOURNAL.ORGANIZE.like(SQLQueryUtils.likeAllParam(organize)));
                }
            }

            if (StringUtils.hasLength(guidanceTeacher)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(guidanceTeacher));
                } else {
                    a = a.and(INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(guidanceTeacher)));
                }
            }

            if (StringUtils.hasLength(createDate)) {
                String format = "yyyy-MM-dd HH:mm:ss";
                String[] createDateArr = createDate.split("至");
                if (!ObjectUtils.isEmpty(createDateArr) && createDateArr.length >= 2) {
                    try {
                        if (ObjectUtils.isEmpty(a)) {
                            a = INTERNSHIP_JOURNAL.CREATE_DATE.ge(DateTimeUtils.formatDateToTimestamp(createDateArr[0], format)).and(INTERNSHIP_JOURNAL.CREATE_DATE.le(DateTimeUtils.formatDateToTimestamp(createDateArr[1], format)));
                        } else {
                            a = a.and(INTERNSHIP_JOURNAL.CREATE_DATE.ge(DateTimeUtils.formatDateToTimestamp(createDateArr[0], format))).and(INTERNSHIP_JOURNAL.CREATE_DATE.le(DateTimeUtils.formatDateToTimestamp(createDateArr[1], format)));
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
    public void sortCondition(DataTablesUtils<InternshipJournalBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = INTERNSHIP_JOURNAL.STUDENT_NAME.asc();
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.asc();
                } else {
                    sortField[0] = INTERNSHIP_JOURNAL.STUDENT_NAME.desc();
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = INTERNSHIP_JOURNAL.STUDENT_NUMBER.asc();
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.asc();
                } else {
                    sortField[0] = INTERNSHIP_JOURNAL.STUDENT_NUMBER.desc();
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.desc();
                }
            }

            if ("organize".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = INTERNSHIP_JOURNAL.ORGANIZE.asc();
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.asc();
                } else {
                    sortField[0] = INTERNSHIP_JOURNAL.ORGANIZE.desc();
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.desc();
                }
            }

            if ("school_guidance_teacher".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER.asc();
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.asc();
                } else {
                    sortField[0] = INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER.desc();
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.desc();
                }
            }

            if ("create_date".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = INTERNSHIP_JOURNAL.CREATE_DATE.asc();
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.asc();
                } else {
                    sortField[0] = INTERNSHIP_JOURNAL.CREATE_DATE.desc();
                    sortField[1] = INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.desc();
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
