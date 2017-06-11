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
import top.zbeboy.isy.domain.tables.daos.GraduationDesignTutorDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTutor;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2017-05-20.
 */
@Slf4j
@Service("graduationDesignTutorService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignTutorServiceImpl implements GraduationDesignTutorService {

    private final DSLContext create;

    @Resource
    private GraduationDesignTutorDao graduationDesignTutorDao;

    @Autowired
    public GraduationDesignTutorServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public GraduationDesignTutor findById(String id) {
        return graduationDesignTutorDao.findById(id);
    }

    @Override
    public Optional<Record> findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(int studentId, String graduationDesignReleaseId) {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetchOptional();
    }

    @Override
    public Optional<Record> findByStudentIdAndGraduationDesignReleaseIdRelation(int studentId, String graduationDesignReleaseId) {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetchOptional();
    }

    @Override
    public Result<Record> findByStaffIdAndGraduationDesignReleaseIdRelationForStudent(int staffId, String graduationDesignReleaseId) {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .join(STUDENT)
                .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .where(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(staffId)
                        .and(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetch();
    }

    @Override
    public Result<Record> findByGraduationDesignTeacherIdRelationForStudent(String graduationDesignTeacherId) {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(STUDENT)
                .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .where(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId))
                .fetch();
    }

    @Override
    public int countNotFillStudent(GraduationDesignReleaseBean graduationDesignReleaseBean) {
        Select<Record> select = create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseBean.getGraduationDesignReleaseId()).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID)));
        Record1<Integer> count = create.selectCount()
                .from(STUDENT)
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .where(SCIENCE.SCIENCE_ID.eq(graduationDesignReleaseBean.getScienceId()).and(ORGANIZE.GRADE.eq(graduationDesignReleaseBean.getAllowGrade()))
                        .andNotExists(select))
                .fetchOne();
        return count.value1();
    }

    @Override
    public int countFillStudent(GraduationDesignReleaseBean graduationDesignReleaseBean) {
        Record1<Integer> count = create.selectCount()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseBean.getGraduationDesignReleaseId()))
                .fetchOne();
        return count.value1();
    }

    @Override
    public void deleteByGraduationDesignTeacherId(String graduationDesignTeacherId) {
        create.deleteFrom(GRADUATION_DESIGN_TUTOR)
                .where(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(graduationDesignTeacherId))
                .execute();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignTutor graduationDesignTutor) {
        graduationDesignTutorDao.insert(graduationDesignTutor);
    }

    @Override
    public void update(GraduationDesignTutor graduationDesignTutor) {
        graduationDesignTutorDao.update(graduationDesignTutor);
    }

    @Override
    public void deleteByIds(List<String> ids) {
        graduationDesignTutorDao.deleteById(ids);
    }

    @Override
    public List<GraduationDesignTutorBean> findAllFillByPage(DataTablesUtils<GraduationDesignTutorBean> dataTablesUtils, GraduationDesignTutorBean condition) {
        List<GraduationDesignTutorBean> graduationDesignTutorBeens = new ArrayList<>();
        Result<Record> records;
        Condition a = searchFillCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TUTOR)
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT.join(USERS.as("T")).on(STUDENT.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(STAFF.join(USERS.as("S")).on(STAFF.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.getGraduationDesignReleaseId()));
            sortFillCondition(dataTablesUtils, selectConditionStep);
            fillPagination(dataTablesUtils, selectConditionStep);
            records = selectConditionStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TUTOR)
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT.join(USERS.as("T")).on(STUDENT.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(STAFF.join(USERS.as("S")).on(STAFF.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.getGraduationDesignReleaseId()).and(a));
            sortFillCondition(dataTablesUtils, selectConditionStep);
            fillPagination(dataTablesUtils, selectConditionStep);
            records = selectConditionStep.fetch();
        }
        for (Record r : records) {
            GraduationDesignTutorBean graduationDesignTutorBean = new GraduationDesignTutorBean();
            graduationDesignTutorBean.setGraduationDesignTeacherId(r.getValue(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID));
            graduationDesignTutorBean.setStudentId(r.getValue(GRADUATION_DESIGN_TUTOR.STUDENT_ID));
            graduationDesignTutorBean.setGraduationDesignTutorId(r.getValue(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID));
            graduationDesignTutorBean.setStudentName(r.getValue(USERS.as("T").REAL_NAME));
            graduationDesignTutorBean.setStudentNumber(r.getValue(STUDENT.STUDENT_NUMBER));
            graduationDesignTutorBean.setStaffName(r.getValue(USERS.as("S").REAL_NAME));
            graduationDesignTutorBean.setOrganizeName(r.getValue(ORGANIZE.ORGANIZE_NAME));
            graduationDesignTutorBeens.add(graduationDesignTutorBean);
        }
        return graduationDesignTutorBeens;
    }

    @Override
    public int countAllFill(GraduationDesignTutorBean condition) {
        Record1<Integer> count = create.selectCount()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.getGraduationDesignReleaseId()))
                .fetchOne();
        return count.value1();
    }

    @Override
    public int countFillByCondition(DataTablesUtils<GraduationDesignTutorBean> dataTablesUtils, GraduationDesignTutorBean condition) {
        Record1<Integer> count;
        Condition a = searchFillCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TUTOR)
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.getGraduationDesignReleaseId()));
            count = selectConditionStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TUTOR)
                    .join(GRADUATION_DESIGN_TEACHER)
                    .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                    .join(STUDENT.join(USERS.as("T")).on(STUDENT.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(STAFF.join(USERS.as("S")).on(STAFF.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.getGraduationDesignReleaseId()).and(a));
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    @Override
    public Result<Record> findAllNotFillByPage(DataTablesUtils<StudentBean> dataTablesUtils, GraduationDesignRelease condition) {
        Result<Record> records;
        Condition a = searchNotFillCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(STUDENT)
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(SCIENCE.SCIENCE_ID.eq(condition.getScienceId()).and(ORGANIZE.GRADE.eq(condition.getAllowGrade()))
                            .andNotExists(selectTutor(condition)));
            sortNotFillCondition(dataTablesUtils, selectConditionStep);
            notFillPagination(dataTablesUtils, selectConditionStep);
            records = selectConditionStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(STUDENT)
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(SCIENCE.SCIENCE_ID.eq(condition.getScienceId()).and(ORGANIZE.GRADE.eq(condition.getAllowGrade()))
                            .andNotExists(selectTutor(condition)).and(a));
            sortNotFillCondition(dataTablesUtils, selectConditionStep);
            notFillPagination(dataTablesUtils, selectConditionStep);
            records = selectConditionStep.fetch();
        }
        return records;
    }

    @Override
    public int countAllNotFill(GraduationDesignRelease condition) {
        Record1<Integer> count = create.selectCount()
                .from(STUDENT)
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .where(SCIENCE.SCIENCE_ID.eq(condition.getScienceId()).and(ORGANIZE.GRADE.eq(condition.getAllowGrade()))
                        .andNotExists(selectTutor(condition)))
                .fetchOne();
        return count.value1();
    }

    @Override
    public int countNotFillByCondition(DataTablesUtils<StudentBean> dataTablesUtils, GraduationDesignRelease condition) {
        Record1<Integer> count;
        Condition a = searchNotFillCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(STUDENT)
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .where(SCIENCE.SCIENCE_ID.eq(condition.getScienceId()).and(ORGANIZE.GRADE.eq(condition.getAllowGrade()))
                            .andNotExists(selectTutor(condition)));
            count = selectConditionStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(STUDENT)
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .where(SCIENCE.SCIENCE_ID.eq(condition.getScienceId()).and(ORGANIZE.GRADE.eq(condition.getAllowGrade()))
                            .andNotExists(selectTutor(condition)).and(a));
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    @Override
    public int countByStaffId(int staffId) {
        Record1<Integer> count = create.selectCount()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(staffId))
                .fetchOne();
        return count.value1();
    }

    private Select<Record> selectTutor(GraduationDesignRelease condition) {
        return create.select()
                .from(GRADUATION_DESIGN_TUTOR)
                .join(GRADUATION_DESIGN_TEACHER)
                .on(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(condition.getGraduationDesignReleaseId()).and(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID)));
    }

    /**
     * 已填报学生搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    private Condition searchFillCondition(DataTablesUtils<GraduationDesignTutorBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String organizeName = StringUtils.trimWhitespace(search.getString("organizeName"));
            if (StringUtils.hasLength(studentName)) {
                a = USERS.as("T").REAL_NAME.like(SQLQueryUtils.likeAllParam(studentName));
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                }
            }

            if (StringUtils.hasLength(organizeName)) {
                int organizeId = NumberUtils.toInt(organizeName);
                if (organizeId > 0) {
                    if (ObjectUtils.isEmpty(a)) {
                        a = ORGANIZE.ORGANIZE_ID.eq(organizeId);
                    } else {
                        a = a.and(ORGANIZE.ORGANIZE_ID.eq(organizeId));
                    }
                }
            }
        }
        return a;
    }

    /**
     * 未填报学生搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    private Condition searchNotFillCondition(DataTablesUtils<StudentBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String organizeName = StringUtils.trimWhitespace(search.getString("organizeName"));
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

            if (StringUtils.hasLength(organizeName)) {
                int organizeId = NumberUtils.toInt(organizeName);
                if (organizeId > 0) {
                    if (ObjectUtils.isEmpty(a)) {
                        a = ORGANIZE.ORGANIZE_ID.eq(organizeId);
                    } else {
                        a = a.and(ORGANIZE.ORGANIZE_ID.eq(organizeId));
                    }
                }
            }
        }
        return a;
    }

    /**
     * 已填报学生数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    private void sortFillCondition(DataTablesUtils<GraduationDesignTutorBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.as("T").REAL_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.asc();
                } else {
                    sortField[0] = USERS.as("T").REAL_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.desc();
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
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.asc();
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.desc();
                }
            }

            if ("staff_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.as("S").REAL_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.asc();
                } else {
                    sortField[0] = USERS.as("S").REAL_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TUTOR_ID.desc();
                }
            }
        }

        if (!ObjectUtils.isEmpty(sortField)) {
            selectConditionStep.orderBy(sortField);
        }
    }

    /**
     * 未填报学生数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    private void sortNotFillCondition(DataTablesUtils<StudentBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc();
                    sortField[1] = STUDENT.STUDENT_ID.asc();
                } else {
                    sortField[0] = USERS.REAL_NAME.desc();
                    sortField[1] = STUDENT.STUDENT_ID.desc();
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
                    sortField[1] = STUDENT.STUDENT_ID.asc();
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc();
                    sortField[1] = STUDENT.STUDENT_ID.desc();
                }
            }
        }

        if (!ObjectUtils.isEmpty(sortField)) {
            selectConditionStep.orderBy(sortField);
        }
    }

    /**
     * 已填报学生分页
     *
     * @param dataTablesUtils     分页工具
     * @param selectConditionStep 条件
     */
    private void fillPagination(DataTablesUtils<GraduationDesignTutorBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        int start = dataTablesUtils.getStart();
        int length = dataTablesUtils.getLength();
        selectConditionStep.limit(start, length);
    }

    /**
     * 未填报学生分页
     *
     * @param dataTablesUtils     分页工具
     * @param selectConditionStep 条件
     */
    private void notFillPagination(DataTablesUtils<StudentBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        int start = dataTablesUtils.getStart();
        int length = dataTablesUtils.getLength();
        selectConditionStep.limit(start, length);
    }
}
