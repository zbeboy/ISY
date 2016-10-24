package top.zbeboy.isy.web.vo.data.department;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by 杨逸云 on 2016/9/23.
 */
public class DepartmentVo {
    private Integer departmentId;
    @NotNull
    @Size(max = 200)
    private String departmentName;
    private Byte departmentIsDel;
    private Integer collegeId;

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Byte getDepartmentIsDel() {
        return departmentIsDel;
    }

    public void setDepartmentIsDel(Byte departmentIsDel) {
        this.departmentIsDel = departmentIsDel;
    }

    public Integer getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }
}
