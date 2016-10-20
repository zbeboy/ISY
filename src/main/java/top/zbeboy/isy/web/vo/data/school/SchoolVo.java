package top.zbeboy.isy.web.vo.data.school;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-09-19.
 */
public class SchoolVo {
    private Integer schoolId;
    @NotNull
    @Size(max = 200)
    private String schoolName;
    private Byte schoolIsDel;

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Byte getSchoolIsDel() {
        return schoolIsDel;
    }

    public void setSchoolIsDel(Byte schoolIsDel) {
        this.schoolIsDel = schoolIsDel;
    }

    @Override
    public String toString() {
        return "SchoolVo{" +
                "schoolId=" + schoolId +
                ", schoolName='" + schoolName + '\'' +
                ", schoolIsDel=" + schoolIsDel +
                '}';
    }
}
