package top.zbeboy.isy.service.data;

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
import top.zbeboy.isy.domain.tables.daos.StaffDao;
import top.zbeboy.isy.domain.tables.pojos.Staff;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;
import top.zbeboy.isy.domain.tables.records.StaffRecord;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-08-28.
 */
@Service("staffService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class StaffServiceImpl implements StaffService {

    private final Logger log = LoggerFactory.getLogger(StaffServiceImpl.class);

    private final DSLContext create;

    @Resource
    private StaffDao staffDao;

    @Resource
    private UsersService usersService;

    @Resource
    private RoleService roleService;

    @Autowired
    public StaffServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Optional<Record> findByIdRelation(int id) {
        return create.select()
                .from(STAFF)
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .join(DEPARTMENT)
                .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .where(STAFF.STAFF_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Optional<Record> findByIdRelationForUsers(int id) {
        return create.select()
                .from(STAFF)
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(STAFF.STAFF_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Staff findByStaffNumber(String staffNumber) {
        return staffDao.fetchOneByStaffNumber(staffNumber);
    }

    @Override
    public Result<Record> findByDepartmentIdRelationExistsAuthorities(int departmentId) {
        Select<AuthoritiesRecord> authoritiesRecordSelect =
                create.selectFrom(AUTHORITIES)
                        .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME));
        return create.select()
                .from(STAFF)
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .where(STAFF.DEPARTMENT_ID.eq(departmentId)).andExists(authoritiesRecordSelect)
                .fetch();

    }

    @Override
    public Staff findByUsername(String username) {
        return staffDao.fetchOne(STAFF.USERNAME, username);
    }

    @Override
    public Result<StaffRecord> findByStaffNumberNeUsername(String username, String staffNumber) {
        return create.selectFrom(STAFF)
                .where(STAFF.STAFF_NUMBER.eq(staffNumber).and(STAFF.USERNAME.ne(username)))
                .fetch();
    }

    @Override
    public Result<StaffRecord> findByIdCardNeUsername(String username, String idCard) {
        return create.selectFrom(STAFF)
                .where(STAFF.ID_CARD.eq(idCard).and(STAFF.USERNAME.ne(username))).fetch();
    }

    @Override
    public List<Staff> findByIdCard(String idCard) {
        return staffDao.fetchByIdCard(idCard);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(Staff staff) {
        staffDao.insert(staff);
    }

    @Override
    public void update(Staff staff) {
        staffDao.update(staff);
    }

    @Override
    public Optional<Record> findByUsernameRelation(String username) {
        return create.select()
                .from(STAFF)
                .join(DEPARTMENT)
                .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .join(USERS)
                .on(STAFF.USERNAME.eq(USERS.USERNAME))
                .leftJoin(NATION)
                .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                .leftJoin(POLITICAL_LANDSCAPE)
                .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                .where(STAFF.USERNAME.eq(username))
                .fetchOptional();
    }

    @Override
    public void deleteByUsername(String username) {
        create.deleteFrom(STAFF).where(STAFF.USERNAME.eq(username)).execute();
    }

    @Override
    public Result<Record> findAllByPageExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils) {
        Select<AuthoritiesRecord> select = usersService.existsAuthoritiesSelect();
        Result<Record> records = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .whereExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            }

        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a)).andExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            }

        }
        return records;
    }

    @Override
    public Result<Record> findAllByPageNotExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils) {
        Select<AuthoritiesRecord> select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME));
        Result<Record> records = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .whereNotExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andNotExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            }
        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andNotExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a)).andNotExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            }

        }
        return records;
    }

    @Override
    public int countAllExistsAuthorities() {
        Select<AuthoritiesRecord> select = usersService.existsAuthoritiesSelect();
        // 分权限显示用户数据
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            Record1<Integer> count = create.selectCount()
                    .from(STAFF)
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .whereExists(select)
                    .fetchOne();
            return count.value1();
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            Record1<Integer> count = create.selectCount()
                    .from(STAFF)
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .join(DEPARTMENT)
                    .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andExists(select)
                    .fetchOne();
            return count.value1();
        }
        return 0;
    }

    @Override
    public int countAllNotExistsAuthorities() {
        Select<AuthoritiesRecord> select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME));
        // 分权限显示用户数据
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            Record1<Integer> count = create.selectCount()
                    .from(STAFF)
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .whereNotExists(select)
                    .fetchOne();
            return count.value1();
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            Record1<Integer> count = create.selectCount()
                    .from(STAFF)
                    .join(USERS)
                    .on(STAFF.USERNAME.eq(USERS.USERNAME))
                    .join(DEPARTMENT)
                    .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andNotExists(select)
                    .fetchOne();
            return count.value1();
        }
        return 0;
    }

    @Override
    public int countByConditionExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils) {
        Select<AuthoritiesRecord> select = usersService.existsAuthoritiesSelect();
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .whereExists(select);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andExists(select);
                count = selectConditionStep.fetchOne();
            }
        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andExists(select);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a)).andExists(select);
                count = selectConditionStep.fetchOne();
            }

        }
        if (!ObjectUtils.isEmpty(count)) {
            return count.value1();
        }
        return 0;
    }

    @Override
    public int countByConditionNotExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils) {
        Select<AuthoritiesRecord> select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME));
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .whereNotExists(select);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andNotExists(select);
                count = selectConditionStep.fetchOne();
            }
        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andNotExists(select);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STAFF)
                        .join(DEPARTMENT)
                        .on(STAFF.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STAFF.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STAFF.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STAFF.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a)).andNotExists(select);
                count = selectConditionStep.fetchOne();
            }
        }
        if (!ObjectUtils.isEmpty(count)) {
            return count.value1();
        }
        return 0;
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    public Condition searchCondition(DataTablesUtils<StaffBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String school = StringUtils.trimWhitespace(search.getString("school"));
            String college = StringUtils.trimWhitespace(search.getString("college"));
            String department = StringUtils.trimWhitespace(search.getString("department"));
            String post = StringUtils.trimWhitespace(search.getString("post"));
            String staffNumber = StringUtils.trimWhitespace(search.getString("staffNumber"));
            String username = StringUtils.trimWhitespace(search.getString("username"));
            String mobile = StringUtils.trimWhitespace(search.getString("mobile"));
            String idCard = StringUtils.trimWhitespace(search.getString("idCard"));
            String realName = StringUtils.trimWhitespace(search.getString("realName"));
            String sex = StringUtils.trimWhitespace(search.getString("sex"));
            if (StringUtils.hasLength(school)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(school));
            }

            if (StringUtils.hasLength(college)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(college));
                } else {
                    a = a.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(college)));
                }
            }

            if (StringUtils.hasLength(department)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(department));
                } else {
                    a = a.and(DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(department)));
                }
            }

            if (StringUtils.hasLength(post)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.POST.like(SQLQueryUtils.likeAllParam(post));
                } else {
                    a = a.and(STAFF.POST.like(SQLQueryUtils.likeAllParam(post)));
                }
            }

            if (StringUtils.hasLength(staffNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber));
                } else {
                    a = a.and(STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber)));
                }
            }

            if (StringUtils.hasLength(username)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.USERNAME.like(SQLQueryUtils.likeAllParam(username));
                } else {
                    a = a.and(STAFF.USERNAME.like(SQLQueryUtils.likeAllParam(username)));
                }
            }

            if (StringUtils.hasLength(mobile)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = USERS.MOBILE.like(SQLQueryUtils.likeAllParam(mobile));
                } else {
                    a = a.and(USERS.MOBILE.like(SQLQueryUtils.likeAllParam(mobile)));
                }
            }

            if (StringUtils.hasLength(idCard)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.ID_CARD.like(SQLQueryUtils.likeAllParam(idCard));
                } else {
                    a = a.and(STAFF.ID_CARD.like(SQLQueryUtils.likeAllParam(idCard)));
                }
            }

            if (StringUtils.hasLength(realName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(realName));
                } else {
                    a = a.and(USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(realName)));
                }
            }

            if (StringUtils.hasLength(sex)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.SEX.like(SQLQueryUtils.likeAllParam(sex));
                } else {
                    a = a.and(STAFF.SEX.like(SQLQueryUtils.likeAllParam(sex)));
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
    public void sortCondition(DataTablesUtils<StaffBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("staff_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = STAFF.STAFF_NUMBER.asc();
                } else {
                    sortField[0] = STAFF.STAFF_NUMBER.desc();
                }
            }

            if ("real_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = USERS.REAL_NAME.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("username".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = USERS.USERNAME.desc();
                }
            }

            if ("mobile".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = USERS.MOBILE.asc();
                } else {
                    sortField[0] = USERS.MOBILE.desc();
                }
            }

            if ("id_card".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = STAFF.ID_CARD.asc();
                } else {
                    sortField[0] = STAFF.ID_CARD.desc();
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("college_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("department_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("post".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STAFF.POST.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STAFF.POST.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("sex".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STAFF.SEX.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STAFF.SEX.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("birthday".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STAFF.BIRTHDAY.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STAFF.BIRTHDAY.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("nation_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = NATION.NATION_NAME.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = NATION.NATION_NAME.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("politicalLandscape_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }


            if ("family_residence".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STAFF.FAMILY_RESIDENCE.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STAFF.FAMILY_RESIDENCE.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("enabled".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.ENABLED.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = USERS.ENABLED.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("lang_key".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.LANG_KEY.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = USERS.LANG_KEY.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("join_date".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.JOIN_DATE.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = USERS.JOIN_DATE.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

        }
        if (!ObjectUtils.isEmpty(sortField)) {
            selectConditionStep.orderBy(sortField);
        }
    }

    /**
     * 分页方式
     *
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    public void pagination(DataTablesUtils<StaffBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        int start = dataTablesUtils.getStart();
        int length = dataTablesUtils.getLength();
        selectConditionStep.limit(start, length);
    }
}
