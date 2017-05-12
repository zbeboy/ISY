package top.zbeboy.isy.web.vo.internship.regulate;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

/**
 * Created by lenovo on 2016-12-24.
 */
@Data
public class InternshipRegulateVo {
    private String internshipRegulateId;
    @NotNull
    @Size(max = 10)
    private String studentName;
    @NotNull
    @Size(max = 20)
    private String studentNumber;
    @NotNull
    @Size(max = 15)
    private String studentTel;
    @NotNull
    @Size(max = 200)
    private String internshipContent;
    @NotNull
    @Size(max = 200)
    private String internshipProgress;
    @NotNull
    @Size(max = 20)
    private String reportWay;
    @NotNull
    private Date reportDate;
    private String schoolGuidanceTeacher;
    private String tliy = "无";
    @NotNull
    private Integer studentId;
    @NotNull
    @Size(max = 64)
    private String internshipReleaseId;
    @NotNull
    private Integer staffId;
}
