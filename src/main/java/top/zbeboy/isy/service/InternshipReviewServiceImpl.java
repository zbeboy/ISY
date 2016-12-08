package top.zbeboy.isy.service;

import com.alibaba.fastjson.JSON;
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
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean;
import top.zbeboy.isy.web.bean.internship.review.InternshipReviewBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zbeboy on 2016/12/7.
 */
@Service("internshipReviewService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipReviewServiceImpl implements InternshipReviewService {

    private final Logger log = LoggerFactory.getLogger(InternshipReviewServiceImpl.class);

    private final DSLContext create;

    private InternshipApplyDao internshipApplyDao;

    @Autowired
    public InternshipReviewServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.internshipApplyDao = new InternshipApplyDao(configuration);
    }

    @Override
    public List<InternshipReviewBean> findAllByPage(PaginationUtils paginationUtils, InternshipApplyBean internshipApplyBean) {
        List<InternshipReviewBean> internshipReviewBeens = new ArrayList<>();
        int pageNum = paginationUtils.getPageNum();
        int pageSize = paginationUtils.getPageSize();
        Condition a = searchCondition(paginationUtils);
        a = otherCondition(a, internshipApplyBean);
        Result<Record> records = create.select()
                .from(INTERNSHIP_APPLY)
                .join(INTERNSHIP_RELEASE)
                .on(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID))
                .join(STUDENT.join(USERS.as("T")).on(STUDENT.USERNAME.eq(USERS.as("T").USERNAME)))
                .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                .join(USERS)
                .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                .where(a)
                .orderBy(INTERNSHIP_APPLY.APPLY_TIME.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();
        records.forEach(r->{
            InternshipReviewBean internshipReviewBean = new InternshipReviewBean();
            internshipReviewBean.setStudentId(r.getValue(INTERNSHIP_APPLY.STUDENT_ID));
            internshipReviewBean.setInternshipReleaseId(r.getValue(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID));
            internshipReviewBean.setInternshipTypeId(r.getValue(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID));
            internshipReviewBean.setRealName(r.getValue(USERS.REAL_NAME));
            internshipReviewBean.setStudentName(r.getValue(USERS.as("T").REAL_NAME));
            internshipReviewBean.setStudentNumber(r.getValue(STUDENT.STUDENT_NUMBER));
            internshipReviewBean.setScienceName(r.getValue(SCIENCE.SCIENCE_NAME));
            internshipReviewBean.setOrganizeName(r.getValue(ORGANIZE.ORGANIZE_NAME));
            internshipReviewBean.setReason(r.getValue(INTERNSHIP_APPLY.REASON));
            internshipReviewBean.setInternshipApplyState(r.getValue(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE));
            internshipReviewBeens.add(internshipReviewBean);
        });
        return internshipReviewBeens;
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
                    .join(STUDENT.join(USERS.as("T")).on(STUDENT.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(INTERNSHIP_APPLY.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
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
            String internshipReleaseId = StringUtils.trimWhitespace(search.getString("internshipReleaseId"));
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String scienceName = StringUtils.trimWhitespace(search.getString("scienceName"));
            String organizeName = StringUtils.trimWhitespace(search.getString("organizeName"));
            if (StringUtils.hasLength(internshipReleaseId)) {
                a = INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId);
            }

            if(StringUtils.hasLength(studentName)){
                if(!ObjectUtils.isEmpty(a)){
                    a = a.and(USERS.as("T").REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName)));
                } else {
                    a = USERS.as("T").REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName));
                }
            }

            if(StringUtils.hasLength(studentNumber)){
                if(!ObjectUtils.isEmpty(a)){
                    a = a.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                } else {
                    a = STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                }
            }

            if(StringUtils.hasLength(scienceName)){
                int scienceId = NumberUtils.toInt(scienceName);
                if(scienceId >  0){
                    if(!ObjectUtils.isEmpty(a)){
                        a = a.and(SCIENCE.SCIENCE_ID.eq(scienceId));
                    } else {
                        a = SCIENCE.SCIENCE_ID.eq(scienceId);
                    }
                }
            }

            if(StringUtils.hasLength(organizeName)){
                int organizeId = NumberUtils.toInt(organizeName);
                if(organizeId >  0){
                    if(!ObjectUtils.isEmpty(a)){
                        a = a.and(ORGANIZE.ORGANIZE_ID.eq(organizeId));
                    } else {
                        a = ORGANIZE.ORGANIZE_ID.eq(organizeId);
                    }
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
