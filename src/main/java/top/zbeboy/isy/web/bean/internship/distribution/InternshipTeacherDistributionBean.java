package top.zbeboy.isy.web.bean.internship.distribution;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution;

/**
 * Created by zbeboy on 2016/11/21.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class InternshipTeacherDistributionBean extends InternshipTeacherDistribution {
    private String internshipTitle;
    private String schoolName;
    private String collegeName;
    private String departmentName;
    private String studentRealName;
    private String studentUsername;
    private String studentNumber;
    private String staffRealName;
    private String staffUsername;
    private String staffNumber;
    private String realName;
}
