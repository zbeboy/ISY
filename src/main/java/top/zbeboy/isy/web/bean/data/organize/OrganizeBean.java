package top.zbeboy.isy.web.bean.data.organize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.Organize;

/**
 * Created by lenovo on 2016-09-25.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class OrganizeBean extends Organize {
    private String schoolName;
    private String collegeName;
    private String departmentName;
    private String scienceName;
    private int schoolId;
    private int collegeId;
    private int departmentId;
}
