package top.zbeboy.isy.service.internship;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.InternshipReleaseDao;
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease;
import top.zbeboy.isy.domain.tables.pojos.Science;
import top.zbeboy.isy.domain.tables.records.InternshipReleaseRecord;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-11-12.
 */
@Slf4j
@Service("internshipReleaseService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipReleaseServiceImpl implements InternshipReleaseService {

    private final DSLContext create;

    @Resource
    private InternshipReleaseDao internshipReleaseDao;

    @Resource
    private InternshipReleaseScienceService internshipReleaseScienceService;

    @Autowired
    public InternshipReleaseServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<InternshipReleaseRecord> findByEndTime(Timestamp endTime) {
        return create.selectFrom(INTERNSHIP_RELEASE)
                .where(INTERNSHIP_RELEASE.END_TIME.le(endTime))
                .fetch();
    }

    @Override
    public InternshipRelease findById(String internshipReleaseId) {
        return internshipReleaseDao.findById(internshipReleaseId);
    }

    @Override
    public Optional<Record> findByIdRelation(String internshipReleaseId) {
        return create.select()
                .from(INTERNSHIP_RELEASE)
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID))
                .join(DEPARTMENT)
                .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId))
                .fetchOptional();
    }

    @Override
    public List<InternshipRelease> findByReleaseTitle(String releaseTitle) {
        return internshipReleaseDao.fetchByInternshipTitle(releaseTitle);
    }

    @Override
    public Result<InternshipReleaseRecord> findByReleaseTitleNeInternshipReleaseId(String releaseTitle, String internshipReleaseId) {
        return create.selectFrom(INTERNSHIP_RELEASE)
                .where(INTERNSHIP_RELEASE.INTERNSHIP_TITLE.eq(releaseTitle).and(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID.ne(internshipReleaseId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(InternshipRelease internshipRelease) {
        internshipReleaseDao.insert(internshipRelease);
    }

    @Override
    public void update(InternshipRelease internshipRelease) {
        internshipReleaseDao.update(internshipRelease);
    }

    @Override
    public Result<Record> findAllByPage(PaginationUtils paginationUtils, InternshipReleaseBean internshipReleaseBean) {
        int pageNum = paginationUtils.getPageNum();
        int pageSize = paginationUtils.getPageSize();
        Condition a = searchCondition(paginationUtils);
        a = otherCondition(a, internshipReleaseBean);
        return create.select()
                .from(INTERNSHIP_RELEASE)
                .join(USERS)
                .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                .join(DEPARTMENT)
                .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(INTERNSHIP_TYPE)
                .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.COLLEGE_ID.eq(SCHOOL.SCHOOL_ID))
                .where(a)
                .orderBy(INTERNSHIP_RELEASE.RELEASE_TIME.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();

    }

    @Override
    public List<InternshipReleaseBean> dealData(PaginationUtils paginationUtils, Result<Record> records, InternshipReleaseBean internshipReleaseBean) {
        List<InternshipReleaseBean> internshipReleaseBeens = new ArrayList<>();
        if (records.isNotEmpty()) {
            internshipReleaseBeens = records.into(InternshipReleaseBean.class);
            String format = "yyyy-MM-dd HH:mm:ss";
            internshipReleaseBeens.forEach(i -> {
                i.setTeacherDistributionStartTimeStr(DateTimeUtils.timestampToString(i.getTeacherDistributionStartTime(), format));
                i.setTeacherDistributionEndTimeStr(DateTimeUtils.timestampToString(i.getTeacherDistributionEndTime(), format));
                i.setStartTimeStr(DateTimeUtils.timestampToString(i.getStartTime(), format));
                i.setEndTimeStr(DateTimeUtils.timestampToString(i.getEndTime(), format));
                i.setReleaseTimeStr(DateTimeUtils.timestampToString(i.getReleaseTime(), format));
                Result<Record> records1 = internshipReleaseScienceService.findByInternshipReleaseIdRelation(i.getInternshipReleaseId());
                i.setSciences(records1.into(Science.class));
            });
            paginationUtils.setTotalDatas(countByCondition(paginationUtils, internshipReleaseBean));
        }
        return internshipReleaseBeens;
    }

    @Override
    public int countByCondition(PaginationUtils paginationUtils, InternshipReleaseBean internshipReleaseBean) {
        Record1<Integer> count;
        Condition a = searchCondition(paginationUtils);
        a = otherCondition(a, internshipReleaseBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(INTERNSHIP_RELEASE);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(INTERNSHIP_RELEASE)
                    .join(USERS)
                    .on(INTERNSHIP_RELEASE.USERNAME.eq(USERS.USERNAME))
                    .join(DEPARTMENT)
                    .on(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(INTERNSHIP_TYPE)
                    .on(INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID.eq(INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID))
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
            if (StringUtils.hasLength(internshipTitle)) {
                a = INTERNSHIP_RELEASE.INTERNSHIP_TITLE.like(SQLQueryUtils.likeAllParam(internshipTitle));
            }
        }
        return a;
    }

    /**
     * 其它条件参数
     *
     * @param a                     搜索条件
     * @param internshipReleaseBean 额外参数
     * @return 条件
     */
    private Condition otherCondition(Condition a, InternshipReleaseBean internshipReleaseBean) {
        if (!ObjectUtils.isEmpty(internshipReleaseBean)) {
            if (!ObjectUtils.isEmpty(internshipReleaseBean.getDepartmentId()) && internshipReleaseBean.getDepartmentId() > 0) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(internshipReleaseBean.getDepartmentId()));
                } else {
                    a = INTERNSHIP_RELEASE.DEPARTMENT_ID.eq(internshipReleaseBean.getDepartmentId());
                }
            }

            if (!ObjectUtils.isEmpty(internshipReleaseBean.getCollegeId()) && internshipReleaseBean.getCollegeId() > 0) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(COLLEGE.COLLEGE_ID.eq(internshipReleaseBean.getCollegeId()));
                } else {
                    a = COLLEGE.COLLEGE_ID.eq(internshipReleaseBean.getCollegeId());
                }
            }

            if (!ObjectUtils.isEmpty(internshipReleaseBean.getInternshipReleaseIsDel())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_IS_DEL.eq(internshipReleaseBean.getInternshipReleaseIsDel()));
                } else {
                    a = INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_IS_DEL.eq(internshipReleaseBean.getInternshipReleaseIsDel());
                }
            }

            if (!ObjectUtils.isEmpty(internshipReleaseBean.getAllowGrade())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(INTERNSHIP_RELEASE.ALLOW_GRADE.eq(internshipReleaseBean.getAllowGrade()));
                } else {
                    a = INTERNSHIP_RELEASE.ALLOW_GRADE.eq(internshipReleaseBean.getAllowGrade());
                }
            }
        }
        return a;
    }
}
