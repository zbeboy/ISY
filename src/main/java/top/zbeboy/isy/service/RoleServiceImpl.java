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
import top.zbeboy.isy.domain.tables.daos.RoleDao;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.CollegeRoleRecord;
import top.zbeboy.isy.domain.tables.records.RoleRecord;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.platform.role.RoleBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.COLLEGE_ROLE;
import static top.zbeboy.isy.domain.Tables.ROLE;
import static top.zbeboy.isy.domain.Tables.COLLEGE;
import static top.zbeboy.isy.domain.Tables.SCHOOL;

/**
 * Created by lenovo on 2016-10-04.
 */
@Service("roleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RoleServiceImpl extends DataTablesPlugin<RoleBean> implements RoleService {

    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final DSLContext create;

    private RoleDao roleDao;

    @Resource
    private UsersService usersService;

    @Resource
    private AuthoritiesService authoritiesService;

    @Autowired
    public RoleServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.roleDao = new RoleDao(configuration);
    }

    @Override
    public Role findByRoleName(String roleName) {
        return roleDao.fetchOne(ROLE.ROLE_NAME, roleName);
    }

    @Override
    public Role findById(int id) {
        return roleDao.findById(id);
    }

    @Override
    public Optional<Record> findByRoleIdRelation(int roleId) {
        return create.select()
                .from(ROLE)
                .leftJoin(COLLEGE_ROLE)
                .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                .leftJoin(COLLEGE)
                .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .leftJoin(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(ROLE.ROLE_ID.eq(roleId))
                .fetchOptional();
    }

    @Override
    public Result<Record> findByRoleNameAndCollegeId(String roleName, int collegeId) {
        return create.select()
                .from(ROLE)
                .leftJoin(COLLEGE_ROLE)
                .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                .where(ROLE.ROLE_NAME.eq(roleName).and(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId)))
                .fetch();
    }

    @Override
    public Result<Record> findByRoleNameAndCollegeIdNeRoleId(String roleName, int collegeId, int roleId) {
        return create.select()
                .from(ROLE)
                .leftJoin(COLLEGE_ROLE)
                .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                .where(ROLE.ROLE_NAME.eq(roleName).and(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId)).and(ROLE.ROLE_ID.ne(roleId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(Role role) {
        roleDao.insert(role);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public int saveAndReturnId(Role role) {
        RoleRecord roleRecord = create.insertInto(ROLE)
                .set(ROLE.ROLE_NAME, role.getRoleName())
                .set(ROLE.ROLE_EN_NAME, role.getRoleEnName())
                .returning(ROLE.ROLE_ID)
                .fetchOne();
        return roleRecord.getRoleId();
    }

    @Override
    public void update(Role role) {
        roleDao.update(role);
    }

    @Override
    public void deleteById(int id) {
        roleDao.deleteById(id);
    }

    @Override
    public Result<RoleRecord> findInRoleId(List<Integer> ids) {
        return create.selectFrom(ROLE)
                .where(ROLE.ROLE_ID.in(ids))
                .fetch();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<RoleBean> dataTablesUtils) {
        Result<Record> records = null;
        List<String> defaultRoles = getDefaultRoles();
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (authoritiesService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            } else if (authoritiesService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = authoritiesService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            }
        } else {
            // 分权限显示用户数据
            if (authoritiesService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(a));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            } else if (authoritiesService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = authoritiesService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
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
        if (authoritiesService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            List<String> defaultRoles = getDefaultRoles();
            Record1<Integer> count = create.selectCount()
                    .from(ROLE)
                    .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles))
                    .fetchOne();
            return count.value1();
        } else if (authoritiesService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = authoritiesService.getRoleCollegeId(record);
            Record1<Integer> count = create.selectCount()
                    .from(ROLE)
                    .leftJoin(COLLEGE_ROLE)
                    .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                    .where(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId))
                    .fetchOne();
            return count.value1();
        }
        return 0;
    }

    @Override
    public int countByCondition(DataTablesUtils<RoleBean> dataTablesUtils) {
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        List<String> defaultRoles = getDefaultRoles();
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (authoritiesService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles));
                count = selectConditionStep.fetchOne();
            } else if (authoritiesService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = authoritiesService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .where(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId));
                count = selectConditionStep.fetchOne();
            }
        } else {
            // 分权限显示用户数据
            if (authoritiesService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(a));
                count = selectConditionStep.fetchOne();
            } else if (authoritiesService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = authoritiesService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
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
    public Result<RoleRecord> findByRoleNameNotExistsCollegeRole(String roleName) {
        SelectConditionStep<CollegeRoleRecord> select = create.selectFrom(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.ROLE_ID.eq(ROLE.ROLE_ID));
        return create.selectFrom(ROLE)
                .where(ROLE.ROLE_NAME.eq(roleName)).andNotExists(select).fetch();
    }

    @Override
    public Result<RoleRecord> findByRoleNameNotExistsCollegeRoleNeRoleId(String roleName, int roleId) {
        SelectConditionStep<CollegeRoleRecord> select = create.selectFrom(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.ROLE_ID.eq(ROLE.ROLE_ID));
        return create.selectFrom(ROLE)
                .where(ROLE.ROLE_NAME.eq(roleName).and(ROLE.ROLE_ID.ne(roleId))).andNotExists(select).fetch();
    }

    /**
     * 获取系统默认角色
     *
     * @return
     */
    private List<String> getDefaultRoles() {
        List<String> defaultRoles = new ArrayList<>();
        defaultRoles.add(Workbook.SYSTEM_AUTHORITIES);
        return defaultRoles;
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<RoleBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String schoolName = StringUtils.trimWhitespace(search.getString("schoolName"));
            String collegeName = StringUtils.trimWhitespace(search.getString("collegeName"));
            String roleName = StringUtils.trimWhitespace(search.getString("roleName"));
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

            if (StringUtils.hasLength(roleName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = ROLE.ROLE_NAME.like(SQLQueryUtils.likeAllParam(roleName));
                } else {
                    a = a.and(ROLE.ROLE_NAME.like(SQLQueryUtils.likeAllParam(roleName)));
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
    public void sortCondition(DataTablesUtils<RoleBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = orderDir.equalsIgnoreCase("asc");
        SortField<Integer> a = null;
        SortField<String> b = null;
        SortField<Byte> c = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if (orderColumnName.equalsIgnoreCase("role_name")) {
                if (isAsc) {
                    b = ROLE.ROLE_NAME.asc();
                } else {
                    b = ROLE.ROLE_NAME.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("school_name")) {
                if (isAsc) {
                    b = SCHOOL.SCHOOL_NAME.asc();
                } else {
                    b = SCHOOL.SCHOOL_NAME.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("college_name")) {
                if (isAsc) {
                    b = COLLEGE.COLLEGE_NAME.asc();
                } else {
                    b = COLLEGE.COLLEGE_NAME.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("role_en_name")) {
                if (isAsc) {
                    b = ROLE.ROLE_EN_NAME.asc();
                } else {
                    b = ROLE.ROLE_EN_NAME.desc();
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
                selectConditionStep.orderBy(ROLE.ROLE_ID.desc());
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(ROLE.ROLE_ID.desc());
            }
        }
    }
}
