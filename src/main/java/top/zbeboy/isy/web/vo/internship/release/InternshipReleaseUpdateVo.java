package top.zbeboy.isy.web.vo.internship.release;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2016/11/18.
 */
public class InternshipReleaseUpdateVo {
    @NotNull
    private String internshipReleaseId;
    @NotNull
    @Size(max = 100)
    private String releaseTitle;
    @NotNull
    private String teacherDistributionTime;
    @NotNull
    private String time;
    private Byte internshipReleaseIsDel;
    private String files;

    public String getInternshipReleaseId() {
        return internshipReleaseId;
    }

    public void setInternshipReleaseId(String internshipReleaseId) {
        this.internshipReleaseId = internshipReleaseId;
    }

    public String getReleaseTitle() {
        return releaseTitle;
    }

    public void setReleaseTitle(String releaseTitle) {
        this.releaseTitle = releaseTitle;
    }

    public String getTeacherDistributionTime() {
        return teacherDistributionTime;
    }

    public void setTeacherDistributionTime(String teacherDistributionTime) {
        this.teacherDistributionTime = teacherDistributionTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Byte getInternshipReleaseIsDel() {
        return internshipReleaseIsDel;
    }

    public void setInternshipReleaseIsDel(Byte internshipReleaseIsDel) {
        this.internshipReleaseIsDel = internshipReleaseIsDel;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }
}
