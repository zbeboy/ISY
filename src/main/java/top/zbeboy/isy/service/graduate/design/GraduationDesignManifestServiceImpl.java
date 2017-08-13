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
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;
import static top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_DECLARE;
import static top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE;

/**
 * Created by lenovo on 2017-08-13.
 */
@Slf4j
@Service("graduationDesignManifestService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignManifestServiceImpl extends DataTablesPlugin<GraduationDesignDeclareBean> implements GraduationDesignManifestService {

    private final DSLContext create;

    @Autowired
    public GraduationDesignManifestServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<GraduationDesignDeclareBean> findAllManifestByPage(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean) {
        List<GraduationDesignDeclareBean> graduationDesignDeclareBeens = new ArrayList<>();
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDeclareBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record> selectJoinStep = create.select()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(DEFENSE_GROUP_MEMBER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID));
            sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            records = selectJoinStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(DEFENSE_GROUP_MEMBER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        buildManifestData(records, graduationDesignDeclareBeens);
        return graduationDesignDeclareBeens;
    }

    @Override
    public int countAllManifest(GraduationDesignDeclareBean graduationDesignDeclareBean) {
        Record1<Integer> count;
        Condition a = otherCondition(null, graduationDesignDeclareBean);
        if (ObjectUtils.isEmpty(a)) {
            count = create.selectCount()
                    .from(DEFENSE_ORDER)
                    .fetchOne();
        } else {
            count = create.selectCount()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(DEFENSE_GROUP_MEMBER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a)
                    .fetchOne();
        }
        return count.value1();
    }

    @Override
    public int countManifestByCondition(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDeclareBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(DEFENSE_GROUP_MEMBER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    @Override
    public List<GraduationDesignDeclareBean> exportManifestData(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean) {
        List<GraduationDesignDeclareBean> graduationDesignDeclareBeens = new ArrayList<>();
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDeclareBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record> selectJoinStep = create.select()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(DEFENSE_GROUP_MEMBER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID));
            records = selectJoinStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .join(DEFENSE_GROUP_MEMBER)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(DEFENSE_GROUP_MEMBER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(DEFENSE_ORDER.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .join(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .join(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a);
            records = selectConditionStep.fetch();
        }
        buildManifestData(records, graduationDesignDeclareBeens);
        return graduationDesignDeclareBeens;
    }


    private void buildManifestData(Result<Record> records, List<GraduationDesignDeclareBean> graduationDesignDeclareBeens) {
        for (Record r : records) {
            GraduationDesignDeclareBean tempGraduationDesignDeclareBean = new GraduationDesignDeclareBean();
            tempGraduationDesignDeclareBean.setSubjectTypeId(r.getValue(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID));
            tempGraduationDesignDeclareBean.setSubjectTypeName(r.getValue(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME));
            tempGraduationDesignDeclareBean.setOriginTypeId(r.getValue(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID));
            tempGraduationDesignDeclareBean.setOriginTypeName(r.getValue(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME));
            tempGraduationDesignDeclareBean.setGuidePeoples(r.getValue(GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES));
            tempGraduationDesignDeclareBean.setGraduationDesignPresubjectId(r.getValue(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID));
            tempGraduationDesignDeclareBean.setStaffId(r.getValue(STAFF.STAFF_ID));
            tempGraduationDesignDeclareBean.setStaffName(r.getValue(USERS.as("T").REAL_NAME));
            tempGraduationDesignDeclareBean.setAcademicTitleName(r.getValue(ACADEMIC_TITLE.ACADEMIC_TITLE_NAME));
            tempGraduationDesignDeclareBean.setStudentName(r.getValue(DEFENSE_ORDER.STUDENT_NAME));
            tempGraduationDesignDeclareBean.setStudentNumber(r.getValue(DEFENSE_ORDER.STUDENT_NUMBER));
            tempGraduationDesignDeclareBean.setPresubjectTitle(r.getValue(GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE));
            tempGraduationDesignDeclareBean.setGraduationDesignReleaseId(r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID));
            tempGraduationDesignDeclareBean.setScoreTypeName(r.getValue(SCORE_TYPE.SCORE_TYPE_NAME));
            tempGraduationDesignDeclareBean.setDefenseOrderId(r.getValue(DEFENSE_ORDER.DEFENSE_ORDER_ID));
            graduationDesignDeclareBeens.add(tempGraduationDesignDeclareBean);
        }
    }

    /**
     * 其它条件
     *
     * @param graduationDesignDeclareBean 条件
     * @return 条件
     */
    public Condition otherCondition(Condition a, GraduationDesignDeclareBean graduationDesignDeclareBean) {
        if (!ObjectUtils.isEmpty(graduationDesignDeclareBean)) {
            if (!ObjectUtils.isEmpty(graduationDesignDeclareBean.getStaffId()) && graduationDesignDeclareBean.getStaffId() > 0) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDeclareBean.getStaffId()));
                } else {
                    a = GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDeclareBean.getStaffId());
                }
            }

            if (StringUtils.hasLength(graduationDesignDeclareBean.getGraduationDesignReleaseId())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.getGraduationDesignReleaseId()));
                } else {
                    a = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDeclareBean.getGraduationDesignReleaseId());
                }
            }
        }

        return a;
    }

    /**
     * 数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));

            if (StringUtils.hasLength(studentName)) {
                a = DEFENSE_ORDER.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName));
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = DEFENSE_ORDER.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(DEFENSE_ORDER.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
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
    public void sortCondition(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("presubject_title".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("subject_type_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("origin_type_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("guide_teacher".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.as("T").REAL_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = USERS.as("T").REAL_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("academic_title_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = ACADEMIC_TITLE.ACADEMIC_TITLE_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("guide_peoples".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DECLARE.GUIDE_PEOPLES.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NUMBER.asc();
                } else {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NUMBER.desc();
                }
            }

            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = DEFENSE_ORDER.STUDENT_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("score_type_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCORE_TYPE.SCORE_TYPE_ID.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = SCORE_TYPE.SCORE_TYPE_ID.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
