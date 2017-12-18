package top.zbeboy.isy.service.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;

import javax.annotation.Resource;

/**
 * Created by lenovo on 2016-10-15.
 */
@Slf4j
@Service("commonControllerMethodService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CommonControllerMethodServiceImpl implements CommonControllerMethodService {

    @Resource
    private UsersService usersService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private StudentService studentService;

    @Resource
    private RoleService roleService;

    @Override
    public boolean limitCurrentStudent(int studentId) {
        // 强制身份判断
        if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES) && !roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                Users users = usersService.getUserFromSession();
                Student student = studentService.findByUsername(users.getUsername());
                return ObjectUtils.isEmpty(student) || student.getStudentId() == studentId;
            }
        }
        return true;
    }
}
