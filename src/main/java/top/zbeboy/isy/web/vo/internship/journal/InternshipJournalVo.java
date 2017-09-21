package top.zbeboy.isy.web.vo.internship.journal;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by zbeboy on 2016/12/16.
 */
@Data
public class InternshipJournalVo {
    private String internshipJournalId;
    @NotNull
    @Size(max = 30)
    private String studentName;
    @NotNull
    @Size(max = 20)
    private String studentNumber;
    @NotNull
    @Size(max = 200)
    private String organize;
    @NotNull
    @Size(max = 30)
    private String schoolGuidanceTeacher;
    @NotNull
    @Size(max = 200)
    private String graduationPracticeCompanyName;
    @NotNull
    @Size(max = 65535)
    private String internshipJournalContent;
    @NotNull
    @Size(max = 65535)
    private String internshipJournalHtml;
    @NotNull
    private Date internshipJournalDate;
    private Timestamp createDate;
    @NotNull
    private Integer studentId;
    @NotNull
    private Integer staffId;
    @NotNull
    private String internshipReleaseId;
    private String internshipJournalWord;
    private Byte isSeeStaff;
}
