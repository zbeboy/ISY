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
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.daos.ScienceDao;
import top.zbeboy.isy.domain.tables.pojos.Science;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.ScienceRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.science.ScienceBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-08-21.
 */
@Service("scienceService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ScienceServiceImpl extends DataTablesPlugin<ScienceBean> implements ScienceService {
    private final Logger log = LoggerFactory.getLogger(ScienceServiceImpl.class);

    private final DSLContext create;

    @Resource
    private ScienceDao scienceDao;

    @Resource
    private RoleService roleService;

    @Resource
    private UsersService usersService;

    @Autowired
    public ScienceServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<ScienceRecord> findByDepartmentId(int departmentId) {
        Byte isDel = 0;
        return create.selectFrom(SCIENCE)
                .where(SCIENCE.SCIENCE_IS_DEL.eq(isDel).and(SCIENCE.DEPARTMENT_ID.eq(departmentId)))
                .fetch();
    }

    @Override
    public Result<Record2<String, Integer>> findByGradeAndDepartmentId(String grade, int departmentId) {
        Byte isDel = 0;
        return create.selectDistinct(SCIENCE.SCIENCE_NAME, SCIENCE.SCIENCE_ID)
                .from(SCIENCE)
                .join(ORGANIZE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .where(SCIENCE.SCIENCE_IS_DEL.eq(isDel).and(ORGANIZE.GRADE.eq(grade)).and(SCIENCE.DEPARTMENT_ID.eq(departmentId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(Science science) {
        scienceDao.insert(science);
    }

    @Override
    public void update(Science science) {
        scienceDao.update(science);
    }

    @Override
    public void updateIsDel(List<Integer> ids, Byte isDel) {
        for (int id : ids) {
            create.update(SCIENCE).set(SCIENCE.SCIENCE_IS_DEL, isDel).where(SCIENCE.SCIENCE_ID.eq(id)).execute();
        }
    }

    @Override
    public Science findById(int id) {
        return scienceDao.findById(id);
    }

    @Override
    public Optional<Record> findByIdRelation(int id) {
        return create.select()
                .from(SCIENCE)
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(SCIENCE.SCIENCE_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<ScienceBean> dataTablesUtils) {
        Result<Record> records = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectJoinStep<Record> selectJoinStep = create.select()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID));
                sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
                pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
                records = selectJoinStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            }
        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a);
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            }

        }
        return records;
    }

    @Override
    public int countAll() {
        // 分权限显示用户数据
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            return statisticsAll(create, SCIENCE);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            Record1<Integer> count = create.selectCount()
                    .from(SCIENCE)
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(COLLEGE.COLLEGE_ID.eq(collegeId))
                    .fetchOne();
            return count.value1();
        }
        return 0;
    }

    @Override
    public int countByCondition(DataTablesUtils<ScienceBean> dataTablesUtils) {
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                        .from(SCIENCE);
                count = selectJoinStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId));
                count = selectConditionStep.fetchOne();
            }

        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(SCIENCE)
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a));
                count = selectConditionStep.fetchOne();
            }

        }
        if (!ObjectUtils.isEmpty(count)) {
            return count.value1();
        }
        return 0;
    }

    @Override
    public Result<ScienceRecord> findByScienceNameAndDepartmentId(String scienceName, int departmentId) {
        return create.selectFrom(SCIENCE)
                .where(SCIENCE.SCIENCE_NAME.eq(scienceName).and(SCIENCE.DEPARTMENT_ID.eq(departmentId)))
                .fetch();
    }

    @Override
    public Result<ScienceRecord> findByScienceNameAndDepartmentIdNeScienceId(String scienceName, int scienceId, int departmentId) {
        return create.selectFrom(SCIENCE)
                .where(SCIENCE.SCIENCE_NAME.eq(scienceName).and(SCIENCE.DEPARTMENT_ID.eq(departmentId)).and(SCIENCE.SCIENCE_ID.ne(scienceId)))
                .fetch();
    }

    /**
     * 专业数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<ScienceBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String schoolName = StringUtils.trimWhitespace(search.getString("schoolName"));
            String collegeName = StringUtils.trimWhitespace(search.getString("collegeName"));
            String departmentName = StringUtils.trimWhitespace(search.getString("departmentName"));
            String scienceName = StringUtils.trimWhitespace(search.getString("scienceName"));
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName));
            }

            if (StringUtils.hasLength(collegeName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName));
                } else {
                    a = a.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName)));
                }
            }

            if (StringUtils.hasLength(departmentName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(departmentName));
                } else {
                    a = a.and(DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(departmentName)));
                }
            }

            if (StringUtils.hasLength(scienceName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = SCIENCE.SCIENCE_NAME.like(SQLQueryUtils.likeAllParam(scienceName));
                } else {
                    a = a.and(SCIENCE.SCIENCE_NAME.like(SQLQueryUtils.likeAllParam(scienceName)));
                }
            }

        }
        return a;
    }

    /**
     * 专业数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<ScienceBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("science_id".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = SCIENCE.SCIENCE_ID.asc();
                } else {
                    sortField = SCIENCE.SCIENCE_ID.desc();
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = SCHOOL.SCHOOL_NAME.asc();
                } else {
                    sortField = SCHOOL.SCHOOL_NAME.desc();
                }
            }

            if ("college_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = COLLEGE.COLLEGE_NAME.asc();
                } else {
                    sortField = COLLEGE.COLLEGE_NAME.desc();
                }
            }

            if ("department_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = DEPARTMENT.DEPARTMENT_NAME.asc();
                } else {
                    sortField = DEPARTMENT.DEPARTMENT_NAME.desc();
                }
            }

            if ("science_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = SCIENCE.SCIENCE_NAME.asc();
                } else {
                    sortField = SCIENCE.SCIENCE_NAME.desc();
                }
            }

            if ("science_is_del".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = SCIENCE.SCIENCE_IS_DEL.asc();
                } else {
                    sortField = SCIENCE.SCIENCE_IS_DEL.desc();
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
