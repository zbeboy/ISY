package top.zbeboy.isy.service.platform;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.daos.RoleDao;
import top.zbeboy.isy.domain.tables.pojos.*;
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

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-10-04.
 */
@Slf4j
@Service("roleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RoleServiceImpl extends DataTablesPlugin<RoleBean> implements RoleService {

    private final DSLContext create;

    @Resource
    private RoleDao roleDao;

    @Resource
    private UsersService usersService;

    @Autowired
    public RoleServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Role findByRoleEnName(String roleEnName) {
        return roleDao.fetchOne(ROLE.ROLE_EN_NAME, roleEnName);
    }

    @Override
    public Result<Record> findByRoleNameAndRoleType(String roleName, int roleType) {
        return create.select()
                .from(ROLE)
                .where(ROLE.ROLE_NAME.eq(roleName).and(ROLE.ROLE_TYPE.eq(roleType)))
                .fetch();
    }

    @Override
    public Result<Record> findByRoleNameAndRoleTypeNeRoleId(String roleName, int roleType, String roleId) {
        return create.select()
                .from(ROLE)
                .where(ROLE.ROLE_NAME.eq(roleName).and(ROLE.ROLE_TYPE.eq(roleType)).and(ROLE.ROLE_ID.ne(roleId)))
                .fetch();
    }

    @Override
    public Role findById(String id) {
        return roleDao.findById(id);
    }

    @Override
    public Optional<Record> findByRoleIdRelation(String roleId) {
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
    public Result<Record> findByRoleNameAndCollegeIdNeRoleId(String roleName, int collegeId, String roleId) {
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

    @Override
    public void update(Role role) {
        roleDao.update(role);
    }

    @Override
    public void deleteById(String id) {
        roleDao.deleteById(id);
    }

    @Override
    public Result<RoleRecord> findInRoleId(List<String> ids) {
        return create.selectFrom(ROLE)
                .where(ROLE.ROLE_ID.in(ids))
                .fetch();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<RoleBean> dataTablesUtils, RoleBean roleBean) {
        Result<Record> records = null;
        List<String> defaultRoles = getDefaultRoles();
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(ROLE.ROLE_TYPE.eq(roleBean.getRoleType())));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            } else if (isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(ROLE.ROLE_TYPE.eq(roleBean.getRoleType())));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            }
        } else {
            // 分权限显示用户数据
            if (isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(a).and(ROLE.ROLE_TYPE.eq(roleBean.getRoleType())));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            } else if (isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a).and(ROLE.ROLE_TYPE.eq(roleBean.getRoleType())));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            }
        }
        return records;
    }

    @Override
    public int countAll(RoleBean roleBean) {
        // 分权限显示用户数据
        if (isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            List<String> defaultRoles = getDefaultRoles();
            Record1<Integer> count = create.selectCount()
                    .from(ROLE)
                    .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(ROLE.ROLE_TYPE.eq(roleBean.getRoleType())))
                    .fetchOne();
            return count.value1();
        } else if (isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = getRoleCollegeId(record);
            Record1<Integer> count = create.selectCount()
                    .from(ROLE)
                    .leftJoin(COLLEGE_ROLE)
                    .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                    .where(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId).and(ROLE.ROLE_TYPE.eq(roleBean.getRoleType())))
                    .fetchOne();
            return count.value1();
        }
        return 0;
    }

    @Override
    public int countByCondition(DataTablesUtils<RoleBean> dataTablesUtils, RoleBean roleBean) {
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        List<String> defaultRoles = getDefaultRoles();
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(ROLE.ROLE_TYPE.eq(roleBean.getRoleType())));
                count = selectConditionStep.fetchOne();
            } else if (isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .where(COLLEGE_ROLE.COLLEGE_ID.eq(collegeId).and(ROLE.ROLE_TYPE.eq(roleBean.getRoleType())));
                count = selectConditionStep.fetchOne();
            }
        } else {
            // 分权限显示用户数据
            if (isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(ROLE.ROLE_EN_NAME.notIn(defaultRoles).and(a).and(ROLE.ROLE_TYPE.eq(roleBean.getRoleType())));
                count = selectConditionStep.fetchOne();
            } else if (isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ROLE)
                        .leftJoin(COLLEGE_ROLE)
                        .on(ROLE.ROLE_ID.eq(COLLEGE_ROLE.ROLE_ID))
                        .leftJoin(COLLEGE)
                        .on(COLLEGE_ROLE.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .leftJoin(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a).and(ROLE.ROLE_TYPE.eq(roleBean.getRoleType())));
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
    public Result<RoleRecord> findByRoleNameNotExistsCollegeRoleNeRoleId(String roleName, String roleId) {
        SelectConditionStep<CollegeRoleRecord> select = create.selectFrom(COLLEGE_ROLE)
                .where(COLLEGE_ROLE.ROLE_ID.eq(ROLE.ROLE_ID));
        return create.selectFrom(ROLE)
                .where(ROLE.ROLE_NAME.eq(roleName).and(ROLE.ROLE_ID.ne(roleId))).andNotExists(select).fetch();
    }

    @Override
    public String findByUsernameToStringNoCache(String username) {
        Result<Record1<String>> record1s = usersService.findByUsernameWithRole(username);
        StringBuilder stringBuilder = new StringBuilder();
        record1s.forEach(r -> stringBuilder.append(r.getValue(0)).append(" "));
        return stringBuilder.toString();
    }

    @Override
    public boolean isCurrentUserInRole(String role) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                return springSecurityUser.getAuthorities().contains(new SimpleGrantedAuthority(role));
            }
        }
        return false;
    }

    @Override
    public int getRoleCollegeId(Optional<Record> record) {
        int collegeId = 0;
        if (record.isPresent()) {
            College college = record.get().into(College.class);
            if (!ObjectUtils.isEmpty(college)) {
                collegeId = college.getCollegeId();
            }
        }
        return collegeId;
    }

    @Override
    public int getRoleDepartmentId(Optional<Record> record) {
        int departmentId = 0;
        if (record.isPresent()) {
            Department department = record.get().into(Department.class);
            if (!ObjectUtils.isEmpty(department)) {
                departmentId = department.getDepartmentId();
            }
        }
        return departmentId;
    }

    @Override
    public int getRoleSchoolId(Optional<Record> record) {
        int schoolId = 0;
        if (record.isPresent()) {
            School school = record.get().into(School.class);
            if (!ObjectUtils.isEmpty(school)) {
                schoolId = school.getSchoolId();
            }
        }
        return schoolId;
    }

    /**
     * 获取系统默认角色
     *
     * @return 默认角色
     */
    private List<String> getDefaultRoles() {
        List<String> defaultRoles = new ArrayList<>();
        defaultRoles.add(Workbook.SYSTEM_AUTHORITIES);
        return defaultRoles;
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
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
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<RoleBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("role_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ROLE.ROLE_NAME.asc();
                    sortField[1] = ROLE.ROLE_ID.asc();
                } else {
                    sortField[0] = ROLE.ROLE_NAME.desc();
                    sortField[1] = ROLE.ROLE_ID.desc();
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc();
                    sortField[1] = ROLE.ROLE_ID.asc();
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc();
                    sortField[1] = ROLE.ROLE_ID.desc();
                }
            }

            if ("college_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc();
                    sortField[1] = ROLE.ROLE_ID.asc();
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc();
                    sortField[1] = ROLE.ROLE_ID.desc();
                }
            }

            if ("role_en_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ROLE.ROLE_EN_NAME.asc();
                    sortField[1] = ROLE.ROLE_ID.asc();
                } else {
                    sortField[0] = ROLE.ROLE_EN_NAME.desc();
                    sortField[1] = ROLE.ROLE_ID.desc();
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
