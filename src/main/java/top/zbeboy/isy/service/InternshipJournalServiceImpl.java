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
import top.zbeboy.isy.domain.tables.daos.InternshipJournalDao;
import top.zbeboy.isy.domain.tables.daos.InternshipReleaseDao;
import top.zbeboy.isy.domain.tables.pojos.InternshipCollege;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.INTERNSHIP_JOURNAL;

/**
 * Created by zbeboy on 2016/12/14.
 */
@Service("internshipJournalService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipJournalServiceImpl extends DataTablesPlugin<InternshipJournal> implements InternshipJournalService {

    private final Logger log = LoggerFactory.getLogger(InternshipJournalServiceImpl.class);

    private final DSLContext create;

    private InternshipJournalDao internshipJournalDao;

    @Autowired
    public InternshipJournalServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.internshipJournalDao = new InternshipJournalDao(configuration);
    }

    @Override
    public InternshipJournal findById(String id) {
        return internshipJournalDao.findById(id);
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<InternshipJournal> dataTablesUtils, InternshipJournal internshipJournal) {
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, INTERNSHIP_JOURNAL, INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipJournal.getInternshipReleaseId()));
    }

    @Override
    public int countAll(InternshipJournal internshipJournal) {
        return statisticsAllWithCondition(create, INTERNSHIP_JOURNAL, INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipJournal.getInternshipReleaseId()));
    }

    @Override
    public int countByCondition(DataTablesUtils<InternshipJournal> dataTablesUtils, InternshipJournal internshipJournal) {
        return statisticsWithCondition(dataTablesUtils, create, INTERNSHIP_JOURNAL, INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID.eq(internshipJournal.getInternshipReleaseId()));
    }

    @Override
    public List<InternshipJournal> findInIds(String ids) {
        return internshipJournalDao.fetchByInternshipJournalId(ids);
    }

    @Override
    public void batchDelete(List<String> ids) {
        create.deleteFrom(INTERNSHIP_JOURNAL)
                .where(INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.in(ids))
                .execute();
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<InternshipJournal> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String studentId = StringUtils.trimWhitespace(search.getString("studentId"));
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String organize = StringUtils.trimWhitespace(search.getString("organize"));
            String guidanceTeacher = StringUtils.trimWhitespace(search.getString("guidanceTeacher"));

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
                            a = INTERNSHIP_JOURNAL.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                        } else {
                            a = a.and(INTERNSHIP_JOURNAL.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
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
        }
        return a;
    }

    /**
     * 数据排序
     *
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    @Override
    public void sortCondition(DataTablesUtils<InternshipJournal> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = INTERNSHIP_JOURNAL.STUDENT_NAME.asc();
                } else {
                    sortString = INTERNSHIP_JOURNAL.STUDENT_NAME.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = INTERNSHIP_JOURNAL.STUDENT_NUMBER.asc();
                } else {
                    sortString = INTERNSHIP_JOURNAL.STUDENT_NUMBER.desc();
                }
            }

            if ("organize".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = INTERNSHIP_JOURNAL.ORGANIZE.asc();
                } else {
                    sortString = INTERNSHIP_JOURNAL.ORGANIZE.desc();
                }
            }

            if ("school_guidance_teacher".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER.asc();
                } else {
                    sortString = INTERNSHIP_JOURNAL.SCHOOL_GUIDANCE_TEACHER.desc();
                }
            }

            if ("create_date".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortTimestamp = INTERNSHIP_JOURNAL.CREATE_DATE.asc();
                } else {
                    sortTimestamp = INTERNSHIP_JOURNAL.CREATE_DATE.desc();
                }
            }
        }

        sortToFinish(selectConditionStep, selectJoinStep, type);
    }
}
