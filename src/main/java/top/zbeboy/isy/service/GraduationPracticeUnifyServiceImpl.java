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
import top.zbeboy.isy.domain.tables.daos.GraduationPracticeUnifyDao;
import top.zbeboy.isy.domain.tables.daos.InternshipCollegeDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeUnify;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.internship.review.GraduationPracticeCollegeBean;
import top.zbeboy.isy.web.bean.internship.review.GraduationPracticeUnifyBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.sql.Date;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-11-27.
 */
@Service("graduationPracticeUnifyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationPracticeUnifyServiceImpl extends DataTablesPlugin<GraduationPracticeUnifyBean> implements GraduationPracticeUnifyService {

    private final Logger log = LoggerFactory.getLogger(GraduationPracticeUnifyServiceImpl.class);

    private final DSLContext create;

    private GraduationPracticeUnifyDao graduationPracticeUnifyDao;

    @Autowired
    public GraduationPracticeUnifyServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.graduationPracticeUnifyDao = new GraduationPracticeUnifyDao(configuration);
    }

    @Override
    public GraduationPracticeUnify findById(String id) {
        return graduationPracticeUnifyDao.findById(id);
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(GRADUATION_PRACTICE_UNIFY)
                .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }

    @Override
    public Result<Record> findByInternshipReleaseIdAndStudentIdRelation(String internshipReleaseId, int studentId) {
        return create.select()
                .from(GRADUATION_PRACTICE_UNIFY)
                .join(STUDENT)
                .on(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(studentId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationPracticeUnify graduationPracticeUnify) {
        graduationPracticeUnifyDao.insert(graduationPracticeUnify);
    }

    @Override
    public void update(GraduationPracticeUnify graduationPracticeUnify) {
        graduationPracticeUnifyDao.update(graduationPracticeUnify);
    }

    @Override
    public void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        create.deleteFrom(GRADUATION_PRACTICE_UNIFY)
                .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(studentId)))
                .execute();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<GraduationPracticeUnifyBean> dataTablesUtils, GraduationPracticeUnifyBean graduationPracticeUnifyBean) {
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_PRACTICE_UNIFY)
                    .join(STUDENT)
                    .on(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnifyBean.getInternshipReleaseId()));
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_PRACTICE_UNIFY)
                    .join(STUDENT)
                    .on(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnifyBean.getInternshipReleaseId())).and(a);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        return records;
    }

    @Override
    public int countAll(GraduationPracticeUnifyBean graduationPracticeUnifyBean) {
        Record1<Integer> count = create.selectCount()
                .from(GRADUATION_PRACTICE_UNIFY)
                .join(STUDENT)
                .on(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnifyBean.getInternshipReleaseId()))
                .fetchOne();
        return count.value1();
    }

    @Override
    public int countByCondition(DataTablesUtils<GraduationPracticeUnifyBean> dataTablesUtils, GraduationPracticeUnifyBean graduationPracticeUnifyBean) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_PRACTICE_UNIFY)
                    .join(STUDENT)
                    .on(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnifyBean.getInternshipReleaseId()));
            count = selectConditionStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_PRACTICE_UNIFY)
                    .join(STUDENT)
                    .on(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnifyBean.getInternshipReleaseId())).and(a);
            count = selectConditionStep.fetchOne();
        }
        if (!ObjectUtils.isEmpty(count)) {
            return count.value1();
        }
        return 0;
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<GraduationPracticeUnifyBean> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
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
    public void sortCondition(DataTablesUtils<GraduationPracticeUnifyBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = USERS.REAL_NAME.asc();
                } else {
                    sortString = USERS.REAL_NAME.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = STUDENT.STUDENT_NUMBER.asc();
                } else {
                    sortString = STUDENT.STUDENT_NUMBER.desc();
                }
            }

            sortToFinish(selectConditionStep,selectJoinStep,type,STUDENT.STUDENT_NUMBER);
        }
    }
}
