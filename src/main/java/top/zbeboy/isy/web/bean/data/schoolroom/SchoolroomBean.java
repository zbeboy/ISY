package top.zbeboy.isy.web.bean.data.schoolroom;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.Schoolroom;

/**
 * Created by zbeboy on 2017/5/31.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class SchoolroomBean extends Schoolroom {
    private String schoolName;
    private String collegeName;
    private String buildingName;
    private int schoolId;
    private int collegeId;
}
