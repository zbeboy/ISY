package top.zbeboy.isy.service;

import com.alibaba.fastjson.JSON;
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
import top.zbeboy.isy.domain.tables.daos.InternshipApplyDao;
import top.zbeboy.isy.domain.tables.daos.InternshipCollegeDao;
import top.zbeboy.isy.domain.tables.pojos.InternshipApply;
import top.zbeboy.isy.domain.tables.pojos.Science;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.bean.internship.review.InternshipReviewBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-11-29.
 */
@Service("internshipApplyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipApplyServiceImpl implements InternshipApplyService {

    private final Logger log = LoggerFactory.getLogger(InternshipApplyServiceImpl.class);

    private final DSLContext create;

    private InternshipApplyDao internshipApplyDao;

    @Resource
    private InternshipReleaseScienceService internshipReleaseScienceService;

    @Autowired
    public InternshipApplyServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.internshipApplyDao = new InternshipApplyDao(configuration);
    }

    @Override
    public InternshipApply findById(String id) {
        return internshipApplyDao.findById(id);
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return  create.select()
                .from(INTERNSHIP_APPLY)
                .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_APPLY.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(InternshipApply internshipApply) {
        internshipApplyDao.insert(internshipApply);
    }

    @Override
    public void update(InternshipApply internshipApply) {
        internshipApplyDao.update(internshipApply);
    }

    @Override
    public void updateStateWithInternshipReleaseIdAndState(String internshipReleaseId, int changeState, int updateState) {
        create.update(INTERNSHIP_APPLY)
                .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE,updateState)
                .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(changeState)))
                .execute();
    }

    @Override
    public void updateStateByChangeFillEndTime(Timestamp changeFillEndTime, int changeState, int updateState) {
        create.update(INTERNSHIP_APPLY)
                .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE,updateState)
                .where(INTERNSHIP_APPLY.CHANGE_FILL_END_TIME.le(changeFillEndTime).and(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(changeState)))
                .execute();
    }

    @Override
    public void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        create.deleteFrom(INTERNSHIP_APPLY)
                .where(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(INTERNSHIP_APPLY.STUDENT_ID.eq(studentId)))
                .execute();
    }

    @Override
    public Result<Record> findAllByPage(PaginationUtils paginationUtils, InternshipApplyBean internshipApplyBean) {
        int pageNum = paginationUtils.getPageNum();
        int pageSize = paginationUtils.getPageSize();
        Condition a = searchCondition(paginationUtils);
        a = otherCondition(a, internshipApplyBean);
        return create.select()
                .from(INTERNSHIP_APPLY)
                .join(INTERNSHIP_RELEASE)
                .on(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                .join(STUDENT)
                .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                .join(USERS)
                .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                .join(DEPARTMENT)
                .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                .where(a)
                .orderBy(INTERNSHIP_APPLY.APPLY_TIME.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();
    }

    @Override
    public List<InternshipApplyBean> dealData(PaginationUtils paginationUtils, Result<Record> records, InternshipApplyBean internshipApplyBean) {
        List<InternshipApplyBean> internshipApplyBeens = new ArrayList<>();
        if (records.isNotEmpty()) {
            internshipApplyBeens = records.into(InternshipApplyBean.class);
            String format = "yyyy-MM-dd HH:mm:ss";
            internshipApplyBeens.forEach(i -> {
                i.setTeacherDistributionStartTimeStr(DateTimeUtils.timestampToString(i.getTeacherDistributionStartTime(), format));
                i.setTeacherDistributionEndTimeStr(DateTimeUtils.timestampToString(i.getTeacherDistributionEndTime(), format));
                i.setStartTimeStr(DateTimeUtils.timestampToString(i.getStartTime(), format));
                i.setEndTimeStr(DateTimeUtils.timestampToString(i.getEndTime(), format));
                i.setReleaseTimeStr(DateTimeUtils.timestampToString(i.getReleaseTime(), format));
                Result<Record> records1 = internshipReleaseScienceService.findByInternshipReleaseIdRelation(i.getInternshipReleaseId());
                i.setSciences(records1.into(Science.class));
            });
            paginationUtils.setTotalDatas(countByCondition(paginationUtils, internshipApplyBean));
        }
        return internshipApplyBeens;
    }

    @Override
    public int countByCondition(PaginationUtils paginationUtils, InternshipApplyBean internshipApplyBean) {
        Record1<Integer> count;
        Condition a = searchCondition(paginationUtils);
        a = otherCondition(a, internshipApplyBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(INTERNSHIP_APPLY);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_APPLY)
                    .join(INTERNSHIP_RELEASE)
                    .on(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                    .join(STUDENT)
                    .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(INTERNSHIP_TYPE)
                    .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                    .join(USERS)
                    .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                    .join(DEPARTMENT)
                    .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .join(SCHOOL)
                    .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    /**
     * 搜索条件
     *
     * @param paginationUtils 分页工具
     * @return 条件
     */
    public Condition searchCondition(PaginationUtils paginationUtils) {
        Condition a = null;
        JSONObject search = JSON.parseObject(paginationUtils.getSearchParams());
        if (!ObjectUtils.isEmpty(search)) {
            String internshipTitle = StringUtils.trimWhitespace(search.getString("internshipTitle"));
            String internshipReleaseId = StringUtils.trimWhitespace(search.getString("internshipReleaseId"));
            if (StringUtils.hasLength(internshipTitle)) {
                a = INTERNSHIP_RELEASE.INTERNSHIP_TITLE.like(SQLQueryUtils.likeAllParam(internshipTitle));
            }

            if (StringUtils.hasLength(internshipReleaseId)) {
                if(!ObjectUtils.isEmpty(a)){
                    a = a.and(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId));
                } else {
                    a = INTERNSHIP_RELEASE.INTERNSHIP_TITLE.like(SQLQueryUtils.likeAllParam(internshipTitle));
                }
            }
        }
        return a;
    }

    /**
     * 其它条件参数
     *
     * @param a                 搜索条件
     * @param internshipApplyBean 额外参数
     * @return 条件
     */
    private Condition otherCondition(Condition a, InternshipApplyBean internshipApplyBean) {
        if (!ObjectUtils.isEmpty(internshipApplyBean)) {
            if (!ObjectUtils.isEmpty(internshipApplyBean.getStudentId()) && internshipApplyBean.getStudentId()>0) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(INTERNSHIP_APPLY.STUDENT_ID.eq(internshipApplyBean.getStudentId()));
                } else {
                    a = INTERNSHIP_APPLY.STUDENT_ID.eq(internshipApplyBean.getStudentId());
                }
            }

            if (StringUtils.hasLength(internshipApplyBean.getInternshipReleaseId())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipApplyBean.getInternshipReleaseId()));
                } else {
                    a = INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(internshipApplyBean.getInternshipReleaseId());
                }
            }

            if (!ObjectUtils.isEmpty(internshipApplyBean.getInternshipReleaseIsDel())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_IS_DEL.eq(internshipApplyBean.getInternshipReleaseIsDel()));
                } else {
                    a = INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_IS_DEL.eq(internshipApplyBean.getInternshipReleaseIsDel());
                }
            }

            if (!ObjectUtils.isEmpty(internshipApplyBean.getInternshipApplyState())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(internshipApplyBean.getInternshipApplyState()));
                } else {
                    a = INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE.eq(internshipApplyBean.getInternshipApplyState());
                }
            }
        }
        return a;
    }
}
