package top.zbeboy.isy.web.bean.data.department;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.Department;

/**
 * Created by lenovo on 2016/9/23.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DepartmentBean extends Department {
    private String schoolName;
    private String collegeName;
    private int schoolId;
}
