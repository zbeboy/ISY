package top.zbeboy.isy.web.bean.data.student;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.Student;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by lenovo on 2016-09-27.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class StudentBean extends Student {
    private Integer schoolId;
    private String schoolName;
    private Byte schoolIsDel;

    private Integer collegeId;
    private String collegeName;
    private String collegeAddress;
    private Byte collegeIsDel;

    private Integer departmentId;
    private String departmentName;
    private Byte departmentIsDel;

    private Integer scienceId;
    private String scienceName;
    private Byte scienceIsDel;

    private String organizeName;
    private Byte organizeIsDel;
    private String grade;

    private String password;
    private Byte enabled;
    private Integer usersTypeId;
    private String realName;
    private String mobile;
    private String avatar;
    private Byte verifyMailbox;
    private String mailboxVerifyCode;
    private String passwordResetKey;
    private Timestamp mailboxVerifyValid;
    private Timestamp passwordResetKeyValid;
    private String langKey;
    private Date joinDate;

    private String nationName;

    private String politicalLandscapeName;

    private String roleName;

    // 楼号
    private String ridgepole;
    // 宿舍号
    private String dorm;
}
