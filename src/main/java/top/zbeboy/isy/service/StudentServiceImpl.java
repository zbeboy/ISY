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
import top.zbeboy.isy.domain.tables.daos.StudentDao;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;
import top.zbeboy.isy.domain.tables.records.StudentRecord;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-08-22.
 */
@Service("studentService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final DSLContext create;

    @Resource
    private StudentDao studentDao;

    @Resource
    private UsersService usersService;

    @Resource
    private RoleService roleService;

    @Autowired
    public StudentServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Student findById(int id) {
        return studentDao.findById(id);
    }

    @Override
    public Optional<Record> findByIdRelation(int id) {
        return create.select()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .where(STUDENT.STUDENT_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Optional<Record> findByIdRelationForUsers(int id) {
        return create.select()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(STUDENT.STUDENT_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Student findByStudentNumber(String studentNumber) {
        return studentDao.fetchOneByStudentNumber(studentNumber);
    }

    @Override
    public Result<StudentRecord> findInOrganizeIds(List<Integer> organizeIds) {
        return create.selectFrom(STUDENT)
                .where(STUDENT.ORGANIZE_ID.in(organizeIds))
                .fetch();
    }

    @Override
    public Optional<Record> findByUsernameAndDepartmentId(String username, int departmentId) {
        return create.select()
                .from(STUDENT)
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .where(STUDENT.USERNAME.eq(username).and(DEPARTMENT.DEPARTMENT_ID.eq(departmentId)))
                .fetchOptional();
    }

    @Override
    public Optional<Record> findByStudentNumberAndDepartmentId(String studentNumber, int departmentId) {
        return create.select()
                .from(STUDENT)
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .where(STUDENT.STUDENT_NUMBER.eq(studentNumber).and(DEPARTMENT.DEPARTMENT_ID.eq(departmentId)))
                .fetchOptional();
    }

    @Override
    public Result<StudentRecord> findByStudentNumberNeUsername(String username, String studentNumber) {
        return create.selectFrom(STUDENT)
                .where(STUDENT.STUDENT_NUMBER.eq(studentNumber).and(STUDENT.USERNAME.ne(username))).fetch();
    }

    @Override
    public Result<StudentRecord> findByIdCardNeUsername(String username, String idCard) {
        return create.selectFrom(STUDENT)
                .where(STUDENT.ID_CARD.eq(idCard).and(STUDENT.USERNAME.ne(username))).fetch();
    }

    @Override
    public List<Student> findByIdCard(String idCard) {
        return studentDao.fetchByIdCard(idCard);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(Student student) {
        studentDao.insert(student);
    }

    @Override
    public void update(Student student) {
        studentDao.update(student);
    }

    @Override
    public Optional<Record> findByUsernameRelation(String username) {
        return create.select()
                .from(STUDENT)
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .leftJoin(NATION)
                .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                .leftJoin(POLITICAL_LANDSCAPE)
                .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                .where(STUDENT.USERNAME.eq(username))
                .fetchOptional();
    }

    @Override
    public Student findByUsername(String username) {
        return studentDao.fetchOne(STUDENT.USERNAME, username);
    }

    @Override
    public void deleteByUsername(String username) {
        create.deleteFrom(STUDENT).where(STUDENT.USERNAME.eq(username)).execute();
    }

    @Override
    public Result<Record> findAllByPageExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils) {
        Select<AuthoritiesRecord> select = usersService.existsAuthoritiesSelect();
        Result<Record> records = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .whereExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            }

        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a)).andExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            }
        }
        return records;
    }

    @Override
    public Result<Record> findAllByPageNotExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils) {
        Select<AuthoritiesRecord> select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME));
        Result<Record> records = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .whereNotExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andNotExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            }
        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andNotExists(select);
                sortCondition(dataTablesUtils, selectConditionStep);
                pagination(dataTablesUtils, selectConditionStep);
                records = selectConditionStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
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
                    .from(STUDENT)
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .whereExists(select)
                    .fetchOne();
            return count.value1();
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            Record1<Integer> count = create.selectCount()
                    .from(STUDENT)
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
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
                    .from(STUDENT)
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .whereNotExists(select)
                    .fetchOne();
            return count.value1();
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            Record1<Integer> count = create.selectCount()
                    .from(STUDENT)
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andNotExists(select)
                    .fetchOne();
            return count.value1();
        }
        return 0;
    }

    @Override
    public int countByConditionExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils) {
        Select<AuthoritiesRecord> select = usersService.existsAuthoritiesSelect();
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .whereExists(select);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andExists(select);
                count = selectConditionStep.fetchOne();
            }
        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andExists(select);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
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
    public int countByConditionNotExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils) {
        Select<AuthoritiesRecord> select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME));
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .whereNotExists(select);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId)).andNotExists(select);
                count = selectConditionStep.fetchOne();
            }
        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andNotExists(select);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
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
    public Condition searchCondition(DataTablesUtils<StudentBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String school = StringUtils.trimWhitespace(search.getString("school"));
            String college = StringUtils.trimWhitespace(search.getString("college"));
            String department = StringUtils.trimWhitespace(search.getString("department"));
            String science = StringUtils.trimWhitespace(search.getString("science"));
            String grade = StringUtils.trimWhitespace(search.getString("grade"));
            String organize = StringUtils.trimWhitespace(search.getString("organize"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
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

            if (StringUtils.hasLength(science)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = SCIENCE.SCIENCE_NAME.like(SQLQueryUtils.likeAllParam(science));
                } else {
                    a = a.and(SCIENCE.SCIENCE_NAME.like(SQLQueryUtils.likeAllParam(science)));
                }
            }

            if (StringUtils.hasLength(grade)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = ORGANIZE.GRADE.like(SQLQueryUtils.likeAllParam(grade));
                } else {
                    a = a.and(ORGANIZE.GRADE.like(SQLQueryUtils.likeAllParam(grade)));
                }
            }

            if (StringUtils.hasLength(organize)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organize));
                } else {
                    a = a.and(ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organize)));
                }
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                }
            }

            if (StringUtils.hasLength(username)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STUDENT.USERNAME.like(SQLQueryUtils.likeAllParam(username));
                } else {
                    a = a.and(STUDENT.USERNAME.like(SQLQueryUtils.likeAllParam(username)));
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
                    a = STUDENT.ID_CARD.like(SQLQueryUtils.likeAllParam(idCard));
                } else {
                    a = a.and(STUDENT.ID_CARD.like(SQLQueryUtils.likeAllParam(idCard)));
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
                    a = STUDENT.SEX.like(SQLQueryUtils.likeAllParam(sex));
                } else {
                    a = a.and(STUDENT.SEX.like(SQLQueryUtils.likeAllParam(sex)));
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
    public void sortCondition(DataTablesUtils<StudentBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = STUDENT.STUDENT_NUMBER.asc();
                } else {
                    sortField = STUDENT.STUDENT_NUMBER.desc();
                }
            }

            if ("real_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = USERS.REAL_NAME.asc();
                } else {
                    sortField = USERS.REAL_NAME.desc();
                }
            }

            if ("username".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = USERS.USERNAME.asc();
                } else {
                    sortField = USERS.USERNAME.desc();
                }
            }

            if ("mobile".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = USERS.MOBILE.asc();
                } else {
                    sortField = USERS.MOBILE.desc();
                }
            }

            if ("id_card".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = STUDENT.ID_CARD.asc();
                } else {
                    sortField = STUDENT.ID_CARD.desc();
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

            if ("grade".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = ORGANIZE.GRADE.asc();
                } else {
                    sortField = ORGANIZE.GRADE.desc();
                }
            }

            if ("organize_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = ORGANIZE.ORGANIZE_NAME.asc();
                } else {
                    sortField = ORGANIZE.ORGANIZE_NAME.desc();
                }
            }

            if ("sex".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = STUDENT.SEX.asc();
                } else {
                    sortField = STUDENT.SEX.desc();
                }
            }

            if ("birthday".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = STUDENT.BIRTHDAY.asc();
                } else {
                    sortField = STUDENT.BIRTHDAY.desc();
                }
            }

            if ("nation_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = NATION.NATION_NAME.asc();
                } else {
                    sortField = NATION.NATION_NAME.desc();
                }
            }

            if ("politicalLandscape_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.asc();
                } else {
                    sortField = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.desc();
                }
            }

            if ("dormitory_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = STUDENT.DORMITORY_NUMBER.asc();
                } else {
                    sortField = STUDENT.DORMITORY_NUMBER.desc();
                }
            }

            if ("place_origin".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = STUDENT.PLACE_ORIGIN.asc();
                } else {
                    sortField = STUDENT.PLACE_ORIGIN.desc();
                }
            }

            if ("parent_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = STUDENT.PARENT_NAME.asc();
                } else {
                    sortField = STUDENT.PARENT_NAME.desc();
                }
            }

            if ("parent_contact_phone".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = STUDENT.PARENT_CONTACT_PHONE.asc();
                } else {
                    sortField = STUDENT.PARENT_CONTACT_PHONE.desc();
                }
            }

            if ("family_residence".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = STUDENT.FAMILY_RESIDENCE.asc();
                } else {
                    sortField = STUDENT.FAMILY_RESIDENCE.desc();
                }
            }

            if ("enabled".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = USERS.ENABLED.asc();
                } else {
                    sortField = USERS.ENABLED.desc();
                }
            }

            if ("lang_key".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = USERS.LANG_KEY.asc();
                } else {
                    sortField = USERS.LANG_KEY.desc();
                }
            }

            if ("join_date".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = USERS.JOIN_DATE.asc();
                } else {
                    sortField = USERS.JOIN_DATE.desc();
                }
            }

        }
        if (!ObjectUtils.isEmpty(sortField)) {
            selectConditionStep.orderBy(sortField);
        } else {
            selectConditionStep.orderBy(USERS.USERNAME);
        }
    }

    /**
     * 分页方式
     *
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    public void pagination(DataTablesUtils<StudentBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        int start = dataTablesUtils.getStart();
        int length = dataTablesUtils.getLength();
        selectConditionStep.limit(start, length);
    }
}
