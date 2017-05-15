package top.zbeboy.isy.service.data;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.daos.StudentDao;
import top.zbeboy.isy.domain.tables.pojos.Nation;
import top.zbeboy.isy.domain.tables.pojos.PoliticalLandscape;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;
import top.zbeboy.isy.domain.tables.records.StudentRecord;
import top.zbeboy.isy.elastic.pojo.StudentElastic;
import top.zbeboy.isy.elastic.repository.StudentElasticRepository;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-08-22.
 */
@Slf4j
@Service("studentService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final DSLContext create;

    @Resource
    private StudentDao studentDao;

    @Resource
    private UsersService usersService;

    @Resource
    private RoleService roleService;

    @Resource
    private StudentElasticRepository studentElasticRepository;

    @Resource
    private OrganizeService organizeService;

    @Resource
    private PoliticalLandscapeService politicalLandscapeService;

    @Resource
    private NationService nationService;

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
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
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
    public Result<Record> findInOrganizeIdsAndEnabledExistsAuthorities(List<Integer> organizeIds, Byte b) {
        Select<AuthoritiesRecord> authoritiesRecordSelect =
                create.selectFrom(AUTHORITIES)
                        .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME));
        return create.select()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(STUDENT.ORGANIZE_ID.in(organizeIds).and(USERS.ENABLED.eq(b)).andExists(authoritiesRecordSelect))
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
    public void save(StudentElastic studentElastic) {
        StudentRecord studentRecord = create.insertInto(STUDENT)
                .set(STUDENT.STUDENT_NUMBER, studentElastic.getStudentNumber())
                .set(STUDENT.BIRTHDAY, studentElastic.getBirthday())
                .set(STUDENT.SEX, studentElastic.getSex())
                .set(STUDENT.ID_CARD, studentElastic.getIdCard())
                .set(STUDENT.FAMILY_RESIDENCE, studentElastic.getFamilyResidence())
                .set(STUDENT.POLITICAL_LANDSCAPE_ID, studentElastic.getPoliticalLandscapeId())
                .set(STUDENT.NATION_ID, studentElastic.getNationId())
                .set(STUDENT.DORMITORY_NUMBER, studentElastic.getDormitoryNumber())
                .set(STUDENT.PARENT_NAME, studentElastic.getParentName())
                .set(STUDENT.PARENT_CONTACT_PHONE, studentElastic.getParentContactPhone())
                .set(STUDENT.PLACE_ORIGIN, studentElastic.getPlaceOrigin())
                .set(STUDENT.ORGANIZE_ID, studentElastic.getOrganizeId())
                .set(STUDENT.USERNAME, studentElastic.getUsername())
                .returning(STUDENT.STUDENT_ID)
                .fetchOne();
        studentElastic.setAuthorities(-1);
        studentElastic.setStudentId(studentRecord.getStudentId());
        studentElasticRepository.save(studentElastic);
    }

    @Override
    public void update(Student student) {
        studentDao.update(student);
        StudentElastic studentElastic = studentElasticRepository.findOne(student.getStudentId() + "");
        studentElastic.setStudentNumber(student.getStudentNumber());
        studentElastic.setBirthday(student.getBirthday());
        studentElastic.setSex(student.getSex());
        studentElastic.setIdCard(student.getIdCard());
        studentElastic.setFamilyResidence(student.getFamilyResidence());
        studentElastic.setDormitoryNumber(student.getDormitoryNumber());
        studentElastic.setParentName(student.getParentName());
        studentElastic.setParentContactPhone(student.getParentContactPhone());
        studentElastic.setPlaceOrigin(student.getPlaceOrigin());
        if (!Objects.equals(student.getPoliticalLandscapeId(), studentElastic.getPoliticalLandscapeId())) {
            if (!Objects.isNull(student.getPoliticalLandscapeId()) && student.getPoliticalLandscapeId() > 0) {
                PoliticalLandscape politicalLandscape = politicalLandscapeService.findById(student.getPoliticalLandscapeId());
                if (!Objects.isNull(politicalLandscape)) {
                    studentElastic.setPoliticalLandscapeId(politicalLandscape.getPoliticalLandscapeId());
                    studentElastic.setPoliticalLandscapeName(politicalLandscape.getPoliticalLandscapeName());
                }
            } else {
                studentElastic.setPoliticalLandscapeId(student.getPoliticalLandscapeId());
                studentElastic.setPoliticalLandscapeName("");
            }
        }
        if (!Objects.equals(student.getNationId(), studentElastic.getNationId())) {
            if (!Objects.isNull(student.getNationId()) && student.getNationId() > 0) {
                Nation nation = nationService.findById(student.getNationId());
                if (!Objects.isNull(nation)) {
                    studentElastic.setNationId(nation.getNationId());
                    studentElastic.setNationName(nation.getNationName());
                }
            } else {
                studentElastic.setNationId(student.getNationId());
                studentElastic.setNationName("");
            }

        }
        if (!Objects.isNull(student.getOrganizeId()) && student.getOrganizeId() > 0 && !Objects.equals(student.getOrganizeId(), studentElastic.getOrganizeId())) {
            Optional<Record> record = organizeService.findByIdRelation(student.getOrganizeId());
            if (record.isPresent()) {
                OrganizeBean organizeBean = record.get().into(OrganizeBean.class);
                studentElastic.setSchoolId(organizeBean.getSchoolId());
                studentElastic.setSchoolName(organizeBean.getSchoolName());
                studentElastic.setCollegeId(organizeBean.getCollegeId());
                studentElastic.setCollegeName(organizeBean.getCollegeName());
                studentElastic.setDepartmentId(organizeBean.getDepartmentId());
                studentElastic.setDepartmentName(organizeBean.getDepartmentName());
                studentElastic.setScienceId(organizeBean.getScienceId());
                studentElastic.setScienceName(organizeBean.getScienceName());
                studentElastic.setOrganizeId(organizeBean.getOrganizeId());
                studentElastic.setOrganizeName(organizeBean.getOrganizeName());
                studentElastic.setGrade(organizeBean.getGrade());
            }
        }
        studentElasticRepository.delete(studentElastic);
        studentElasticRepository.save(studentElastic);
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
        studentElasticRepository.deleteByUsername(username);
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

    @Override
    public int countByOrganizeIdAndEnabledExistsAuthorities(int organizeId, Byte b) {
        Select<AuthoritiesRecord> authoritiesRecordSelect =
                create.selectFrom(AUTHORITIES)
                        .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME));
        Record1<Integer> count = create.selectCount()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(USERS.ENABLED.eq(b).and(STUDENT.ORGANIZE_ID.eq(organizeId)).andExists(authoritiesRecordSelect))
                .fetchOne();
        return count.value1();
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
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = STUDENT.STUDENT_NUMBER.asc();
                } else {
                    sortField[0] = STUDENT.STUDENT_NUMBER.desc();
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
                    sortField[0] = STUDENT.ID_CARD.asc();
                } else {
                    sortField[0] = STUDENT.ID_CARD.desc();
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

            if ("science_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_NAME.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = SCIENCE.SCIENCE_NAME.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("grade".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ORGANIZE.GRADE.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = ORGANIZE.GRADE.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("organize_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("sex".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STUDENT.SEX.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STUDENT.SEX.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("birthday".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STUDENT.BIRTHDAY.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STUDENT.BIRTHDAY.desc();
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

            if ("dormitory_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STUDENT.DORMITORY_NUMBER.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STUDENT.DORMITORY_NUMBER.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("place_origin".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STUDENT.PLACE_ORIGIN.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STUDENT.PLACE_ORIGIN.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("parent_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STUDENT.PARENT_NAME.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STUDENT.PARENT_NAME.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("parent_contact_phone".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STUDENT.PARENT_CONTACT_PHONE.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STUDENT.PARENT_CONTACT_PHONE.desc();
                    sortField[1] = USERS.USERNAME.desc();
                }
            }

            if ("family_residence".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = STUDENT.FAMILY_RESIDENCE.asc();
                    sortField[1] = USERS.USERNAME.asc();
                } else {
                    sortField[0] = STUDENT.FAMILY_RESIDENCE.desc();
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
    public void pagination(DataTablesUtils<StudentBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep) {
        int start = dataTablesUtils.getStart();
        int length = dataTablesUtils.getLength();
        selectConditionStep.limit(start, length);
    }
}
