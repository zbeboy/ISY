package top.zbeboy.isy.web.vo.graduate.design.release;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2017-05-06.
 */
public class GraduationDesignReleaseUpdateVo {
    @NotNull
    @Size(max = 64)
    private String graduationDesignReleaseId;
    @NotNull
    @Size(max = 100)
    private String graduationDesignTitle;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
    @NotNull
    private String fillTeacherTime;
    private Byte graduationDesignIsDel;
    private String files;

    public String getGraduationDesignReleaseId() {
        return graduationDesignReleaseId;
    }

    public void setGraduationDesignReleaseId(String graduationDesignReleaseId) {
        this.graduationDesignReleaseId = graduationDesignReleaseId;
    }

    public String getGraduationDesignTitle() {
        return graduationDesignTitle;
    }

    public void setGraduationDesignTitle(String graduationDesignTitle) {
        this.graduationDesignTitle = graduationDesignTitle;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFillTeacherTime() {
        return fillTeacherTime;
    }

    public void setFillTeacherTime(String fillTeacherTime) {
        this.fillTeacherTime = fillTeacherTime;
    }

    public Byte getGraduationDesignIsDel() {
        return graduationDesignIsDel;
    }

    public void setGraduationDesignIsDel(Byte graduationDesignIsDel) {
        this.graduationDesignIsDel = graduationDesignIsDel;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }
}
