package top.zbeboy.isy.web.bean.internship.apply;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.InternshipApply;
import top.zbeboy.isy.domain.tables.pojos.Science;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by lenovo on 2016-11-29.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class InternshipApplyBean extends InternshipApply {
    private String internshipTitle;
    private Timestamp releaseTime;
    private String username;
    private String allowGrade;
    private Timestamp teacherDistributionStartTime;
    private Timestamp teacherDistributionEndTime;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte internshipReleaseIsDel;
    private Integer departmentId;
    private Integer internshipTypeId;
    private List<Science> sciences;
    private String teacherDistributionStartTimeStr;
    private String teacherDistributionEndTimeStr;
    private String startTimeStr;
    private String endTimeStr;
    private String releaseTimeStr;
    private String publisher;
    private String departmentName;
    private String schoolName;
    private String collegeName;
    private String internshipTypeName;
    private int schoolId;
    private int collegeId;
    private String fileId;
    private String originalFileName;
    private String ext;

    // 用于实习显示指导教师用
    private String  schoolGuidanceTeacher;
    private String  schoolGuidanceTeacherTel;
}
