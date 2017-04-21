package top.zbeboy.isy.web.vo.data.science;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-09-24.
 */
public class ScienceVo {
    private Integer scienceId;
    @NotNull
    @Size(max = 200)
    private String scienceName;
    private Byte scienceIsDel;
    @NotNull
    @Min(1)
    private Integer departmentId;
    @NotNull
    @Size(max = 20)
    private String  scienceCode;

    public Integer getScienceId() {
        return scienceId;
    }

    public void setScienceId(Integer scienceId) {
        this.scienceId = scienceId;
    }

    public String getScienceName() {
        return scienceName;
    }

    public void setScienceName(String scienceName) {
        this.scienceName = scienceName;
    }

    public Byte getScienceIsDel() {
        return scienceIsDel;
    }

    public void setScienceIsDel(Byte scienceIsDel) {
        this.scienceIsDel = scienceIsDel;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getScienceCode() {
        return scienceCode;
    }

    public void setScienceCode(String scienceCode) {
        this.scienceCode = scienceCode;
    }
}
