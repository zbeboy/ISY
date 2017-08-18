package top.zbeboy.isy.service.data;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Role;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;
import top.zbeboy.isy.elastic.config.ElasticBook;
import top.zbeboy.isy.elastic.pojo.OrganizeElastic;
import top.zbeboy.isy.elastic.pojo.StaffElastic;
import top.zbeboy.isy.elastic.pojo.StudentElastic;
import top.zbeboy.isy.elastic.pojo.UsersElastic;
import top.zbeboy.isy.elastic.repository.*;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.system.AuthoritiesService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zhaoyin on 17-8-8.
 */
@Slf4j
@Service("elasticSyncService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ElasticSyncServiceImpl implements ElasticSyncService {

    private final DSLContext create;

    @Autowired
    private OrganizeElasticRepository organizeElasticRepository;

    @Autowired
    private UsersElasticRepository usersElasticRepository;

    @Autowired
    private StudentElasticRepository studentElasticRepository;

    @Autowired
    private StaffElasticRepository staffElasticRepository;

    @Autowired
    private SystemLogElasticRepository systemLogElasticRepository;

    @Autowired
    private SystemMailboxElasticRepository systemMailboxElasticRepository;

    @Autowired
    private SystemSmsElasticRepository systemSmsElasticRepository;

    @Autowired
    private AuthoritiesService authoritiesService;

    @Autowired
    private RoleService roleService;

    @Autowired
    public ElasticSyncServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Async
    @Override
    public void cleanSystemLog() {
        systemLogElasticRepository.deleteAll();
    }

    @Async
    @Override
    public void cleanSystemMailbox() {
        systemMailboxElasticRepository.deleteAll();
    }

    @Async
    @Override
    public void cleanSystemSms() {
        systemSmsElasticRepository.deleteAll();
    }

    @Async
    @Override
    public void syncOrganizeData() {
        organizeElasticRepository.deleteAll();
        Result<Record> record = create.select()
                .from(ORGANIZE)
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .fetch();

        if (record.isNotEmpty()) {
            List<OrganizeElastic> organizeElastics = record.into(OrganizeElastic.class);
            organizeElasticRepository.save(organizeElastics);
        }
    }

    @Async
    @Override
    public void syncUsersData() {
        usersElasticRepository.deleteAll();
        Result<Record> record = create.select()
                .from(USERS)
                .join(USERS_TYPE)
                .on(USERS.USERS_TYPE_ID.eq(USERS_TYPE.USERS_TYPE_ID))
                .fetch();
        List<UsersElastic> usersElastics = new ArrayList<>();
        for (Record r : record) {
            UsersElastic usersElastic = r.into(UsersElastic.class);
            List<AuthoritiesRecord> authoritiesRecords = authoritiesService.findByUsername(r.get(USERS.USERNAME));
            Map<String, Object> map = buildAuthoritiesAndRoleNameData(authoritiesRecords);
            usersElastic.setAuthorities((Integer) map.get("authorities"));
            usersElastic.setRoleName((String) map.get("roleName"));
            usersElastics.add(usersElastic);
        }
        if (!ObjectUtils.isEmpty(usersElastics) && usersElastics.size() > 0) {
            usersElasticRepository.save(usersElastics);
        }
    }

    @Async
    @Override
    public void syncStudentData() {
        studentElasticRepository.deleteAll();
        Result<Record> record = create.select()
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
                .leftJoin(NATION)
                .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                .leftJoin(POLITICAL_LANDSCAPE)
                .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                .fetch();
        List<StudentElastic> studentElastics = new ArrayList<>();
        for (Record r : record) {
            StudentElastic studentElastic = r.into(StudentElastic.class);
            List<AuthoritiesRecord> authoritiesRecords = authoritiesService.findByUsername(r.get(USERS.USERNAME));
            Map<String, Object> map = buildAuthoritiesAndRoleNameData(authoritiesRecords);
            studentElastic.setAuthorities((Integer) map.get("authorities"));
            studentElastic.setRoleName((String) map.get("roleName"));
            studentElastics.add(studentElastic);
        }
        if (!ObjectUtils.isEmpty(studentElastics) && studentElastics.size() > 0) {
            studentElasticRepository.save(studentElastics);
        }
    }

    @Async
    @Override
    public void syncStaffData() {
        staffElasticRepository.deleteAll();
        Result<Record> record = create.select()
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
                .leftJoin(ACADEMIC_TITLE)
                .on(STAFF.ACADEMIC_TITLE_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                .fetch();
        List<StaffElastic> staffElastics = new ArrayList<>();
        for (Record r : record) {
            StaffElastic staffElastic = r.into(StaffElastic.class);
            List<AuthoritiesRecord> authoritiesRecords = authoritiesService.findByUsername(r.get(USERS.USERNAME));
            Map<String, Object> map = buildAuthoritiesAndRoleNameData(authoritiesRecords);
            staffElastic.setAuthorities((Integer) map.get("authorities"));
            staffElastic.setRoleName((String) map.get("roleName"));
            staffElastics.add(staffElastic);
        }
        if (!ObjectUtils.isEmpty(staffElastics) && staffElastics.size() > 0) {
            staffElasticRepository.save(staffElastics);
        }
    }

    @Async
    @Override
    public void collegeRoleNameUpdate(int collegeId, String roleName) {
        // step1 : 更新该院下学生
        List<StudentElastic> studentElastics = studentElasticRepository.findByCollegeIdAndRoleNameLike(collegeId, roleName);
        refreshStudent(studentElastics);

        // step2 : 更新用户表
        List<UsersElastic> usersElastics = new ArrayList<>();
        studentElastics.forEach(studentElastic -> {
            usersElastics.add(usersElasticRepository.findOne(studentElastic.getUsername()));
        });

        // step3 : 更新改院下教职工
        List<StaffElastic> staffElastics = staffElasticRepository.findByCollegeIdAndRoleNameLike(collegeId, roleName);
        refreshStaff(staffElastics);

        // step4 : 更新用户表
        staffElastics.forEach(staffElastic -> {
            usersElastics.add(usersElasticRepository.findOne(staffElastic.getUsername()));
        });

        refreshUsers(usersElastics);
    }

    @Async
    @Override
    public void systemRoleNameUpdate(String roleName) {
        List<StaffElastic> staffElastics = new ArrayList<>();
        List<StudentElastic> studentElastics = new ArrayList<>();
        List<UsersElastic> usersElastics = usersElasticRepository.findByRoleNameLike(roleName);
        for (UsersElastic usersElastic : usersElastics) {
            // 根据用户类型刷新角色名
            if (usersElastic.getUsersTypeName().equals(Workbook.STAFF_USERS_TYPE)) {
                staffElastics.add(staffElasticRepository.findByUsername(usersElastic.getUsername()));
            } else if (usersElastic.getUsersTypeName().equals(Workbook.STUDENT_USERS_TYPE)) {
                studentElastics.add(studentElasticRepository.findByUsername(usersElastic.getUsername()));
            }
        }
        refreshUsers(usersElastics);
        refreshStaff(staffElastics);
        refreshStudent(studentElastics);
    }

    private void refreshStudent(List<StudentElastic> studentElastics) {
        if (!ObjectUtils.isEmpty(studentElastics) && studentElastics.size() > 0) {
            studentElasticRepository.delete(studentElastics);
            for (StudentElastic r : studentElastics) {
                List<AuthoritiesRecord> authoritiesRecords = authoritiesService.findByUsername(r.getUsername());
                String roleName = buildRoleNameData(authoritiesRecords);
                r.setRoleName(roleName);
            }
            studentElasticRepository.save(studentElastics);
        }
    }

    private void refreshStaff(List<StaffElastic> staffElastics) {
        if (!ObjectUtils.isEmpty(staffElastics) && staffElastics.size() > 0) {
            staffElasticRepository.delete(staffElastics);
            for (StaffElastic r : staffElastics) {
                List<AuthoritiesRecord> authoritiesRecords = authoritiesService.findByUsername(r.getUsername());
                String roleName = buildRoleNameData(authoritiesRecords);
                r.setRoleName(roleName);
            }
            staffElasticRepository.save(staffElastics);
        }
    }

    private void refreshUsers(List<UsersElastic> usersElastics) {
        if (!ObjectUtils.isEmpty(usersElastics) && usersElastics.size() > 0) {
            usersElasticRepository.delete(usersElastics);
            for (UsersElastic r : usersElastics) {
                List<AuthoritiesRecord> authoritiesRecords = authoritiesService.findByUsername(r.getUsername());
                String roleName = buildRoleNameData(authoritiesRecords);
                r.setRoleName(roleName);
            }
            usersElasticRepository.save(usersElastics);
        }
    }

    /**
     * 构造权限与角色数据
     *
     * @param authoritiesRecords 权限数据
     * @return 数据
     */
    private Map<String, Object> buildAuthoritiesAndRoleNameData(List<AuthoritiesRecord> authoritiesRecords) {
        Map<String, Object> map = new HashMap<>();
        int authorities = ElasticBook.HAS_AUTHORITIES;
        if (!ObjectUtils.isEmpty(authoritiesRecords) && authoritiesRecords.size() > 0) {
            boolean hasUse = false;
            StringBuilder stringBuilder = new StringBuilder();
            for (AuthoritiesRecord a : authoritiesRecords) {
                if (a.getAuthority().equals(Workbook.SYSTEM_AUTHORITIES)) {
                    authorities = ElasticBook.SYSTEM_AUTHORITIES;
                    hasUse = true;
                }
                Role tempRole = roleService.findByRoleEnName(a.getAuthority());
                stringBuilder.append(tempRole.getRoleName()).append(" ");
            }

            if (!hasUse) {
                for (AuthoritiesRecord a : authoritiesRecords) {
                    if (a.getAuthority().equals(Workbook.ADMIN_AUTHORITIES)) {
                        authorities = ElasticBook.ADMIN_AUTHORITIES;
                        hasUse = true;
                        break;
                    }
                }
            }

            if (!hasUse) {
                for (AuthoritiesRecord a : authoritiesRecords) {
                    if (a.getAuthority().equals(Workbook.OPS_AUTHORITIES)) {
                        authorities = ElasticBook.OPS_AUTHORITIES;
                        break;
                    }
                }
            }
            map.put("roleName", stringBuilder.toString().trim());
        } else {
            authorities = ElasticBook.NO_AUTHORITIES;
            map.put("roleName", "");
        }
        map.put("authorities", authorities);
        return map;
    }

    /**
     * 构造角色名
     *
     * @param authoritiesRecords 权限数据
     * @return 角色名
     */
    private String buildRoleNameData(List<AuthoritiesRecord> authoritiesRecords) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!ObjectUtils.isEmpty(authoritiesRecords) && authoritiesRecords.size() > 0) {
            for (AuthoritiesRecord a : authoritiesRecords) {
                Role tempRole = roleService.findByRoleEnName(a.getAuthority());
                stringBuilder.append(tempRole.getRoleName()).append(" ");
            }
        }
        return stringBuilder.toString().trim();
    }
}
