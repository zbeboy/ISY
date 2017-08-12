package top.zbeboy.isy.web.bean.data.science;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.Science;

/**
 * Created by lenovo on 2016-09-24.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ScienceBean extends Science {
    private String schoolName;
    private String collegeName;
    private String departmentName;
    private int schoolId;
    private int collegeId;
}
