package top.zbeboy.isy.web.bean.platform.role;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.Role;

/**
 * Created by lenovo on 2016-10-16.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class RoleBean extends Role {
    private Integer collegeId;
    private String  collegeName;
    private Integer schoolId;
    private String  schoolName;
    private Integer applicationId;
}
