package top.zbeboy.isy.web.vo.data.academic;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/4/21.
 */
public class AcademicTitleVo {
    private Integer academicTitleId;
    @NotNull
    @Size(max = 30)
    private String  academicTitleName;

    public Integer getAcademicTitleId() {
        return academicTitleId;
    }

    public void setAcademicTitleId(Integer academicTitleId) {
        this.academicTitleId = academicTitleId;
    }

    public String getAcademicTitleName() {
        return academicTitleName;
    }

    public void setAcademicTitleName(String academicTitleName) {
        this.academicTitleName = academicTitleName;
    }
}
