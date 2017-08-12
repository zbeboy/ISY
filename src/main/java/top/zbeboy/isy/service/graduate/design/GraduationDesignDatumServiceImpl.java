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
import top.zbeboy.isy.domain.tables.daos.GraduationDesignDatumDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatum;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zbeboy on 2017/6/23.
 */
@Slf4j
@Service("graduationDesignDatumService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignDatumServiceImpl extends DataTablesPlugin<GraduationDesignDatumBean> implements GraduationDesignDatumService {

    private final DSLContext create;

    @Resource
    private GraduationDesignDatumDao graduationDesignDatumDao;

    @Autowired
    public GraduationDesignDatumServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public GraduationDesignDatum findById(String id) {
        return graduationDesignDatumDao.findById(id);
    }

    @Override
    public Optional<Record> findByIdRelation(String id) {
        return create.select()
                .from(GRADUATION_DESIGN_DATUM)
                .join(FILES)
                .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                .join(GRADUATION_DESIGN_TUTOR)
                .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Optional<Record> findByGraduationDesignTutorIdAndGraduationDesignDatumTypeId(String graduationDesignTutorId, int graduationDesignDatumTypeId) {
        return create.select()
                .from(GRADUATION_DESIGN_DATUM)
                .join(GRADUATION_DESIGN_TUTOR)
                .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                .where(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(graduationDesignTutorId).and(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(graduationDesignDatumTypeId)))
                .fetchOptional();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils, GraduationDesignDatumBean graduationDesignDatumBean) {
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDatumBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record> selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID));
            sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            records = selectJoinStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .where(a);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        return records;
    }

    @Override
    public int countAll(GraduationDesignDatumBean graduationDesignDatumBean) {
        Record1<Integer> count;
        Condition a = otherCondition(null, graduationDesignDatumBean);
        if (ObjectUtils.isEmpty(a)) {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .fetchOne();
        } else {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .where(a)
                    .fetchOne();
        }
        return count.value1();
    }

    @Override
    public int countByCondition(DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils, GraduationDesignDatumBean graduationDesignDatumBean) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDatumBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    @Override
    public Result<Record> findTeamAllByPage(DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils, GraduationDesignDatumBean graduationDesignDatumBean) {
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDatumBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record> selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID));
            sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            records = selectJoinStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(a);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        return records;
    }

    @Override
    public int countTeamAll(GraduationDesignDatumBean graduationDesignDatumBean) {
        Record1<Integer> count;
        Condition a = otherCondition(null, graduationDesignDatumBean);
        if (ObjectUtils.isEmpty(a)) {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .fetchOne();
        } else {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(a)
                    .fetchOne();
        }
        return count.value1();
    }

    @Override
    public int countTeamByCondition(DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils, GraduationDesignDatumBean graduationDesignDatumBean) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduationDesignDatumBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_DATUM)
                    .join(FILES)
                    .on(GRADUATION_DESIGN_DATUM.FILE_ID.eq(FILES.FILE_ID))
                    .join(GRADUATION_DESIGN_DATUM_TYPE)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(GRADUATION_DESIGN_DATUM_TYPE.GRADUATION_DESIGN_DATUM_TYPE_ID))
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID))
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    @Override
    public void update(GraduationDesignDatum graduationDesignDatum) {
        graduationDesignDatumDao.update(graduationDesignDatum);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignDatum graduationDesignDatum) {
        graduationDesignDatumDao.insert(graduationDesignDatum);
    }

    @Override
    public void deleteById(String id) {
        graduationDesignDatumDao.deleteById(id);
    }

    /**
     * 其它条件
     *
     * @param graduationDesignDatumBean 条件
     * @return 条件
     */
    public Condition otherCondition(Condition a, GraduationDesignDatumBean graduationDesignDatumBean) {
        if (!ObjectUtils.isEmpty(graduationDesignDatumBean)) {
            if (StringUtils.hasLength(graduationDesignDatumBean.getGraduationDesignTutorId())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(graduationDesignDatumBean.getGraduationDesignTutorId()));
                } else {
                    a = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_TUTOR_ID.eq(graduationDesignDatumBean.getGraduationDesignTutorId());
                }
            }

            if (StringUtils.hasLength(graduationDesignDatumBean.getGraduationDesignReleaseId())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDatumBean.getGraduationDesignReleaseId()));
                } else {
                    a = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignDatumBean.getGraduationDesignReleaseId());
                }
            }

            if (!ObjectUtils.isEmpty(graduationDesignDatumBean.getStaffId()) && graduationDesignDatumBean.getStaffId() > 0) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDatumBean.getStaffId()));
                } else {
                    a = GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(graduationDesignDatumBean.getStaffId());
                }
            }
        }
        return a;
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String originalFileName = StringUtils.trimWhitespace(search.getString("originalFileName"));
            String graduationDesignDatumTypeName = StringUtils.trimWhitespace(search.getString("graduationDesignDatumTypeName"));
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            if (StringUtils.hasLength(originalFileName)) {
                a = FILES.ORIGINAL_FILE_NAME.like(SQLQueryUtils.likeAllParam(originalFileName));
            }

            if (StringUtils.hasLength(graduationDesignDatumTypeName)) {
                int graduationDesignDatumTypeId = NumberUtils.toInt(graduationDesignDatumTypeName);
                if (graduationDesignDatumTypeId > 0) {
                    if (ObjectUtils.isEmpty(a)) {
                        a = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(graduationDesignDatumTypeId);
                    } else {
                        a = a.and(GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.eq(graduationDesignDatumTypeId));
                    }
                }
            }

            if (StringUtils.hasLength(studentName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName));
                } else {
                    a = a.and(USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName)));
                }
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
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {

            if ("real_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.asc();
                } else {
                    sortField[0] = USERS.REAL_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = STUDENT.STUDENT_NUMBER.asc();
                } else {
                    sortField[0] = STUDENT.STUDENT_NUMBER.desc();
                }
            }

            if ("organize_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.asc();
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.desc();
                }
            }

            if ("original_file_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = FILES.ORIGINAL_FILE_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_PRESUBJECT.PRESUBJECT_TITLE.desc();
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.desc();
                }
            }

            if ("graduation_design_datum_type_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.asc();
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_TYPE_ID.desc();
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.desc();
                }
            }

            if ("version".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DATUM.VERSION.asc();
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DATUM.VERSION.desc();
                    sortField[1] = GRADUATION_DESIGN_DATUM.GRADUATION_DESIGN_DATUM_ID.desc();
                }
            }

            if ("update_time".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_DATUM.UPDATE_TIME.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_DATUM.UPDATE_TIME.desc();
                }
            }
        }
        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
