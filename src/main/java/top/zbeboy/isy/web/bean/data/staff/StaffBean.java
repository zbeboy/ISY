package top.zbeboy.isy.web.bean.data.staff;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.Staff;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by lenovo on 2016-09-27.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class StaffBean extends Staff{
    private Integer schoolId;
    private String schoolName;
    private Byte schoolIsDel;

    private Integer collegeId;
    private String collegeName;
    private Byte collegeIsDel;

    private String departmentName;
    private Byte departmentIsDel;

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

    private String  nationName;

    private String  politicalLandscapeName;

    private String roleName;

    private String academicTitleName;

    // 用于checkbox 选中使用
    private boolean checked;
}
