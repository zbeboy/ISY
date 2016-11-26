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
import top.zbeboy.isy.domain.tables.daos.DepartmentDao;
import top.zbeboy.isy.domain.tables.pojos.Department;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.DepartmentRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.department.DepartmentBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.COLLEGE;
import static top.zbeboy.isy.domain.Tables.DEPARTMENT;
import static top.zbeboy.isy.domain.Tables.SCHOOL;

/**
 * Created by lenovo on 2016-08-21.
 */
@Service("departmentService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DepartmentServiceImpl extends DataTablesPlugin<DepartmentBean> implements DepartmentService {

    private final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DSLContext create;

    private DepartmentDao departmentDao;

    @Resource
    private RoleService roleService;

    @Resource
    private UsersService usersService;

    @Autowired
    public DepartmentServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.departmentDao = new DepartmentDao(configuration);
    }

    @Override
    public Result<DepartmentRecord> findByCollegeId(int collegeId) {
        Byte isDel = 0;
        return create.selectFrom(DEPARTMENT)
                .where(DEPARTMENT.DEPARTMENT_IS_DEL.eq(isDel).and(DEPARTMENT.COLLEGE_ID.eq(collegeId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(Department department) {
        departmentDao.insert(department);
    }

    @Override
    public void update(Department department) {
        departmentDao.update(department);
    }

    @Override
    public void updateIsDel(List<Integer> ids, Byte isDel) {
        for (int id : ids) {
            create.update(DEPARTMENT).set(DEPARTMENT.DEPARTMENT_IS_DEL, isDel).where(DEPARTMENT.DEPARTMENT_ID.eq(id)).execute();
        }
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<DepartmentBean> dataTablesUtils) {
        Result<Record> records = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectJoinStep<Record> selectJoinStep = create.select()
                        .from(DEPARTMENT)
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
                        .from(DEPARTMENT)
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
                        .from(DEPARTMENT)
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
                        .from(DEPARTMENT)
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
            return statisticsAll(create, DEPARTMENT);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            Record1<Integer> count = create.selectCount()
                    .from(DEPARTMENT)
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(COLLEGE.COLLEGE_ID.eq(collegeId))
                    .fetchOne();
            return count.value1();
        }
        return 0;

    }

    @Override
    public int countByCondition(DataTablesUtils<DepartmentBean> dataTablesUtils) {
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                        .from(DEPARTMENT);
                count = selectJoinStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(DEPARTMENT)
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId));
                count = selectConditionStep.fetchOne();
            }

        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(DEPARTMENT)
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
                        .from(DEPARTMENT)
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
    public Result<DepartmentRecord> findByDepartmentNameAndCollegeId(String departmentName, int collegeId) {
        return create.selectFrom(DEPARTMENT)
                .where(DEPARTMENT.DEPARTMENT_NAME.eq(departmentName).and(DEPARTMENT.COLLEGE_ID.eq(collegeId)))
                .fetch();
    }

    @Override
    public Result<DepartmentRecord> findByDepartmentNameAndCollegeIdNeDepartmentId(String departmentName, int departmentId, int collegeId) {
        return create.selectFrom(DEPARTMENT)
                .where(DEPARTMENT.DEPARTMENT_NAME.eq(departmentName).and(DEPARTMENT.DEPARTMENT_ID.ne(departmentId)).and(DEPARTMENT.COLLEGE_ID.eq(collegeId)))
                .fetch();
    }

    @Override
    public Optional<Record> findByIdRelation(int id) {
        return create.select()
                .from(DEPARTMENT)
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(DEPARTMENT.DEPARTMENT_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Department findById(int id) {
        return departmentDao.findById(id);
    }

    /**
     * 系数据全局搜索条件
     *
     * @param dataTablesUtils
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<DepartmentBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String schoolName = StringUtils.trimWhitespace(search.getString("schoolName"));
            String collegeName = StringUtils.trimWhitespace(search.getString("collegeName"));
            String departmentName = StringUtils.trimWhitespace(search.getString("departmentName"));
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

        }
        return a;
    }

    /**
     * 系数据排序
     *
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    @Override
    public void sortCondition(DataTablesUtils<DepartmentBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField<Integer> a = null;
        SortField<String> b = null;
        SortField<Byte> c = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("department_id".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    a = DEPARTMENT.DEPARTMENT_ID.asc();
                } else {
                    a = DEPARTMENT.DEPARTMENT_ID.desc();
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    b = SCHOOL.SCHOOL_NAME.asc();
                } else {
                    b = SCHOOL.SCHOOL_NAME.desc();
                }
            }

            if ("college_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    b = COLLEGE.COLLEGE_NAME.asc();
                } else {
                    b = COLLEGE.COLLEGE_NAME.desc();
                }
            }

            if ("department_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    b = DEPARTMENT.DEPARTMENT_NAME.asc();
                } else {
                    b = DEPARTMENT.DEPARTMENT_NAME.desc();
                }
            }

            if ("department_is_del".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    c = DEPARTMENT.DEPARTMENT_IS_DEL.asc();
                } else {
                    c = DEPARTMENT.DEPARTMENT_IS_DEL.desc();
                }
            }

        }

        if (!ObjectUtils.isEmpty(a)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(a);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(a);
            }

        } else if (!ObjectUtils.isEmpty(b)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(b);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(b);
            }
        } else if (!ObjectUtils.isEmpty(c)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(c);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(c);
            }
        } else {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(DEPARTMENT.DEPARTMENT_ID.desc());
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(DEPARTMENT.DEPARTMENT_ID.desc());
            }
        }
    }
}
