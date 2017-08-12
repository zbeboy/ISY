package top.zbeboy.isy.web.bean.internship.release;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease;
import top.zbeboy.isy.domain.tables.pojos.Science;

import java.util.List;

/**
 * Created by lenovo on 2016-11-14.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class InternshipReleaseBean extends InternshipRelease {
    private String realName;
    private String departmentName;
    private String schoolName;
    private String collegeName;
    private String internshipTypeName;
    private List<Science> sciences;
    private int schoolId;
    private int collegeId;
    private String teacherDistributionStartTimeStr;
    private String teacherDistributionEndTimeStr;
    private String startTimeStr;
    private String endTimeStr;
    private String releaseTimeStr;

    // 实习审核 统计总数
    private int waitTotalData;
    private int passTotalData;
    private int failTotalData;
    private int basicApplyTotalData;
    private int companyApplyTotalData;
    private int basicFillTotalData;
    private int companyFillTotalData;

    // 实习统计 统计总数
    private int submittedTotalData;
    private int unsubmittedTotalData;
}
